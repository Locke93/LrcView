package com.github.lcokean.lrcviewdemo.view;

/**
 * @author pengj
 * @version 4.3 on 2016/10/13
 */

public class LinearLrcInterpolator implements LrcInterpolator {

    @Override
    public float getLrcRowRate(LrcRow currLrc, float rate) {
        return rate * 1.0f;
    }

}
