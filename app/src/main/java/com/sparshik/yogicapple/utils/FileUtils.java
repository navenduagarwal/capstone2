package com.sparshik.yogicapple.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * File utilities
 */
public class FileUtils extends Thread {
    private static final long SPACE_TO_SAVE = 52428800;
    private static File externalDownloadDir = null;

    public static File getSaveFolder(Context mContext) {
        if (externalDownloadDir == null && "mounted".equals(Environment.getExternalStorageState())) {
            if (Environment.isExternalStorageEmulated()) {
                externalDownloadDir = mContext.getFilesDir();
            } else {
                externalDownloadDir = mContext.getExternalFilesDir(null);
            }
        }
        if (externalDownloadDir == null) {
            externalDownloadDir = mContext.getCacheDir();
        }
        return externalDownloadDir;
    }

    public static File getCacheFolder(Context mContext) {
        return mContext.getCacheDir();
    }

    public static void deleteOfflineFiles() {
        new FileUtils().start();
    }

    public static long getFreeSpace(Context context) {
        return getSaveFolder(context).getUsableSpace();
    }

    public static boolean diskSpaceAvailable(Context context, long needed) {
        return getFreeSpace(context) - needed > SPACE_TO_SAVE;
    }

    public static int countFiles(Context mContext) {
        if (externalDownloadDir == null && "mounted".equals(Environment.getExternalStorageState())) {
            if (Environment.isExternalStorageEmulated()) {
                externalDownloadDir = mContext.getFilesDir();
            } else {
                externalDownloadDir = mContext.getExternalFilesDir(null);
            }
        }
        if (externalDownloadDir == null) {
            externalDownloadDir = mContext.getCacheDir();
        }
        return externalDownloadDir.listFiles().length;
    }

    public void run(Context context) {
        File[] files = FileUtils.getSaveFolder(context).listFiles();
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            if (filename.endsWith(Constants.SUFFIX_AUDIO) ||
                    filename.endsWith(Constants.SUFFIX_AUDIO_OLD) ||
                    filename.endsWith(Constants.SUFFIX_VIDEO) ||
                    filename.endsWith(Constants.SUFFIX_DOWNLOADING)) {
                files[i].delete();
            }
        }

    }

}
