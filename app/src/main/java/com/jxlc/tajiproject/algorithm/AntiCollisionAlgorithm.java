package com.jxlc.tajiproject.algorithm;

import com.jxlc.tajiproject.bean.TowerCraneInfo;
import com.randal.aviana.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.jxlc.tajiproject.algorithm.IntersectValue.INTERSECT_TYPE_1REACH2;
import static com.jxlc.tajiproject.algorithm.IntersectValue.INTERSECT_TYPE_2REACH1;
import static com.jxlc.tajiproject.algorithm.IntersectValue.INTERSECT_TYPE_ONLYFRONT;
import static com.jxlc.tajiproject.algorithm.IntersectValue.INTERSECT_TYPE_REACHEACHOTHER;

/**
 * Created by Randal on 2017-05-08.
 */

public class AntiCollisionAlgorithm implements TowerCraneInfo.StableInfoListener {
    private List<TowerCraneInfo> mTCInfoList;
    private List<AntiCollisionListener> mListeners;
    private List<IntersectKey> mPairList;
    private HashMap<IntersectKey, IntersectValue> mPairMap;

    private Timer mTimer;
    private int interval = 1000;      // ms

    private AntiCollisionAlgorithm(){
        mTCInfoList = new ArrayList<>();
        mListeners = new ArrayList<>();
        mPairList = new ArrayList<>();
        mPairMap = new HashMap<>();
        mTimer = new Timer();
    }
    private static class SingletonHolder {
        static final AntiCollisionAlgorithm INSTANCE = new AntiCollisionAlgorithm();
    }
    public static AntiCollisionAlgorithm getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addListener(AntiCollisionListener l) {
        mListeners.add(l);
    }

    public void removeListener(AntiCollisionListener l) {
        mListeners.remove(l);
    }

    public List<TowerCraneInfo> getTCInfoList() {
        return mTCInfoList;
    }

    public void setTowerCraneList(List<TowerCraneInfo> list) {
        mTCInfoList = list;
        updatePairMap();
    }

    public void addTowerCrane(TowerCraneInfo info) {
        mTCInfoList.add(info);
        updatePairMap();
    }

    public void removeTowerCrane(TowerCraneInfo info) {
        mTCInfoList.remove(info);
        updatePairMap();
    }

    public boolean removeTowerCraneById(int id) {
        for (TowerCraneInfo info : mTCInfoList) {
            if (info.getIdentifier() == id) {
                mTCInfoList.remove(info);
                updatePairMap();
                return true;
            }
        }
        return false;
    }

    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (mPairMap.isEmpty()) {
                    return;
                }

                boolean intersect = false;
                for (Map.Entry<IntersectKey, IntersectValue> entry : mPairMap.entrySet()) {
                    IntersectKey key = entry.getKey();
                    IntersectValue value = entry.getValue();
                    int type = value.intersectType;
                    LogUtils.d("key = " + key + " value = " + value);

                    switch (type) {
                        case INTERSECT_TYPE_ONLYFRONT: {
                            intersect = checkRuntimeAngleMF2F(key, value);
                            break;
                        }
                        case INTERSECT_TYPE_2REACH1: {
                            boolean b1 = checkRuntimeAngleMF2F(key, value);
                            boolean b2 = checkRuntimeAngleMB2F(key, value);
                            intersect = (b1 || b2);
                            break;
                        }
                        case INTERSECT_TYPE_1REACH2: {
                            boolean b1 = checkRuntimeAngleMF2F(key, value);
                            boolean b2 = checkRuntimeAngleMF2B(key, value);
                            intersect = (b1 || b2);
                            break;
                        }
                        case INTERSECT_TYPE_REACHEACHOTHER: {
                            boolean b1 = checkRuntimeAngleMF2F(key, value);
                            boolean b2 = checkRuntimeAngleMB2F(key, value);
                            boolean b3 = checkRuntimeAngleMF2B(key, value);
                            intersect = (b1 || b2 || b3);
                            break;
                        }
                        default:
                            break;
                    }
                }

