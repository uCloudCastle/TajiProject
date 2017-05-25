package com.randal.aviana;

import java.io.File;

//Environment.getDataDirectory().getPath() :           /data
//Environment.getDownloadCacheDirectory().getPath()  : /cache
//Environment.getExternalStorageDirectory().getPath(): /storage/sdcard
//Environment.getRootDirectory().getPath()           : /system
//Context.getCacheDir().getPath()                    : /data/data/com.zhd/cache
//Context.getExternalCacheDir().getPath()            : /mnt/sdcard/Android/data/com.zhd/cache
//Context.getFilesDir().getPath()                    : /data/data/com.zhd/files
//Context.getObbDir().getPath()                      : /mnt/sdcard/Android/obb/com.zhd
//Context.getPackageName()                           : com.zhd
//Context.getPackageCodePath()                       : /data/app/com.zhd-1.apk
//Context.getPackageResourcePath()                   : /data/app/com.zhd-1.apk

public class StorageUtils {
    private StorageUtils(){
        throw new UnsupportedOperationException("DO NOT INSTANTIATE THIS CLASS");
    }

    public static long getFreeSizeKB(String path, boolean createPath) {          // KB
        File folder = new File(path);
        if (!folder.exists()) {
            if (createPath) {
                folder.mkdirs();
            } else {
                return 0;
            }
        }
        long freeSpace = folder.getUsableSpace();
        return freeSpace / 1024;
    }

    public static int getFreeSizeMB(String path, boolean createPath) {          // MB
        File folder = new File(path);
        if (!folder.exists()) {
            if (createPath) {
                folder.mkdirs();
            } else {
                return 0;
            }
        }
        long freeSpace = folder.getFreeSpace();
        return (int)(freeSpace / (1024 * 1024));
    }

    public static long getTotalSizeKB(String path, boolean createPath) {          // KB
        File folder = new File(path);
        if (!folder.exists()) {
            if (createPath) {
                folder.mkdirs();
            } else {
                return 0;
            }
        }
        long freeSpace = folder.getTotalSpace();
        return freeSpace / 1024;
    }

    public static int getTotalSizeMB(String path, boolean createPath) {          // MB
        File folder = new File(path);
        if (!folder.exists()) {
            if (createPath) {
                folder.mkdirs();
            } else {
                return 0;
            }
        }
        long freeSpace = folder.getTotalSpace();
        return (int)(freeSpace / (1024 * 1024));
    }
}
