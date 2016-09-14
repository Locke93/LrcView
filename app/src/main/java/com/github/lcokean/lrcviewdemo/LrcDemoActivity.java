package com.github.lcokean.lrcviewdemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;

import com.github.lcokean.lrcviewdemo.view.DefaultLrcBuilder;
import com.github.lcokean.lrcviewdemo.view.ILrcBuilder;
import com.github.lcokean.lrcviewdemo.view.ILrcView;
import com.github.lcokean.lrcviewdemo.view.LrcRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LrcDemoActivity extends Activity {

    public final static String TAG = "MainActivity";
    ILrcView mLrcView;
    private int mPalyTimerDuration = 1000;
    private Timer mTimer;
    private TimerTask mTask;

    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                Result += line + "\r\n";
            }
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc);
        mLrcView = (ILrcView) findViewById(R.id.lrc_view);
        //file:///android_asset/test.lrc;
        String lrc = getFromAssets("test.lrc");
        Log.d(TAG, "lrc:" + lrc);

        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrc);

        mLrcView.setLrc(rows);
        beginLrcPlay();

        mLrcView.setListener(new ILrcView.LrcViewListener() {

            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (mPlayer != null) {
                    Log.d(TAG, "onLrcSeeked:" + row.time);
                    mPlayer.seekTo((int) row.time);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }


    MediaPlayer mPlayer;

    public void beginLrcPlay() {

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getAssets().openFd("m.mp3").getFileDescriptor());
            mPlayer.setOnPreparedListener(new OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "onPrepared");
                    mp.start();
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTask = new LrcTask();
                        mTimer.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration);
                    }
                }
            });
            mPlayer.setOnCompletionListener(new OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    stopLrcPlay();
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopLrcPlay() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    class LrcTask extends TimerTask {

        long beginTime = -1;

        @Override
        public void run() {
            if (beginTime == -1) {
                beginTime = System.currentTimeMillis();
            }

            final long timePassed = mPlayer.getCurrentPosition();
            LrcDemoActivity.this.runOnUiThread(new Runnable() {

                public void run() {
                    mLrcView.seekLrcToTime(timePassed);
                }
            });

        }
    }

    ;
}