                if (!intersect) {
                    return;
                }
                checkCollision();
            }
        };
        mTimer.scheduleAtFixedRate(task, 0, interval);
    }

    public void stop() {
        mTimer.cancel();
    }

    // call when Tower Crane List changed
    // Or the stable data of one of them changed
    private void updatePairMap() {
        if (updateIntersectPairs() == 0) {
            mPairMap.clear();
            return;
        }
        generatePairMap();
    }

    private int updateIntersectPairs() {
        for (int i = 0; i < mTCInfoList.size(); ++i) {
            for (int j = i + 1; j < mTCInfoList.size(); ++j) {
                float dx = mTCInfoList.get(i).getCoordinateX() - mTCInfoList.get(j).getCoordinateX();
                float dy = mTCInfoList.get(i).getCoordinateY() - mTCInfoList.get(j).getCoordinateY();
                double dis = Math.sqrt(dx * dx + dy * dy);

                IntersectKey pair = new IntersectKey(mTCInfoList.get(i).getIdentifier(),
                        mTCInfoList.get(j).getIdentifier(), dis);
                if (dis < mTCInfoList.get(i).getFrontArmLength() + mTCInfoList.get(j).getFrontArmLength()) {
                    if (!mPairList.contains(pair)) {
                        mPairList.add(pair);
                    }
                } else {
                    if (mPairList.contains(pair)) {
                        mPairList.remove(pair);
                    }
                }
            }
        }
        return mPairList.size();
    }

    private void generatePairMap() {
        for (IntersectKey key : mPairList) {
            TowerCraneInfo info1 = getTowerCraneInfoById(key.idOne);
            TowerCraneInfo info2 = getTowerCraneInfoById(key.idTwo);

            float lenFB = info1.getFrontArmLength() + info2.getRearArmLength();
            float lenBF = info1.getRearArmLength() + info2.getFrontArmLength();
            double dis = key.distance;

            IntersectValue value = new IntersectValue();
            if (dis >= lenFB && dis >= lenBF) {                   // 前臂交叉
                value.intersectType = INTERSECT_TYPE_ONLYFRONT;
                putAngle2IntersectValue(value, info1, info2, true);
                mPairMap.put(key, value);
            } else if (dis >= lenFB && dis < lenBF) {            // #2 覆盖 #1 后臂圆
                value.intersectType = INTERSECT_TYPE_ONLYFRONT;
                putAngle2IntersectValue(value, info1, info2, true);
                value.intersectType = INTERSECT_TYPE_2REACH1;
                putAngle2IntersectValue(value, info1, info2, true);
                mPairMap.put(key, value);
            } else if (dis < lenFB && dis >= lenBF) {            // #1 覆盖 #2 后臂圆
                value.intersectType = INTERSECT_TYPE_ONLYFRONT;
                putAngle2IntersectValue(value, info1, info2, true);
                value.intersectType = INTERSECT_TYPE_1REACH2;
                putAngle2IntersectValue(value, info1, info2, true);
                mPairMap.put(key, value);
            } else {                                             // 相互覆盖后臂圆
                value.intersectType = INTERSECT_TYPE_ONLYFRONT;
                putAngle2IntersectValue(value, info1, info2, true);
                value.intersectType = INTERSECT_TYPE_2REACH1;
                putAngle2IntersectValue(value, info1, info2, true);
                value.intersectType = INTERSECT_TYPE_1REACH2;
                putAngle2IntersectValue(value, info1, info2, true);
                value.intersectType = INTERSECT_TYPE_REACHEACHOTHER;
                mPairMap.put(key, value);
            }

            for (AntiCollisionListener listener : mListeners) {
                listener.onHasIntersection(key.idOne, key.idTwo, value);
            }
        }
    }

    // which will according type to put master and the other tower crane value
    // so if isMaster is true, it will be execute twice
    private void putAngle2IntersectValue(IntersectValue value, TowerCraneInfo info1,
                                         TowerCraneInfo info2, boolean isMaster) {
        int comType = value.intersectType;
        if (!isMaster) {
            if (comType == INTERSECT_TYPE_2REACH1) comType = INTERSECT_TYPE_1REACH2;
            else if (comType == INTERSECT_TYPE_1REACH2) comType = INTERSECT_TYPE_2REACH1;
        }

        float x1 = info1.getCoordinateX();
        float x2 = info2.getCoordinateX();
        float y1 = info1.getCoordinateY();
        float y2 = info2.getCoordinateY();

        switch (comType) {
            case INTERSECT_TYPE_ONLYFRONT: {
                float L1 = info1.getFrontArmLength();
                float L2 = info2.getFrontArmLength();
                AngleSaver saver = new AngleSaver();
                obtainAngle(x1, x2, y1, y2, L1, L2, saver);
                if (isMaster) {
                    value.towerOneFFAngleMin = saver.min;
                    value.towerOneFFAngleMax = saver.max;
                } else {
                    value.towerTwoFFAngleMin = saver.min;
                    value.towerTwoFFAngleMax = saver.max;
                }
                break;
            }
            case INTERSECT_TYPE_2REACH1: {
                float L1 = info1.getRearArmLength();
                float L2 = info2.getFrontArmLength();
                AngleSaver saver = new AngleSaver();
                obtainAngle(x1, x2, y1, y2, L1, L2, saver);
                if (isMaster) {
                    value.towerOneBFAngleMin = saver.min;
                    value.towerOneBFAngleMax = saver.max;
                } else {
                    value.towerTwoBFAngleMin = saver.min;
                    value.towerTwoBFAngleMax = saver.max;
                }
                break;
            }
            case INTERSECT_TYPE_1REACH2: {
                float L1 = info1.getFrontArmLength();
                float L2 = info2.getRearArmLength();
                AngleSaver saver = new AngleSaver();
                obtainAngle(x1, x2, y1, y2, L1, L2, saver);
                if (isMaster) {
                    value.towerOneFBAngleMin = saver.min;
                    value.towerOneFBAngleMax = saver.max;
                } else {
                    value.towerTwoFBAngleMin = saver.min;
                    value.towerTwoFBAngleMax = saver.max;
                }
                break;
            }
            case INTERSECT_TYPE_REACHEACHOTHER: {
                break;
            }
            default:
                break;
        }

        if (isMaster) {
            putAngle2IntersectValue(value, info2, info1, false);
        }
    }

    private void obtainAngle(float x1, float x2, float y1, float y2, float L1, float L2, AngleSaver saver) {
        float angle_AB, angle_1, angle_2, angle_3;
        float x_m, y_m;
        float L_m, L_qie;
        x_m = x2 - x1;
        y_m = y2 - y1;
        L_m = (float)Math.sqrt(x_m * x_m + y_m * y_m);
        angle_AB = (float)Math.atan2(y_m, x_m);                                     //范围在(-PI,PI)

        if (L_m > L2) {
            L_qie = L_m * L_m - L2 * L2;
            if (L_qie > (L1 * L1)) {
                angle_2 = (float)Math.acos((L_m * L_m + L1 * L1 - L2 * L2) / (2 * L_m * L1));//范围在(0,PI)
                angle_3 = angle_2;
            } else {
                angle_1 = (float)Math.asin(L2 / L_m);
                angle_3 = angle_1;
            }
        } else {
            angle_2 = (float)Math.acos((L_m * L_m + L1 * L1 - L2 * L2) / (2 * L_m * L1));//范围在(0,PI)
            angle_3 = angle_2;
        }

        saver.min = (angle_AB - angle_3) * 180 / (float) Math.PI;
        saver.max = (angle_AB + angle_3) * 180 / (float) Math.PI;
    }

    private boolean checkRuntimeAngleMF2F(IntersectKey key, IntersectValue value) {
        // master
        float angle = getTowerCraneInfoById(key.idOne).getAngle();
        if (value.towerOneFFAngleMin <= angle && angle < value.towerOneFFAngleMax) {
            if (!value.towerOneFFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontEnterIntersection(key.idOne, key.idTwo, true);
                }
                value.towerOneFFMark = true;
            }
        } else {
            if (value.towerOneFFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontLeaveIntersection(key.idOne, key.idTwo, true);
                }
                value.towerOneFFMark = false;
            }
        }

        // flower
        angle = getTowerCraneInfoById(key.idTwo).getAngle();
        if (value.towerTwoFFAngleMin <= angle && angle < value.towerTwoFFAngleMax) {
            if (!value.towerTwoFFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontEnterIntersection(key.idTwo, key.idOne, true);
                }
                value.towerOneFFMark = true;
            }
        } else {
            if (value.towerTwoFFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontLeaveIntersection(key.idTwo, key.idOne, true);
                }
                value.towerTwoFFMark = false;
            }
        }

        return (value.towerOneFFMark && value.towerTwoFFMark);
    }

    private boolean checkRuntimeAngleMB2F(IntersectKey key, IntersectValue value) {
        // master
        float angle = getTowerCraneInfoById(key.idOne).getAngle();
        if (value.towerOneBFAngleMin <= angle && angle < value.towerOneBFAngleMax) {
            if (!value.towerOneBFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onRearEnterIntersection(key.idOne, key.idTwo);
                }
                value.towerOneBFMark = true;
            }
        } else {
            if (value.towerOneBFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onRearLeaveIntersection(key.idOne, key.idTwo);
                }
                value.towerOneBFMark = false;
            }
        }

        // flower
        angle = getTowerCraneInfoById(key.idTwo).getAngle();
        if (value.towerTwoFBAngleMin <= angle && angle < value.towerTwoFBAngleMax) {
            if (!value.towerTwoFBMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontEnterIntersection(key.idTwo, key.idOne, false);
                }
                value.towerTwoFBMark = true;
            }
        } else {
            if (value.towerTwoFBMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontLeaveIntersection(key.idTwo, key.idOne, false);
                }
                value.towerTwoFBMark = false;
            }
        }
        return (value.towerOneBFMark && value.towerTwoFBMark);
    }

    private boolean checkRuntimeAngleMF2B(IntersectKey key, IntersectValue value) {
        // master
        float angle = getTowerCraneInfoById(key.idOne).getAngle();
        if (value.towerOneFBAngleMin <= angle && angle < value.towerOneFBAngleMax) {
            if (!value.towerOneFBMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontEnterIntersection(key.idOne, key.idTwo, false);
                }
                value.towerOneFBMark = true;
            }
        } else {
            if (value.towerOneFBMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontLeaveIntersection(key.idOne, key.idTwo, false);
                }
                value.towerOneFBMark = false;
            }
        }

        // flower
        angle = getTowerCraneInfoById(key.idTwo).getAngle();
        if (value.towerTwoBFAngleMin <= angle && angle < value.towerTwoBFAngleMax) {
            if (!value.towerTwoBFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onRearEnterIntersection(key.idTwo, key.idOne);
                }
                value.towerOneBFMark = true;
            }
        } else {
            if (value.towerTwoBFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onRearLeaveIntersection(key.idTwo, key.idOne);
                }
                value.towerTwoBFMark = false;
            }
        }

        return (value.towerOneFBMark && value.towerTwoBFMark);
    }

    private void checkCollision() {

    }

    private TowerCraneInfo getTowerCraneInfoById(int id) {
        for (TowerCraneInfo info : mTCInfoList) {
            if(info.getIdentifier() == id) {
                return info;
            }
        }
        return null;
    }

    @Override
    public void onStableInfoChanged() {
        updatePairMap();
    }
}
