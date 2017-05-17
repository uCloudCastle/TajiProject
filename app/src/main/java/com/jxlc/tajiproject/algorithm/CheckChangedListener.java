package com.jxlc.tajiproject.algorithm;

/**
 * Created by randal on 2017/5/17.
 */

public interface CheckChangedListener {
    // called when checked tower from id_1 to id_2
    void onCheckChanged(int oldId, int newId);

    // called when checked tower data changed
    void onCheckedDataChanged();
}
