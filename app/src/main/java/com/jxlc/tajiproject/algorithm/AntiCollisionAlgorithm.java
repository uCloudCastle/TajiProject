package com.jxlc.tajiproject.algorithm;

import com.jxlc.tajiproject.bean.TowerCraneInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.type;
import static com.jxlc.tajiproject.algorithm.IntersectValue.INTERSECT_TYPE_1REACH2;
import static com.jxlc.tajiproject.algorithm.IntersectValue.INTERSECT_TYPE_2REACH1;
import static com.jxlc.tajiproject.algorithm.IntersectValue.INTERSECT_TYPE_ONLYFRONT;
import static com.jxlc.tajiproject.algorithm.IntersectValue.INTERSECT_TYPE_REACHEACHOTHER;

/**
 * Created by Randal on 2017-05-08.
 */

public class AntiCollisionAlgorithm {
    private List<TowerCraneInfo> mTCInfoList;
    private List<AntiCollisionListener> mListeners;
    private List<IntersectKey> mPairList;
    private HashMap<IntersectKey, IntersectValue> mPairMap;

    private AntiCollisionAlgorithm(){
        mTCInfoList = new ArrayList<>();
        mListeners = new ArrayList<>();
        mPairList = new ArrayList<>();
        mPairMap = new HashMap<>();
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

    public void setTowerCraneList(List<TowerCraneInfo> list) {
        mTCInfoList = list;
    }

    public void addTowerCrane(TowerCraneInfo info) {
        mTCInfoList.add(info);
    }

    public boolean removeTowerCrane(TowerCraneInfo info) {
        return mTCInfoList.remove(info);
    }

    public boolean removeTowerCraneById(int id) {
        for (TowerCraneInfo info : mTCInfoList) {
            if (info.getIdentifier() == id) {
                mTCInfoList.remove(info);
                return true;
            }
        }
        return false;
    }

    public void run() {
        if (updateIntersectPairs() == 0) {
            return;
        }

        generatePairMap();
        // generateMinMaxIntersectAngle




        // monitorAngle
        // checkPengzhuang.
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

            if (dis >= lenFB && dis >= lenBF) {                   // 前臂交叉
                IntersectValue value = new IntersectValue();
                value.intersectType = INTERSECT_TYPE_ONLYFRONT;
                putAngle2IntersectValue(value, info1, info2, true);
                mPairMap.put(key, value);
            } else if (dis >= lenFB && dis < lenBF) {            // #2 覆盖 #1 后臂圆

            } else if (dis < lenFB && dis >= lenBF) {            // #1 覆盖 #2 后臂圆
            } else {                                             // 相互覆盖后臂圆
            }

            for (AntiCollisionListener listener : mListeners) {
                listener.onHasIntersection(key.idOne, key.idTwo, type);
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
            if (comType == INTERSECT_TYPE_1REACH2) comType = INTERSECT_TYPE_2REACH1;
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
                    value.towerOneFFAngleMin = saver.min;
                    value.towerOneFFAngleMax = saver.max;
                } else {
                    value.towerTwoFFAngleMin = saver.min;
                    value.towerTwoFFAngleMax = saver.max;
                }
                break;
            }
            case INTERSECT_TYPE_1REACH2: {
                break;
            }
            case INTERSECT_TYPE_REACHEACHOTHER: {
                break;
            }
            default:
                break;
        }

        if (isMaster) {
            isMaster = false;
            putAngle2IntersectValue(value, info1, info2, isMaster);
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

    private TowerCraneInfo getTowerCraneInfoById(int id) {
        for (TowerCraneInfo info : mTCInfoList) {
            if(info.getIdentifier() == id) {
                return info;
            }
        }
        return null;
    }

}
