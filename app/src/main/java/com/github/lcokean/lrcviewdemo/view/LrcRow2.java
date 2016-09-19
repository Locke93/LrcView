package com.github.lcokean.lrcviewdemo.view;

/**
 * @author pengj
 * @version 4.3 on 2016/9/18
 */
public class LrcRow2 implements Comparable<LrcRow2> {
    public String content;
    public long timeStart;
    public long timeEnd;

    public LrcRow2(String text, long start, long end) {
        this.content = text;
        this.timeStart = start;
        this.timeEnd = end;
    }

    @Override
    public int compareTo(LrcRow2 lrcRow) {
        return (int) (timeStart - lrcRow.timeStart);
    }
}
