package com.jxlc.tajiproject.algorithm;

/**
 * Created by Randal on 2017-05-08.
 */

public interface AntiCollisionListener {
    void onHasIntersection(int id1, int id2, IntersectValue value);

    void onFrontEnterIntersection(int majorId, int affectedId, boolean isOutCircle);
    void onRearEnterIntersection(int majorId, int affectedId);
    void onFrontLeaveIntersection(int majorId, int affectedId, boolean isOutCircle);
    void onRearLeaveIntersection(int majorId, int affectedId);

    void onCollisionComing(int id1, int id2);
}
