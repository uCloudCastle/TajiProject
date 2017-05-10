package com.jxlc.tajiproject.algorithm;

/**
 * Created by Randal on 2017-05-08.
 */

interface AntiCollisionListener {
    void onHasIntersection(int id1, int id2, int type);
    void onEnterIntersection(int majorId, int affectedId);
    void onLeaveIntersection(int majorId, int affectedId);
}
