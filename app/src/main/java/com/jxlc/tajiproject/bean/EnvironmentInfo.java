package com.jxlc.tajiproject.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Randal on 2017-05-06.
 */

public class EnvironmentInfo {
    private final static int INFO_CHANGED_ID = 3202;

    private List<InfoListener> mListeners;
    private float windSpeed;                       // M/s
    private float temperature;                     // °C
    private float constructionSiteWidth;             // M
    private float constructionSiteHeight;            // M

    private EnvironmentInfo(){
        mListeners = new ArrayList<>();
    }
    private static class SingletonHolder {
        static final EnvironmentInfo INSTANCE = new EnvironmentInfo();
    }
    public static EnvironmentInfo getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addListener(InfoListener l) {
        mListeners.add(l);
    }

    public void removeListener(InfoListener l) {
        mListeners.remove(l);
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(INFO_CHANGED_ID);
        }
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(INFO_CHANGED_ID);
        }
    }

    public float getConstructionSiteWidth() {
        return constructionSiteWidth;
    }

    public void setConstructionSiteWidth(float constructionSiteWidth) {
        this.constructionSiteWidth = constructionSiteWidth;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(INFO_CHANGED_ID);
        }
    }

    public float getConstructionSiteHeight() {
        return constructionSiteHeight;
    }

    public void setConstructionSiteHeight(float constructionSiteHeight) {
        this.constructionSiteHeight = constructionSiteHeight;
        for (InfoListener l : mListeners) {
            l.onInfoChanged(INFO_CHANGED_ID);
        }
    }

    @Override
    public String toString() {
        return "EnvironmentInfo{" +
                "windSpeed=" + windSpeed +
                ", temperature=" + temperature +
                ", constructionSiteWidth=" + constructionSiteWidth +
                ", constructionSiteHeight=" + constructionSiteHeight +
                '}';
    }
}
