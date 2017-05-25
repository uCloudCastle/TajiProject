package com.randal.aviana;

import android.os.SystemClock;
import android.view.KeyEvent;

/* Usage:
    RemoteControlUtils.init("1234", 2000);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (RemoteControlUtils.handleKeyEvent(keyCode)) {
            ...
            return true;
        }

        ...
    }
}
*/
public class RemoteControlUtils {

    private RemoteControlUtils(){
        throw new UnsupportedOperationException("DO NOT INSTANTIATE THIS CLASS");
    }

    private static char[] mKeyInput;
    private static long[] mHits;
    private static String KEY_WORD;
    private static long KEY_INTERVAL;

    public static void init(String key, int intervalMillis) {
        if (key.length() < 1) {
            return;
        }

        mKeyInput = new char[key.length()];
        mHits = new long[key.length()];
        KEY_INTERVAL = intervalMillis;
        KEY_WORD = key;
    }

    public static boolean handleKeyEvent(int keyCode) {
        if (KEY_WORD.length() < 1) {
            return false;
        }

        char keyStr = 0;
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_POUND) {
            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                keyStr = (char) ('0' + (keyCode - KeyEvent.KEYCODE_0));
            } else if (keyCode == KeyEvent.KEYCODE_STAR) {
                keyStr = '*';
            } else if (keyCode == KeyEvent.KEYCODE_POUND) {
                keyStr = '#';
            }

            System.arraycopy(mKeyInput, 1, mKeyInput, 0, mKeyInput.length - 1);
            mKeyInput[mKeyInput.length - 1] = keyStr;

            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        }

        if (mHits[0] >= SystemClock.uptimeMillis() - KEY_INTERVAL
                && String.valueOf(mKeyInput).equals(KEY_WORD)) {
            return true;
        }
        return false;
    }
}
