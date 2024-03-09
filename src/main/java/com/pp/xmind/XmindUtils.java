package com.pp.xmind;

import com.pp.common.constants.SystemConst;
import com.pp.utils.FileUtils;

public class XmindUtils {
    /**
     * xmind是否是2021版本
     *
     * @param contentPath
     * @return
     */
    public static boolean isXmind2021(String contentPath) {
        return FileUtils.fileExists(contentPath + SystemConst.PATH_SEPARATOR + XmindConst.XMIND2021_CONTENT_FILE);
    }


    /**
     * 获取版本实例
     *
     * @param contentFileDir
     * @return
     */
    public static XmindVersion version(String contentFileDir) {
        return isXmind2021(contentFileDir) ? XmindVersion.XMIND2021 : XmindVersion.XMIND8_UPDATE9;
    }
}
