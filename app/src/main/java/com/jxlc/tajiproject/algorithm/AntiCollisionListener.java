package com.jxlc.tajiproject.algorithm;

/**
 * Created by Randal on 2017-05-08.
 */

interface AntiCollisionListener {
    void onEnterIntersection(int majorId, int affectedId);
    void onLeaveIntersection(int majorId, int affectedId);
}
