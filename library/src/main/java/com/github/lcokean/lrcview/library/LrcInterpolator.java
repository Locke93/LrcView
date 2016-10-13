package com.github.lcokean.lrcview.library;

/**
 * @author pengj
 * @version 4.3 on 2016/10/13
 */

public interface LrcInterpolator {

    /**
     * @param currLrc 当前歌词
     * @param rate    当前已播放时间比例,取值范围[0,1)
     * @return 歌词显示比例, 取值范围[0, 1)
     */
    float getLrcRowRate(LrcRow currLrc, float rate);

}
