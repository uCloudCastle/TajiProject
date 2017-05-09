package com.jxlc.tajiproject.bean;

/**
 * Created by Randal on 2017-05-06.
 */

public class EnvironmentInfo {
    private float windSpeed;                       // M/s
    private float temperature;                     // °C
    private int constructionSiteWidth;             // M
    private int constructionSiteHeight;            // M

    private EnvironmentInfo(){}
    private static class SingletonHolder {
        static final EnvironmentInfo INSTANCE = new EnvironmentInfo();
    }
    public static EnvironmentInfo getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getConstructionSiteWidth() {
        return constructionSiteWidth;
    }

    public void setConstructionSiteWidth(int constructionSiteWidth) {
        this.constructionSiteWidth = constructionSiteWidth;
    }

    public int getConstructionSiteHeight() {
        return constructionSiteHeight;
    }

    public void setConstructionSiteHeight(int constructionSiteHeight) {
        this.constructionSiteHeight = constructionSiteHeight;
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
