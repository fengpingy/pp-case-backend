package com.pp.service.impl;

import com.pp.dao.ModuleMapper;
import com.pp.dao.UnitMapper;
import com.pp.dto.ModuleDTO;
import com.pp.dto.UnitDTO;
import com.pp.dto.response.ModuleTreeDTO;
import com.pp.entity.ModuleEntity;
import com.pp.entity.UnitEntity;
import com.pp.expection.PpExpection;
import com.pp.service.UnitService;
import com.pp.utils.BeanUtils;
import com.pp.utils.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static com.pp.common.constants.CommonError.ENTITY_ID_NOT_EXIST;
import static com.pp.common.constants.SystemConst.DEFAULT_CODE;
import static com.pp.common.constants.SystemConst.DEFAULT_MODULE_NAME;


@Service
@Slf4j
public class UnitServiceImpl implements UnitService {

    @Resource
    private UnitMapper unitMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addUnit(UnitDTO unitDTO) {
        UnitEntity unitEntity = BeanUtils.copyProperty(unitDTO, UnitEntity.class);
        unitMapper.insert(unitEntity);
        ModuleEntity moduleEntity = new ModuleEntity();
        moduleEntity.setCode(DEFAULT_CODE);
        moduleEntity.setName(DEFAULT_MODULE_NAME);
        moduleEntity.setParentId(unitEntity.getId());
        moduleMapper.insert(moduleEntity);
        return unitEntity.getId();
    }

    @Override
    public ModuleTreeDTO selectModuleTree(Long unitId) {
        boolean b = QueryUtils.ObjIsExists(unitMapper, unitId);
        if (!b){
            throw PpExpection.newsPpExpection(ENTITY_ID_NOT_EXIST);
        }
        UnitEntity unitEntity = unitMapper.selectById(unitId);
        ModuleTreeDTO moduleTreeDTO = new ModuleTreeDTO();
        moduleTreeDTO.setId(unitId);
        moduleTreeDTO.setName(unitEntity.getUnitName());
        List<ModuleEntity> moduleEntities = moduleMapper.selectAllModule();
        List<ModuleDTO> moduleDTOS = BeanUtils.listObjectCopyProperty(moduleEntities, ModuleDTO.class);
        assert moduleDTOS != null;
        selectModuleAndSubmodule(moduleTreeDTO,moduleDTOS);
        return moduleTreeDTO;
    }

    public void selectModuleAndSubmodule(ModuleTreeDTO moduleTreeDTO, List<ModuleDTO> allModule) {
        for (ModuleDTO dto : allModule) {
            if (dto.getParentId().equals(moduleTreeDTO.getId())){
                ModuleTreeDTO sonModule = new ModuleTreeDTO();
                BeanUtils.copyProperty(dto, sonModule);
                moduleTreeDTO.getChildren().add(sonModule);
                selectModuleAndSubmodule(sonModule,allModule);
            }
            else {
                continue;
            }
        }
    }
}
