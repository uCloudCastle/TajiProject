package com.jxlc.tajiproject.transmitter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.jxlc.tajiproject.bean.TowerCraneInfo;
import com.jxlc.tajiproject.transmitter.usbserial.UsbReceiver;
import com.jxlc.tajiproject.transmitter.usbserial.UsbService;
import com.randal.aviana.LogUtils;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Transmitter {
    private final static String DATA_SPLIT_SYMBOL = "#";

    private Context mContext;
    private UsbService usbService;
    private UsbReceiver usbReceiver;
    private UsbHandler mHandler;
    private Timer mTimer;
    private volatile static Transmitter sTransmitter;

    public static Transmitter getInstance(Context context) {
        if (sTransmitter == null) {
            synchronized (Transmitter.class) {
                if (sTransmitter == null) {
                    sTransmitter = new Transmitter(context.getApplicationContext());
                }
            }
        }
        return sTransmitter;
    }

    private Transmitter(Context context) {
        mContext = context;
        mHandler = new UsbHandler(context);
        usbReceiver = new UsbReceiver();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    public void start() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                TowerCraneInfo curInfo = AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo();
                String sendFav = towerCraneInfo2FormatString(curInfo);
                if (!sendFav.isEmpty() && usbService != null) {
                    usbService.write(sendFav.getBytes());
                }
            }
        };
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(mContext, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            mContext.startService(startService);
        }
        Intent bindingIntent = new Intent(mContext, service);
        mContext.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        mContext.registerReceiver(usbReceiver, filter);
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class UsbHandler extends Handler {
        private Context mContext;

        public UsbHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    LogUtils.d(data);
                    TowerCraneInfo info = formatString2TowerCraneInfo(data);
                    if (info != null) {
                        AntiCollisionAlgorithm.getInstance().updateTowerCrane(info);
                    }
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mContext, "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mContext, "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private String towerCraneInfo2FormatString(TowerCraneInfo info) {
        if (info == null) {
            return "";
        }
        return info.getIdentifier() + DATA_SPLIT_SYMBOL
                + info.getModelName() + DATA_SPLIT_SYMBOL
                + info.getCoordinateX() + DATA_SPLIT_SYMBOL
                + info.getCoordinateY() + DATA_SPLIT_SYMBOL
                + info.getFrontArmLength()+ DATA_SPLIT_SYMBOL
                + info.getRearArmLength() + DATA_SPLIT_SYMBOL
                + info.getTrolleyDistance() + DATA_SPLIT_SYMBOL
                + info.getArmToGroundHeight() + DATA_SPLIT_SYMBOL
                + info.getRopeLength() + DATA_SPLIT_SYMBOL
                + info.getAngle() + DATA_SPLIT_SYMBOL
                + info.isLiftWeightLimiterWorkFine() + DATA_SPLIT_SYMBOL
                + info.isLiftHeightLimiterWorkFine() + DATA_SPLIT_SYMBOL
                + info.isTorqueLimiterWorkFine() + DATA_SPLIT_SYMBOL
                + info.isOverstrokeLimiterWorkFine() + DATA_SPLIT_SYMBOL
                + info.isSlewingLimiterWorkFine();
    }

    private static TowerCraneInfo formatString2TowerCraneInfo(String formatData) {
        if (formatData == null) {
            return null;
        }
        String[] data = formatData.split("#");
        if (data.length < 15) {
            return null;
        }

        int identifier = Integer.valueOf(data[1]);
        String modelName = data[2];
        float coordinateX = Float.valueOf(data[3]);
        float coordinateY = Float.valueOf(data[4]);
        float frontArmLength = Float.valueOf(data[5]);
        float rearArmLength = Float.valueOf(data[6]);
        float armToGroundHeight = Float.valueOf(data[7]);
        float trolleyDistance = Float.valueOf(data[8]);
        float ropeLength = Float.valueOf(data[9]);
        float angle = Float.valueOf(data[10]);
        boolean liftWeightLimiter = data[11].equals("true");
        boolean liftHeightLimiter = data[12].equals("true");
        boolean torqueLimiter = data[13].equals("true");
        boolean overstrokeLimiter = data[14].equals("true");
        boolean slewingLimiter = data[15].equals("true");

        return new TowerCraneInfo(identifier, modelName, coordinateX, coordinateY,
                frontArmLength, rearArmLength, armToGroundHeight, trolleyDistance, ropeLength,
                angle, liftWeightLimiter, liftHeightLimiter, torqueLimiter, overstrokeLimiter,
                slewingLimiter);
    }
}