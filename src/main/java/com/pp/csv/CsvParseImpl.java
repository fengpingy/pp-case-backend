package com.pp.csv;

import com.pp.common.constants.SystemConst;
import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;
import com.pp.entity.CaseEntity;
import com.pp.entity.other.CaseStepExpect;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import static com.pp.utils.CSVUtils.getCsvData2;
import static com.pp.utils.FileUtils.loadFile;


@Slf4j
public class CsvParseImpl implements CsvParse {
    protected StringBuffer initModuleName = new StringBuffer(SystemConst.DEFAULT_MODULE_NAME);

    /**
     * 内容
     */

    protected List<CsvCase> content;

    /**
     * 内容文件所在目录
     */
    protected String csvFile;

    /**
     * 处理所有内容
     *
     * @return
     */
    public void parseContent() throws IOException {
        this.content = getCsvData2(loadFile(csvFile), CsvCase.class);
    }

    public CsvParseImpl(String csvFile) throws IOException {
        this.csvFile = csvFile;
        parseContent();
    }

    /**
     *
     * @return
     */
    @Override
    public List<Map<String,CaseEntity>> analysisCase() {
        log.info("文件内容++++++++++++++++++");
        List<Map<String, CaseEntity>> mapList = new ArrayList<>();
        for (CsvCase csvCase : content) {
            // 组装测试用例
            // 步骤与预期结
            List<CaseStepExpect> caseStepExpects = new ArrayList<>();
            CaseStepExpect caseStepExpect = new CaseStepExpect();
            caseStepExpect.setStep(csvCase.getStep());
            caseStepExpect.setExpect(csvCase.getExpect());
            caseStepExpects.add(caseStepExpect);
            // 用例编号、标题、类型、等级、前置
            CaseEntity build = CaseEntity.builder()
                    .code(csvCase
                            .getCode())
                    .title(csvCase.getTitle())
                    .type(CaseType.of(csvCase.getType()))
                    .level(CaseLevel.nameOf(csvCase.getLevel()))
                    .precondition(csvCase.getPrecondition())
                    .stepExpect(caseStepExpects)
                    .build();
            Map<String, CaseEntity> caseEntityMap = new HashMap<>();
            caseEntityMap.put(csvCase.getModule(), build);
            mapList.add(caseEntityMap);
        }
        for (Map<String, CaseEntity> caseEntityMap : mapList) {
            log.info(caseEntityMap.toString());
        }
        return mapList;
    }
}
