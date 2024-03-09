package com.pp.csv;

import com.pp.common.constants.SystemConst;
import com.pp.utils.FileUtils;
import com.pp.utils.SystemUtils;
import com.pp.utils.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Slf4j
public class CsvPathHelper {

    private final MultipartFile file;

    public CsvPathHelper(MultipartFile file) {
        this.file = file;
    }



    /**
     * 保存csv文件
     *
     * @return
     */
    public String saveFile() {
        String path = SystemConst.CSV_TMP_PATH +
                Objects.requireNonNull(file.getOriginalFilename()).split(SystemConst.CSV_FILE_SUFFIX)[0] +
                UUID.randomUUID() +
                SystemConst.CSV_FILE_SUFFIX;
        FileUtils.saveFile(file, path);
        log.info("csv文件地址:" + path);
        return path;
    }
}
