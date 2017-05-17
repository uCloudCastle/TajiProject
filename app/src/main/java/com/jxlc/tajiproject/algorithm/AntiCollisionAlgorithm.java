/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

public class AntiCollisionAlgorithm implements TowerCraneInfo.InfoListener {
    private List<TowerCraneInfo> mTCInfoList;
    private List<AntiCollisionListener> mListeners;
    private List<CheckChangedListener> mCheckListeners;
    private List<IntersectKey> mPairList;
    private HashMap<IntersectKey, IntersectValue> mPairMap;

    private float safeDistance_arm2arm = 3;           // M
    private float safeDistance_arm2rope = 5;          // M

    private Timer mTimer;
    private int curCheckId;
    private int interval = 1000;      // ms

    private AntiCollisionAlgorithm(){
        mTCInfoList = new ArrayList<>();
        mListeners = new ArrayList<>();
        mCheckListeners = new ArrayList<>();
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

    public void addCheckListener(CheckChangedListener l) {
        mCheckListeners.add(l);
    }

    public void removeCheckListener(CheckChangedListener l) {
        mCheckListeners.remove(l);
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

    public void setSafeDistance_arm2arm(float value) {
        safeDistance_arm2arm = value;
    }

    public void setSafeDistance_arm2rope(float value) {
        safeDistance_arm2rope = value;
    }

    // set current tower crane
    public void setCheckTowerId(int id) {
        TowerCraneInfo info = getTowerCraneInfoById(id);
        if (info == null) {
            return;
        }

        int old = curCheckId;
        curCheckId = id;
        for (CheckChangedListener listener : mCheckListeners) {
            listener.onCheckChanged(old, curCheckId);
        }
    }

    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                simulation();
                if (mPairMap.isEmpty()) {
                    return;
                }

                boolean intersect = false;
                for (Map.Entry<IntersectKey, IntersectValue> entry : mPairMap.entrySet()) {
                    IntersectKey key = entry.getKey();
                    IntersectValue value = entry.getValue();
                    int type = value.intersectType;
                    //LogUtils.d("key = " + key.toString() + " value = " + value.toString());

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

                    // both arm in Intersect, need check collision
                    if (intersect) {
                        checkCollision(entry);
                    }
                }
            }
        };
        mTimer.scheduleAtFixedRate(task, 0, interval);
    }

    // TODO: it's for demo
    private void simulation() {
        for (TowerCraneInfo info : mTCInfoList) {
            if (info.getAngle() >= 360) {
                info.setAngle(0);
            }
            info.setAngle(info.getAngle() + 3);

            if (info.getTrolleyDistance() >= 40) {
                info.setTrolleyDistance(20);
            }
            info.setTrolleyDistance(info.getTrolleyDistance() + 2);
        }
    }

    // TODO: done it
    // check higher tower rope safe range with lower tower distance
    private void checkCollision(Map.Entry<IntersectKey, IntersectValue> entry) {
        IntersectKey key = entry.getKey();
        IntersectValue value = entry.getValue();

        TowerCraneInfo higher = getTowerCraneInfoById(key.idOne);
        TowerCraneInfo lower = getTowerCraneInfoById(key.idTwo);
        if (higher == null || lower == null) {
            return;
        }
        if (higher.getArmToGroundHeight() < lower.getArmToGroundHeight()) {
            TowerCraneInfo temp = higher;
            higher = lower;
            lower = temp;
        }
//
//        if (higher.getArmToGroundHeight() - lower.getArmToGroundHeight() <= safeDistance_arm2arm) {
//            // 1. 如果吊臂高度差小于等于安全距离,则直接检测吊臂前端
//            float x = higher.getCoordinateX() + higher.getFrontArmLength();
//        } else {
//            // 2. 否则检测低位塔机到吊绳的安全距离
//            float j;
//        }
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

    /*
     * 前臂在前臂限制区状态回调
     */
    private boolean checkRuntimeAngleMF2F(IntersectKey key, IntersectValue value) {
        // master
        float angle = getTowerCraneInfoById(key.idOne).getAngle();
        if (checkIFAngleInRange(angle, value.towerOneFFAngleMin, value.towerOneFFAngleMax)) {
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
        if (checkIFAngleInRange(angle, value.towerTwoFFAngleMin, value.towerTwoFFAngleMax)) {
            if (!value.towerTwoFFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onFrontEnterIntersection(key.idTwo, key.idOne, true);
                }
                value.towerTwoFFMark = true;
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

    /*
     * 后臂在前臂限制区状态回调
     */
    private boolean checkRuntimeAngleMB2F(IntersectKey key, IntersectValue value) {
        // master
        float angle = getTowerCraneInfoById(key.idOne).getAngle();
        angle = convert2RearAngle(angle);
        if (checkIFAngleInRange(angle, value.towerOneBFAngleMin, value.towerOneBFAngleMax)) {
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
        if (checkIFAngleInRange(angle, value.towerTwoFBAngleMin, value.towerTwoFBAngleMax)) {
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

    /*
     * 前臂在后臂限制区状态回调
     */
    private boolean checkRuntimeAngleMF2B(IntersectKey key, IntersectValue value) {
        // master
        float angle = getTowerCraneInfoById(key.idOne).getAngle();
        if (checkIFAngleInRange(angle, value.towerOneFBAngleMin, value.towerOneFBAngleMax)) {
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
        angle = convert2RearAngle(angle);
        if (checkIFAngleInRange(angle, value.towerTwoBFAngleMin, value.towerTwoBFAngleMax)) {
            if (!value.towerTwoBFMark) {
                for (AntiCollisionListener listener : mListeners) {
                    listener.onRearEnterIntersection(key.idTwo, key.idOne);
                }
                value.towerTwoBFMark = true;
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

    /*
     * 角度检查函数
     * obtainAngle()的返回值可能为(-175.1, -120.9)
     * 或者是 (-52.3, 21.2)
     * 可以保证 min < max
     *
     * angle为塔机实时角度值,范围为[0,360)
     */
    private boolean checkIFAngleInRange(float angle, float min, float max) {

        if (min > 0 && max > 0) {
            return (angle >= min) && (angle <= max);
        } else if (min < 0 && max < 0) {
            angle -= 360;
            return (angle >= min) && (angle <= max);
        } else if (min <0 && max >= 0) {
            return ((angle >= min + 360) && (angle <= 360)) ||
                    ((angle >= 0) && (angle <= max));
        } else {
            LogUtils.e("error angle type!");
        }
        return false;
    }

    private float convert2RearAngle(float angle) {
        if (angle < 180) {   // [0,180)
            return angle + 180;
        } else {             // [180,360)
            return angle - 180;
        }
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
    public void onInfoChanged() {

    }

    @Override
    public void onPaintInfoChanged() {

    }

    @Override
    public void onStableInfoChanged() {
        updatePairMap();
    }
}
