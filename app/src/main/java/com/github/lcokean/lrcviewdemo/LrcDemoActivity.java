package com.github.lcokean.lrcviewdemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.github.lcokean.lrcviewdemo.view.LrcBuilder;
import com.github.lcokean.lrcviewdemo.view.LrcRow;
import com.github.lcokean.lrcviewdemo.view.LrcView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class LrcDemoActivity extends Activity {

    LrcView myLrcView;
    //private TimeClock mTimeClock = new TimeClock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc);

        myLrcView = (LrcView) findViewById(R.id.myLrcView);
        String lrc = getFromAssets("test.lrc");
        LrcBuilder builder = new LrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrc);
        myLrcView.setLrc(rows);

        findViewById(R.id.button_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer != null) {
                    myLrcView.reset();
                    mPlayer.reset();
                    //mTimeClock.reset();
                    mPlayer = null;
                }
            }
        });
        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer == null) {
                    beginMusicPlay();
                } else if (!mPlayer.isPlaying()) {
                    try {
                        mPlayer.start();
                        myLrcView.start();
                        // mTimeClock.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.pause();
                    //mTimeClock.pause();
                    myLrcView.pause(mPlayer.getCurrentPosition());
                }
            }
        });
    }

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

    MediaPlayer mPlayer;

    public void beginMusicPlay() {

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getAssets().openFd("m.mp3").getFileDescriptor());
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    myLrcView.start();
                    //mTimeClock.start();
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    myLrcView.reset();
                    // mTimeClock.reset();
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        super.onDestroy();
    }
}
