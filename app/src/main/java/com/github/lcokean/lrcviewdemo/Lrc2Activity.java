package com.github.lcokean.lrcviewdemo;

import android.app.Activity;
import android.os.Bundle;

import com.github.lcokean.lrcviewdemo.view.MyLrcView;

import java.util.ArrayList;

public class Lrc2Activity extends Activity {

    MyLrcView myLrcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc2);

        myLrcView = (MyLrcView) findViewById(R.id.myLrcView);
        ArrayList<String> list = new ArrayList<>();
        list.add("你是我天边最美的云彩你是我天边最美的云彩你是我天边最美的云彩");
        list.add("让我用心把你留下来");
        list.add("留下来");
        list.add("悠悠的唱着最炫名族风,悠悠的唱着最炫名族风,悠悠的唱着最炫名族风");
        list.add("你是我天边最美的云彩你是我天边最美的云彩你是我天边最美的云彩");
        list.add("让我用心把你留下来");
        list.add("留下来");
        list.add("悠悠的唱着最炫名族风,悠悠的唱着最炫名族风,悠悠的唱着最炫名族风");
        list.add("你是我天边最美的云彩你是我天边最美的云彩你是我天边最美的云彩");
        list.add("让我用心把你留下来");
        list.add("留下来");
        list.add("悠悠的唱着最炫名族风,悠悠的唱着最炫名族风,悠悠的唱着最炫名族风");
        list.add("你是我天边最美的云彩你是我天边最美的云彩你是我天边最美的云彩");
        list.add("让我用心把你留下来");
        list.add("留下来");
        list.add("悠悠的唱着最炫名族风,悠悠的唱着最炫名族风,悠悠的唱着最炫名族风");
        myLrcView.setLrc(list);

        myLrcView.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLrcView.start();
            }
        }, 500);
    }
}
