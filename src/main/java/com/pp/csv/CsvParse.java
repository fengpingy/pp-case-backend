package com.pp.csv;

import com.pp.entity.CaseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CsvParse {
    /**
     * 解析case
     */
    List<Map<String,CaseEntity>> analysisCase();

    static CsvParse with (String csvFile) throws IOException {
        return new CsvParseImpl(csvFile);
    }
}
