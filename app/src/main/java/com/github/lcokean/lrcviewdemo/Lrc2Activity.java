package com.github.lcokean.lrcviewdemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.github.lcokean.lrcviewdemo.view.DefaultLrcBuilder2;
import com.github.lcokean.lrcviewdemo.view.LrcRow2;
import com.github.lcokean.lrcviewdemo.view.MyLrcView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Lrc2Activity extends Activity {

    MyLrcView myLrcView;
    private long mFlagTime, mPlayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc2);

        myLrcView = (MyLrcView) findViewById(R.id.myLrcView);
        String lrc = getFromAssets("test.lrc");
        DefaultLrcBuilder2 builder = new DefaultLrcBuilder2();
        List<LrcRow2> rows = builder.getLrcRows(lrc);
        myLrcView.setLrc(rows);

        findViewById(R.id.button_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLrcView.reset();
                mPlayer.reset();
                mPlayTime = 0;
            }
        });
        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer != null && !mPlayer.isPlaying()) {
                    mPlayer.start();
                    myLrcView.start();
                    mFlagTime = SystemClock.elapsedRealtime();
                } else {
                    beginMusicPlay();
                }
            }
        });
        findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.pause();
                mPlayTime += SystemClock.elapsedRealtime() - mFlagTime;
                myLrcView.stop(mPlayTime);
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
                    mFlagTime = SystemClock.elapsedRealtime();
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    myLrcView.reset();
                    mPlayTime = 0;
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

    }
}
