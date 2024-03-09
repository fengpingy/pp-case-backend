package com.pp.utils;

import com.pp.common.constants.SystemConst;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FileUtils {
    /**
     * 读取文件中的内容转成string
     *
     * @return 返回String
     */
    public static String readFileToString(String filePath) {
        File file = loadFile(filePath);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, SystemConst.FILE_ECHARTS);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    /**
     * 保存文件
     * @param file
     * @param path
     */
    public static void saveFile(MultipartFile file, String path) {
        File xFile = loadFile(path);
        if (!xFile.getParentFile().exists()) {
            xFile.getParentFile().mkdirs();
            xFile.mkdirs();
        }
        try {
            file.transferTo(xFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载文件
     * @param path
     * @return
     */
    public static File loadFile(String path){
        return new File(path);
    }


    /**
     * 文件是否存在
     * @param path
     * @return
     */
    public static boolean fileExists(String path){
        File file = loadFile(path);
        return file.exists();
    }


    public static byte[] fileUrlToBytes(String fileUrl) {
        FileInputStream inputStream = null;
        byte[] bytes = null;
        try {
            File file = new File(fileUrl);
            inputStream = new FileInputStream(file);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes,0,inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }
}
