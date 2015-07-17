package com.kuaikan.comic.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by a on 2015/3/26.
 * @author liuchao
 */
public class FileUtil {

    private static final String KK_STORAGE_FILE_PATH = "/KuaiKan";

//    private static final String KK_FILE_CACHE = "/file";

//    private static final String KK_IMAGE_CACHE = "/images";

//    private static final String KK_NET_CATCH = "/.response";

    public static final String SDPATH = Environment.getExternalStorageDirectory() + KK_STORAGE_FILE_PATH;

//    private static

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static boolean saveBitmapAsFile(String path, Bitmap bitmap) {
        boolean flag = false;
        File newfile = new File(path);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(newfile);
            flag = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;

    }


    public static boolean isSDFileExists(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public static boolean isFileExists(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            File file = new File(fileName);
            return isFileExists(file);
        }else{
            return false;
        }
    }

    public static boolean isFileExists(File file) {
        if(file != null){
            return file.exists();
        }else{
            return false;
        }
    }

    public static boolean isDirectory(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            File file = new File(fileName);
            return isDirectory(file);
        }else{
            return false;
        }

    }

    public static boolean isDirectory(File file) {
        if(file != null){
            return file.isDirectory();
        }else{
            return false;
        }
    }

    public static boolean isFile(File file) {
        if(file != null){
            return file.isFile();
        }else{
            return false;
        }
    }
    public static boolean isFile(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            File file = new File(fileName);
            return isFile(file);
        }else{
            return false;
        }
    }

    public static boolean createOrExistsFolder(File file) {
        if (file == null)
            return false;
        boolean result = false;

        if (isFileExists(file) && isDirectory(file)) {
            // 如果file存在且是文件夹，返回true
            return true;
        }
        // 如果文件夹不存在，创建文件夹
        if (file.mkdirs()) {
            // 创建成功返回true
            result = true;
        } else {
            // 创建失败返回false
            result = false;
        }
        return result;
    }
    public static boolean createOrExistsFolder(String fileName) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }
        File file = new File(fileName);
        return createOrExistsFolder(file);
    }

    public static boolean createOrExistsFile(File file) {
        if (file == null)
            return false;
        boolean result = false;
        if (isFileExists(file) && isFile(file)) {
            // 判断文件是否存在且为文件，如果存在结果为true
            return true;
        }
        // 如果文件不存在，创建文件
        // 先创建文件夹，否则不会成功
        File parentFile = file.getParentFile();
        if (!createOrExistsFolder(parentFile)) {
            // 如果父文件夹创建不成功，返回false
            return false;
        }
        try {
            if (file.createNewFile()) {
                // 创建成功返回true
                result = true;
            } else {
                // 创建失败返回false
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    public static boolean createOrExistsFile(String fileName) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }
        File file = new File(fileName);
        return createOrExistsFile(file);
    }

    public static String readStringFromFile(String path){
        FileReader fr= null;
        BufferedReader br = null;
        String readline = "";
        try {
            fr = new FileReader(path);
        //可以换成工程目录下的其他文本文件
        StringBuffer sb = new StringBuffer();
        br = new BufferedReader(fr);

            while((readline = br.readLine())!=null){
                sb.append(readline);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 将String数据存为文件
     */
    public static File writeToFileFromString(String path, String content) {
        byte[] b= content.getBytes();
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(path);
            createOrExistsFile(file);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }


    public static boolean writeToFileFromInputStream(String fileName, InputStream is) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }

        File parentFile = new File(fileName).getParentFile();
        if (!createOrExistsFolder(parentFile)) {
            return false;
        }

        boolean result = false;
        File file = null;
        OutputStream os = null;
        try {
            file = new File(fileName);
            os = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
            result = false;
        } finally {
            try {
                os.close();

            } catch (Exception e2) {
                e2.printStackTrace();
                file = null;

            }
        }
        return result;
    }


}
