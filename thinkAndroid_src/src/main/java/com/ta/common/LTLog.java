package com.ta.common;

/**
 * Created by wenchao on 2016/2/29.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class LTLog {

    private static Boolean          mLogSwitch       = true;
    private static Boolean          mLogWriteToFile  = true;
    private static LogType          mLogType         = LogType.v;
    private static String           mLogPath         = "/mnt/sdcard/";
    private static int              mLogFileSaveDays = 0;
    private static String           mLogFileName     = "log.txt";
    private static SimpleDateFormat mLogSdf          = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static SimpleDateFormat mLogFile         = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    // this instance can be used for multi-threads and FileWriter itself is
    // thread-safe
    private static FileWriter mFileWriter = null;

    public static enum LogType {
        v(0), // log info
        d(1), // debug info
        i(2), // app info
        w(3), // warning info
        e(4); // error info

        private int type;

        public int getType() {
            return type;
        }

        LogType(int tp) {
            type = tp;
        }
    };

    public static void setLogType(LogType type) {
        mLogType = type;
    }

    public static void setLogFileName(String fileName) {
        mLogFileName = fileName;
    }

    public static void launchLog(Boolean isSet) {
        mLogSwitch = isSet;
    }

    public static void writeLogToFile(Boolean isWrite) {
        mLogWriteToFile = isWrite;
    }

    public static void setLifetimeForLogfiles(int lifetime) {
        mLogFileSaveDays = lifetime;
    }

    public static void setLogPath(String path) {
        mLogPath = path;
    }

    public static void w(String tag, String text) {
        log(tag, text, 'w');
    }

    public static void e(String tag, String text) {
        log(tag, text, 'e');
    }

    public static void d(String tag, String text) {
        log(tag, text, 'd');
    }

    public static void i(String tag, String text) {
        log(tag, text, 'i');
    }

    public static void v(String tag, String text) {
        log(tag, text, 'v');
    }

    public static void w(String tag, Object msg) {
        log(tag, msg.toString(), 'w');
    }

    public static void e(String tag, Object msg) {
        log(tag, msg.toString(), 'e');
    }

    public static void d(String tag, Object msg) {
        log(tag, msg.toString(), 'd');
    }

    public static void i(String tag, Object msg) {
        log(tag, msg.toString(), 'i');
    }

    public static void v(String tag, Object msg) {
        log(tag, msg.toString(), 'v');
    }


    private static void log(String tag, String msg, char level) {
        if (mLogSwitch) {
            if ('e' == level) {
                android.util.Log.e(tag, msg);
            } else if ('w' == level && (mLogType.getType() <= LogType.w.getType())) {
                android.util.Log.w(tag, msg);
            } else if ('i' == level && (mLogType.getType() <= LogType.i.getType())) {
                android.util.Log.i(tag, msg);
            } else if ('d' == level && (mLogType.getType() <= LogType.d.getType())) {
                android.util.Log.d(tag, msg);
            } else if ('v' == level && (mLogType.getType() <= LogType.v.getType())) {
                android.util.Log.v(tag, msg);
            }
            if (mLogWriteToFile) {
                writeLogtoFile(mFileWriter, String.valueOf(level), tag, msg);
            }
        }
    }


    private static void writeLogtoFile(FileWriter fileWriter, String type, String tag, String text) {
        try {
            Date nowtime = new Date();
            String needWriteFiel = mLogFile.format(nowtime);
            String needWriteMessage = mLogSdf.format(nowtime) + "    " + type
                    + "    " + tag + "    " + text;

            if (fileWriter == null) {
                File file = new File(mLogPath, needWriteFiel + "--" + mLogFileName);

                // "true" means to append the original content instead of
                // covering it.
                fileWriter = new FileWriter(file, true);
            }

            BufferedWriter bufWriter = new BufferedWriter(fileWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void clean() {
        mFileWriter = null;
        delFile();
    }


    public static void delFile() {
        String needDelFiel = mLogFile.format(getDateBefore());
        File file = new File(mLogPath, needDelFiel + "--" + mLogFileName);
        if (file.exists()) {
            file.delete();
        }
    }


    private static Date getDateBefore() {
        Date     nowtime = new Date();
        Calendar now     = Calendar.getInstance();
        now.setTime(nowtime);

        // To judge whether the logfile is produced in last month
        if (now.get(Calendar.DATE) >= mLogFileSaveDays) {

            // logfile is produced this month
            now.set(Calendar.DATE, now.get(Calendar.DATE)
                    - mLogFileSaveDays);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(nowtime);

            // set the day to the first day of this month
            cal.set(Calendar.DAY_OF_MONTH, 1);

            // set the time to the last day of last month
            cal.add(Calendar.DAY_OF_MONTH, -1);

            // get the logfile produced time
            now.set(Calendar.DATE, now.get(Calendar.DATE) + cal.getActualMaximum(Calendar.DATE)
                    - mLogFileSaveDays);
        }

        return now.getTime();
    }
}
