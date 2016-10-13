package com.github.lcokean.lrcview.library;

/**
 * @author pengj
 * @version 4.3 on 2016/9/18
 */
public class LrcRow implements Comparable<LrcRow> {
    public String content;
    public long timeStart;
    public long timeEnd;

    public LrcRow(String text, long start, long end) {
        this.content = text;
        this.timeStart = start;
        this.timeEnd = end;
    }

    @Override
    public int compareTo(LrcRow lrcRow) {
        return (int) (timeStart - lrcRow.timeStart);
    }
}
