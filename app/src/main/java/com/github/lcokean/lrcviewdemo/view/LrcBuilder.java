/**
 * douzifly @Aug 10, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.github.lcokean.lrcviewdemo.view;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * default lrc builder,convert raw lrc string to lrc rows
 */
public class LrcBuilder {

    static final String TAG = "LrcBuilder";

    public List<LrcRow> getLrcRows(String rawLrc) {
        Log.d(TAG, "getLrcRows by rawString");
        if (rawLrc == null || rawLrc.length() == 0) {
            Log.e(TAG, "getLrcRows rawLrc null or empty");
            return null;
        }
        StringReader reader = new StringReader(rawLrc);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        List<LrcRow> rows = new ArrayList<>();
        try {
            do {
                line = br.readLine();
                Log.d(TAG, "lrc raw line:" + line);
                if (line != null && line.length() > 0) {
                    List<LrcRow> lrcRows = createRows(line);
                    if (lrcRows != null && lrcRows.size() > 0) {
                        for (LrcRow row : lrcRows) {
                            rows.add(row);
                        }
                    }
                }

            } while (line != null);
            if (rows.size() > 0) {
                // sort by time:
                Collections.sort(rows);
            }
            for (int i = 0; i < rows.size(); i++) {
                if (i < rows.size() - 1) {
                    rows.get(i).timeEnd = rows.get(i + 1).timeStart;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "parse exceptioned:" + e.getMessage());
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
        return rows;
    }

    /**
     * create LrcRows by standard Lrc Line , if not standard lrc line,
     * return false<br />
     * [00:00:20] balabalabalabala
     */
    public List<LrcRow> createRows(String standardLrcLine) {
        try {
            if (standardLrcLine.indexOf("[") != 0 || standardLrcLine.indexOf("]") != 9) {
                return null;
            }
            int lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]");
            String content = standardLrcLine.substring(lastIndexOfRightBracket + 1, standardLrcLine.length());

            // times [mm:ss.SS][mm:ss.SS] -> *mm:ss.SS**mm:ss.SS*
            String times = standardLrcLine.substring(0, lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-");
            String arrTimes[] = times.split("-");
            List<LrcRow> listTimes = new ArrayList<LrcRow>();
            for (String temp : arrTimes) {
                if (temp.trim().length() == 0) {
                    continue;
                }
                LrcRow lrcRow = new LrcRow(content, timeConvert(temp), 0L);
                listTimes.add(lrcRow);
            }
            return listTimes;
        } catch (Exception e) {
            return null;
        }
    }

    private static long timeConvert(String timeString) {
        timeString = timeString.replace('.', ':');
        String[] times = timeString.split(":");
        // mm:ss:SS
        return Integer.valueOf(times[0]) * 60 * 1000 +
                Integer.valueOf(times[1]) * 1000 +
                Integer.valueOf(times[2]);
    }
}
