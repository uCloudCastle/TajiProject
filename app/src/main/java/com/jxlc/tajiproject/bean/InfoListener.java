package com.jxlc.tajiproject.bean;

/**
 * Created by Randal on 2017-05-17.
 */

public interface InfoListener {
    // for any info changed call back
    void onInfoChanged(int id);

    // for algorithm
    void onStableInfoChanged(int id);

    // for painter
    void onPaintInfoChanged(int id);

    // for layout
    void onLayoutInfoChanged(int id);
}
