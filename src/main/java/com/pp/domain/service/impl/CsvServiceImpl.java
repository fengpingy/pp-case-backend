package com.pp.domain.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pp.common.UserHolder;
import com.pp.common.constants.SystemConst;
import com.pp.csv.CsvParse;
import com.pp.csv.CsvPathHelper;
import com.pp.dao.CaseMapper;
import com.pp.dao.ModuleMapper;
import com.pp.domain.service.api.CsvService;
import com.pp.dto.response.ModuleTreeDTO;
import com.pp.entity.CaseEntity;
import com.pp.entity.ModuleEntity;
import com.pp.entity.UserEntity;
import com.pp.expection.PpExpection;
import com.pp.service.UnitService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import java.util.*;

import static com.pp.common.constants.CommonError.FILE_IS_EMPTY;
import static com.pp.common.constants.CommonError.FILE_TYPE_ERROR;


@Service
@Slf4j
public class CsvServiceImpl implements CsvService {

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private UnitService unitService;

    @Resource
    private CaseMapper caseMapper;


    /**
     *
     * @param file
     * @param unitId
     * @return
     */
    @SneakyThrows
    @Override
    public Boolean parseCaseAndModule(MultipartFile file, Long unitId) {
        // 文件为空
        if (file.isEmpty()) {
            throw PpExpection.newsPpExpection(FILE_IS_EMPTY);
        }
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        // 文件类型错误
        if (!fileName.endsWith(SystemConst.CSV_FILE_SUFFIX)) {
            throw PpExpection.newsPpExpection(FILE_TYPE_ERROR);
        }
        CsvPathHelper csvPathHelper = new CsvPathHelper(file);
        // 保存文件
        String filePath = csvPathHelper.saveFile();
        // 解析文件
        CsvParse csvParse = CsvParse.with(filePath);
        List<Map<String, CaseEntity>> mapList = csvParse.analysisCase();
        // 插入数据
        UserEntity userEntity = UserHolder.get();
        List<CaseEntity> caseList = new ArrayList<>();
        for (Map<String, CaseEntity> caseEntityMap : mapList) {
            Set<String> strings = caseEntityMap.keySet();
            for (String string : strings) {
                //插入模块
                Long lastModuleId = parseModulePathByName(string, unitId);
                // 构造用例数据
                CaseEntity caseEntity = caseEntityMap.get(string);
                caseEntity.setModuleId(lastModuleId);
                Date date = new Date();
                caseEntity.setId(IdWorker.getId());
                caseEntity.setCreateTime(date);
                caseEntity.setUpdateTime(date);
                if (userEntity != null) {
                    caseEntity.setTesterId(userEntity.getId());
                }
                caseList.add(caseEntity);

            }
        }
        // 插入用例
        caseMapper.batchInsert(caseList);
        return true;
    }


    /**
     *
     * @param modulePath
     * @param unitId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long parseModulePathByName(String modulePath, Long unitId) {
        String[] moduleNames = splitModulePath(modulePath);
        //查询模块树
        ModuleTreeDTO moduleTreeDTO = unitService.selectModuleTree(unitId);
        //默认模块
        List<ModuleTreeDTO> children = moduleTreeDTO.getChildren();
        //
        int startIndex = 0;
        //匹配模块，没有就新增模块
        long startModuleId = moduleTreeDTO.getId();
        for (int i = 0; i < moduleNames.length; i++) {
            boolean exists = false;
            boolean flag = false;
            while ( !flag && !children.isEmpty() ) {
                for (ModuleTreeDTO child : children) {
                    String moduleName = moduleNames[i];
                    String name = child.getName();
                    if (child.getName().equals(moduleNames[i])) {
                        startModuleId = child.getId();
                        children = child.getChildren();
                        exists = true;
                        flag = true;
                        break;
                    }
                    children = child.getChildren();
                }
            }
            if (exists) {
                continue;
            } else {
                startIndex = i;
                break;
            }
        }
        if (startIndex != 0) {
            for (int i = startIndex; i < moduleNames.length; i++) {
                ModuleEntity moduleEntity = new ModuleEntity();
                moduleEntity.setParentId(startModuleId);
                moduleEntity.setName(moduleNames[i]);
                moduleEntity.setCode(moduleNames[i]);
                //暂时循环插入 todo
                moduleMapper.insert(moduleEntity);
                startModuleId = moduleEntity.getId();
            }
        }
        return startModuleId;
    }

    public String[] splitModulePath(String path) {
        String[] split = path.split("/");
        return Arrays.stream(split).filter(s -> !s.equals("")).toArray(String[]::new);
    }
}
