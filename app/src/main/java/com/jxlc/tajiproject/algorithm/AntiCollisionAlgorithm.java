package com.jxlc.tajiproject.algorithm;

import com.jxlc.tajiproject.bean.TowerCraneInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Randal on 2017-05-08.
 */

public class AntiCollisionAlgorithm {
    private static final String INTERSECT_TYPE_ONLYFRONT = "1";
    private static final String INTERSECT_TYPE_2REACH1 = "2";
    private static final String INTERSECT_TYPE_1REACH2 = "3";
    private static final String INTERSECT_TYPE_REACHEACHOTHER = "4";

    private List<TowerCraneInfo> mTCInfoList;
    private List<AntiCollisionListener> mListeners;
    private List<IntersectPair> mPairList;
    private HashMap<IntersectPair, String> mPairMap;

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
        for (Map.Entry<IntersectPair, String> entry : mPairMap.entrySet()) {
            //computeMinMaxAngle();
        }




        // monitorAngle
        // checkPengzhuang.
    }

    private int updateIntersectPairs() {
        for (int i = 0; i < mTCInfoList.size(); ++i) {
            for (int j = i + 1; j < mTCInfoList.size(); ++j) {
                float dx = mTCInfoList.get(i).getCoordinateX() - mTCInfoList.get(j).getCoordinateX();
                float dy = mTCInfoList.get(i).getCoordinateY() - mTCInfoList.get(j).getCoordinateY();
                double dis = Math.sqrt(dx * dx + dy * dy);

                IntersectPair pair = new IntersectPair(mTCInfoList.get(i).getIdentifier(),
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
        for (IntersectPair p : mPairList) {
            TowerCraneInfo info1 = getTowerCraneInfoById(p.idOne);
            TowerCraneInfo info2 = getTowerCraneInfoById(p.idTwo);

            float lenFB = info1.getFrontArmLength() + info2.getRearArmLength();
            float lenBF = info1.getRearArmLength() + info2.getFrontArmLength();
            double dis = p.distance;

            int type = 0;
            if (dis >= lenFB && dis >= lenBF) {                   // 前臂交叉
                type = 1;
                mPairMap.put(p, INTERSECT_TYPE_ONLYFRONT);
            } else if (dis >= lenFB && dis < lenBF) {            // #2 覆盖 #1 后臂圆
                type = 2;
                mPairMap.put(p, INTERSECT_TYPE_2REACH1);
            } else if (dis < lenFB && dis >= lenBF) {            // #1 覆盖 #2 后臂圆
                type = 3;
                mPairMap.put(p, INTERSECT_TYPE_1REACH2);
            } else {                                             // 相互覆盖后臂圆
                type = 4;
                mPairMap.put(p, INTERSECT_TYPE_REACHEACHOTHER);
            }

            for (AntiCollisionListener listener : mListeners) {
                listener.onHasIntersection(p.idOne, p.idTwo, type);
            }
        }
    }

    private void computeMinMaxAngle(int type, float x1, float y1,
                                    float x2, float y2, float len1, float len2) {
        switch (type) {
            case 1: {

                break;
            }
            case 2: {
                break;
            }
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

}
