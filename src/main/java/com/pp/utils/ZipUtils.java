package com.pp.utils;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


import static com.pp.common.constants.SystemConst.FILE_ECHARTS;

public class ZipUtils {
    /**
     * 解压文件
     * @param zipFilePath zip文件地址
     * @param extractPath 压缩文件地址
     */
    public static boolean zipFileExt(String zipFilePath, String extractPath) {

        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            if (!setZipFileCharset(zipFile, FILE_ECHARTS))return false;
            zipFile.extractAll(extractPath);
            return true;
        } catch (ZipException e) {
            return false;
        }
    }


    /**
     * 设置编码
     * @param zipFile  zip文件对象
     * @param charset  字符集
     */
    public static boolean setZipFileCharset(ZipFile zipFile, String charset) {
        if (zipFile == null) {
            return false;
        }
        try {
            zipFile.setFileNameCharset(charset);
            return true;
        } catch (ZipException e) {
            return false;
        }
    }
}
