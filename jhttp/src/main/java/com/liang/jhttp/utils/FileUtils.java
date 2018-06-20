package com.liang.jhttp.utils;

import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static File getAlbumStorageFile(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.
                DIRECTORY_DOWNLOADS), fileName);//参数是文件名称
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getAlbumStorageDir(String parent, String fileName) {
        File file = new File(getAlbumStorageFolder(parent), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getAlbumStorageFolder(String parent) {
        File file = new File(parent);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static void closeCloseable(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
