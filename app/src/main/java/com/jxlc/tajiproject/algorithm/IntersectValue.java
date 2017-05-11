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

    boolean towerOneFFMark = false;
    boolean towerOneFBMark = false;
    boolean towerOneBFMark = false;
    boolean towerTwoFFMark = false;
    boolean towerTwoFBMark = false;
    boolean towerTwoBFMark = false;
}
