package com.github.lcokean.lrcviewdemo;

import android.os.SystemClock;

/**
 * 计时器
 *
 * @version 4.3 on 2016/10/12
 */

public class TimeClock {

    private static final int STATUS_STOP = 0;
    private static final int STATUS_START = 1;
    private static final int STATUS_PAUSE = 2;

    private long mTime = 0, mFlagTime = 0;
    private int mStatus = STATUS_STOP;

    public void start() {
        if (mStatus != STATUS_START) {
            mFlagTime = SystemClock.elapsedRealtime();
            mStatus = STATUS_START;
        }
    }

    public void pause() {
        if (mStatus == STATUS_START) {
            mTime += SystemClock.elapsedRealtime() - mFlagTime;
            mFlagTime = SystemClock.elapsedRealtime();
            mStatus = STATUS_PAUSE;
        }
    }

    public void stop() {
        if (mStatus == STATUS_START) {
            mTime += SystemClock.elapsedRealtime() - mFlagTime;
            mFlagTime = SystemClock.elapsedRealtime();
        }
        mStatus = STATUS_STOP;
    }

    public void reset() {
        mTime = 0;
        mStatus = STATUS_STOP;
    }

    public long getTime() {
        if (mStatus == STATUS_START) {
            mTime += SystemClock.elapsedRealtime() - mFlagTime;
            mFlagTime = SystemClock.elapsedRealtime();
        }
        return mTime;
    }

}
