package com.jxlc.tajiproject.algorithm;

/**
 * Created by Randal on 2017-05-10.
 */

public class IntersectValue {
    public static final int INTERSECT_TYPE_ONLYFRONT = 1;
    public static final int INTERSECT_TYPE_2REACH1 = 2;
    public static final int INTERSECT_TYPE_1REACH2 = 3;
    public static final int INTERSECT_TYPE_REACHEACHOTHER = 4;

    int intersectType;
    float towerOneFFAngleMin;
    float towerOneFFAngleMax;
    float towerOneFBAngleMin;
    float towerOneFBAngleMax;
    float towerOneBFAngleMin;
    float towerOneBFAngleMax;
    float towerTwoFFAngleMin;
    float towerTwoFFAngleMax;
    float towerTwoFBAngleMin;
    float towerTwoFBAngleMax;
    float towerTwoBFAngleMin;
    float towerTwoBFAngleMax;

    // set Mark If this tower crane in Intersect Area now
    boolean towerOneFFMark = false;
    boolean towerOneFBMark = false;
    boolean towerOneBFMark = false;
    boolean towerTwoFFMark = false;
    boolean towerTwoFBMark = false;
    boolean towerTwoBFMark = false;

    @Override
    public String toString() {
        return "IntersectValue{" +
                "intersectType=" + intersectType +
                ", towerOneFFAngleMin=" + towerOneFFAngleMin +
                ", towerOneFFAngleMax=" + towerOneFFAngleMax +
                ", towerOneFBAngleMin=" + towerOneFBAngleMin +
                ", towerOneFBAngleMax=" + towerOneFBAngleMax +
                ", towerOneBFAngleMin=" + towerOneBFAngleMin +
                ", towerOneBFAngleMax=" + towerOneBFAngleMax +
                ", towerTwoFFAngleMin=" + towerTwoFFAngleMin +
                ", towerTwoFFAngleMax=" + towerTwoFFAngleMax +
                ", towerTwoFBAngleMin=" + towerTwoFBAngleMin +
                ", towerTwoFBAngleMax=" + towerTwoFBAngleMax +
                ", towerTwoBFAngleMin=" + towerTwoBFAngleMin +
                ", towerTwoBFAngleMax=" + towerTwoBFAngleMax +
                ", towerOneFFMark=" + towerOneFFMark +
                ", towerOneFBMark=" + towerOneFBMark +
                ", towerOneBFMark=" + towerOneBFMark +
                ", towerTwoFFMark=" + towerTwoFFMark +
                ", towerTwoFBMark=" + towerTwoFBMark +
                ", towerTwoBFMark=" + towerTwoBFMark +
                '}';
    }
}
