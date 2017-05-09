package com.jxlc.tajiproject.algorithm;

import com.jxlc.tajiproject.bean.TowerCraneInfo;

import java.util.List;

/**
 * Created by Randal on 2017-05-08.
 */

public class AntiCollisionAlgorithm {
    private List<TowerCraneInfo> mTCInfoList;
    private List<AntiCollisionListener> mListeners;

    private AntiCollisionAlgorithm(){}
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


}
