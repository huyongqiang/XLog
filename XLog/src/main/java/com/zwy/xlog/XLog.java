package com.zwy.xlog;


import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class XLog {

    private static String TAG = "XLog";
    private static LoggerPrinter printer;
    private static boolean isInerLogToFile = false; //是否将debug日志写入文件
    private static String logFilePath = XUtils.getSDCardPathByEnvironment() + "/XLog/";
    private static final String tag = "XLog内部日志";

    private static String androidVersion;
    private static String cs;
    private static String xh;
    private static String[] abis;
    private static SimpleDateFormat format;
    private static SimpleDateFormat format_dir;
    private static String finalLogPath;
    private static Long fileMaxCapacity = 5L * 1024 * 1024;//单个文件超过该大小时重新创建
    private static boolean isEnableLog = true; //是否开启日志输出 默认开启

    static {
        format_dir = new SimpleDateFormat("yyyy-MM-dd");
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        androidVersion = android.os.Build.VERSION.RELEASE;
        cs = Build.MANUFACTURER;//设备厂商
        xh = Build.MODEL;//设备型号
        abis = XUtils.getABIs();
        printer = new LoggerPrinter();
        printer.addAdapter(new AndroidLogAdapter(TAG));
    }

    //==================================================外部调用==================================================

    /**
     * 是否开启日志输出  默认开启 关闭时传入false
     */
    public static void setIsEnableLog(boolean isEnableLog) {
        XLog.isEnableLog = isEnableLog;
    }

    /**
     * 开启日志写入文件功能(每一行日志都会写入)
     *
     * @param logFileDirPath_ 路径，必须以斜杠结尾，文件名称会以当前时间命名，单个文件超过5M时会创建第二个文件,以此类推
     * @param fileMaxCapacity 单个日志文件超过该大小时重新创建文件，单个文件过大时打开会耗时 默认5M
     */
    public static void openLog2File(String logFileDirPath_, Long fileMaxCapacity) {
        if (fileMaxCapacity > 0) {
            XLog.fileMaxCapacity = fileMaxCapacity * 1024 * 1024;
        }
        if (logFileDirPath_.substring(logFileDirPath_.length() - 1).equals("/")) {
            XLog.logFilePath = logFileDirPath_;
            openLog2File();
        } else {
            Log.e(tag, "日志保存路径必须以斜杠结尾");
        }
    }

    public static void openLog2File() {
        //以当天的时间命名文件夹
        File file = new File(XLog.logFilePath + format_dir.format(new Date()));
        if (!file.exists()) {
            createDir();
        }
        //获取所有的当天文件夹里的文件，找到最后一个,查询他的大小，可以写入时赋值到全局变量，不可写入时重新创建文件
        List<File> files = XUtils.listFilesInDirWithFilter(file);
        if (files != null && files.size() > 1) {
            //多个日志文件
            finalLogPath = getLastLogFilePath(files);
        } else {
            //单个日志文件
            try {
                if (files == null) {
                    Log.e(tag, "日志目录获取失败");
                    return;
                }
                finalLogPath = files.get(0).getPath();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(tag, "日志目录获取失败");
            }
        }
        isInerLogToFile = true;
    }

    /**
     * 清除日志目录下所有文件，包括日志目录
     */
    public static void clearLogFiles() {
        XUtils.recursionDeleteFile(new File(XLog.logFilePath));
    }

    public static void setTag(String tag) {
        XLog.TAG = tag;
        printer.clearLogAdapters();
        printer.addAdapter(new AndroidLogAdapter(TAG));
    }

    public static void d(String logMsg) {
        if (!isEnableLog) return;
        printer.json(logMsg);
        if (isInerLogToFile) addLogToFile(logMsg, 4, "Debug");
    }

    public static void d(String tag, String logMsg) {
        if (!isEnableLog) return;
        if (tag != null && tag.length() > 0) {
            printer.t(tag).json(logMsg);
        } else {
            printer.json(logMsg);
        }
        if (isInerLogToFile) addLogToFile(logMsg, 4, "Debug");
    }

    public static void e(String errorMsg) {
        pe(null, errorMsg, null);
    }


    public static void e(String errorMsg, Throwable throwable) {
        pe(null, errorMsg, throwable);
    }

    public static void e(Throwable throwable) {
        pe(null, "", throwable);
    }


    public static void e(String tag, String errorMsg) {
        pe(tag, errorMsg, null);
    }


    public static void e(String tag, String errorMsg, Throwable throwable) {
        pe(tag, errorMsg, throwable);
    }

    public static void e(Throwable throwable, String tag) {
        pe(tag, "", throwable);
    }


    public static void w(String tag, String logMsg) {
        if (!isEnableLog) return;
        if (tag != null && tag.length() > 0) {
            printer.t(tag).e(logMsg);
        } else {
            printer.w(logMsg);
        }
        if (isInerLogToFile) addLogToFile(logMsg, 4, "Warn");
    }

    public static void w(String logMsg) {
        if (!isEnableLog) return;
        printer.w(logMsg);
        if (isInerLogToFile) addLogToFile(logMsg, 4, "Warn");
    }

    public static void i(String logMsg) {
        if (!isEnableLog) return;
        printer.i(logMsg);
        if (isInerLogToFile) addLogToFile(logMsg, 4, "Info");
    }

    public static void i(String tag, String logMsg) {
        if (!isEnableLog) return;
        if (tag != null && tag.length() > 0) {
            printer.t(tag).i(logMsg);
        } else {
            printer.i(logMsg);
        }
        if (isInerLogToFile) addLogToFile(logMsg, 4, "Info");
    }
    //==================================================外部调用==================================================


    private static String getLastLogFilePath(List<File> files) {
        long a = 0L;
        File file = null;
        for (int i = 0; i < files.size(); i++) {
            try {
                Date date = format.parse(files.get(i).getName());
                if (date.getTime() > a) {
                    a = date.getTime();
                    file = files.get(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return file != null ? file.getPath() : null;
    }

    private static void pe(String tag, String errorMsg, Throwable throwable) {
        if (!isEnableLog) return;
        if (tag != null && tag.length() > 0) {
            printer.t(tag).e(errorMsg);
        } else {
            printer.e(throwable, errorMsg);
        }
        if (isInerLogToFile) addLogToFile(errorMsg, 5, "Error");
    }

    private static void addLogToFile(String logMsg, int l, String logType) {

        //开始写入文件
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String stackTraceBuilder = XUtils.getSimpleClassName(trace[l].getClassName()) +
                "." +
                trace[l].getMethodName() +
                " " +
                " (" +
                trace[l].getFileName() +
                ":" +
                trace[l].getLineNumber() +
                ")";

        String stringBuffer_log = "【" + logType + "】 " + format.format(new Date()) + " " + stackTraceBuilder + " " +
                logMsg + "\n";
        File finalFile = new File(finalLogPath);
        if (!finalFile.exists() || finalFile.length() > fileMaxCapacity) {
            finalFile =  createDir();
        }
        XUtils.input2File(stringBuffer_log, finalFile.getPath(), new InsertLogMsgListener() {
            @Override
            public void onSucc() {
            }

            @Override
            public void onError(String msg) {
                Log.e(tag, msg);
            }
        });

    }


    private static File createDir() {
        //获取目录是否存在，已经存在时拿出最后一个文件，判断文件大小，超过5M时重新创建文件，没超过时用最后一个文件
        File thisDataFileLog = new File(logFilePath + format_dir.format(new Date()) + "/" + format.format(new Date()) + ".log");
        CreateLogFileState createLogFileState = XUtils.createOrExistsFile(thisDataFileLog);

        //多文件拆分
        switch (createLogFileState) {
            case PathError:
                Log.d(tag, "日志文件创建失败,路径错误");
                return null;
            case PermissionDenied:
                Log.d(tag, "日志文件创建失败,权限不足");
                return null;
            case Success_Create:
                StringBuilder stringBuffer = new StringBuilder();
                stringBuffer.append("设备信息：\n");
                stringBuffer.append("系统版本:").append(androidVersion).append("\n");
                stringBuffer.append("设备厂商:").append(cs).append("\n");
                stringBuffer.append("设备型号:").append(xh).append("\n");
                StringBuilder sb2 = new StringBuilder();
                if (abis != null && abis.length > 0) {
                    for (int i = 0; i < abis.length - 1; i++) {
                        sb2.append(abis[i]).append(",");
                    }
                }
                stringBuffer.append("支持的动态库:").append(sb2.substring(0, sb2.length() - 1)).append("\n");
                XUtils.input2File(stringBuffer.toString(), thisDataFileLog.getPath(), null);
                Log.d(tag, "日志文件创建成功");
                break;
        }
        return thisDataFileLog;
    }


}
