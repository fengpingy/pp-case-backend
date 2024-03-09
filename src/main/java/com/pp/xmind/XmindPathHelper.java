package com.pp.xmind;

import com.pp.common.constants.SystemConst;
import com.pp.utils.FileUtils;
import com.pp.utils.SystemUtils;
import com.pp.utils.ZipUtils;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class XmindPathHelper {

    private final MultipartFile file;


    public XmindPathHelper(MultipartFile file) {
        this.file = file;
    }



    private static String zipUnpackTheAddress(){
        return SystemConst.XMIND_TO_ZIP_TMP_PATH + SystemConst.PATH_SEPARATOR + SystemUtils.currentTimeStamp();
    }


    /**
     * 解压文件并返回解压地址
     * @return
     */
    public String saveAndSolutionFile(){
        String extract = zipUnpackTheAddress();
        StringBuilder stringBuilder = new StringBuilder();
        String path = stringBuilder.append(SystemConst.XMIND_TO_ZIP_TMP_PATH)
                .append(file.getOriginalFilename().split(SystemConst.XMIND_FILE_SUFFIX)[0])
                .append(UUID.randomUUID().toString())
                .append(SystemConst.ZIP_FILE_SUFFIX).toString();
        FileUtils.saveFile(file, path);
        //解压地址
        ZipUtils.zipFileExt(path, extract);
        return extract;
    }
}
