package com.pp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pp.dao.ModuleMapper;
import com.pp.dto.ModuleDTO;
import com.pp.entity.ModuleEntity;
import com.pp.expection.PpExpection;
import com.pp.service.CaseService;
import com.pp.service.ModuleService;
import com.pp.utils.BeanUtils;
import com.pp.utils.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pp.common.constants.CommonError.*;
import static com.pp.utils.BeanUtils.copyProperty;
import static com.pp.utils.BeanUtils.listObjectCopyProperty;


@Service
@Slf4j
public class ModuleServiceImpl implements ModuleService {
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private CaseService caseService;

    @Override
    public Long addModule(ModuleDTO moduleDTO) {
        Long parentId = moduleDTO.getParentId();
        getModule(parentId);
        ModuleEntity moduleEntity = BeanUtils.copyProperty(moduleDTO, ModuleEntity.class);
        moduleMapper.insert(moduleEntity);
        return moduleEntity.getId();
    }

    @Override
    public int editModule(ModuleDTO moduleDTO) {
        ModuleEntity moduleEntity = BeanUtils.copyProperty(moduleDTO, ModuleEntity.class);
        return moduleMapper.updateById(moduleEntity);
    }

    @Override
    public ModuleDTO getModule(Long id) {
        ModuleEntity moduleEntity = moduleMapper.selectById(id);
        if (moduleEntity == null) {
            throw new PpExpection(ENTITY_ID_NOT_EXIST);
        }
        return BeanUtils.copyProperty(moduleEntity, ModuleDTO.class);
    }

    @Override
    public int deleteModule(Long moduleId) {
        Boolean aBoolean = caseService.checkCaseByModuleId(moduleId);
        if (aBoolean) {
            throw PpExpection.newsPpExpection(CASE_IS_EXIST);
        }
        return moduleMapper.deleteById(moduleId);
    }

    @Override
    public List<ModuleEntity> selectModuleAndSubmodule(Long moduleId, Boolean isIncludeSubmodule) {
        List<ModuleEntity> list = new ArrayList<>();
        ModuleEntity rootEntity = moduleMapper.selectById(moduleId);
        if (!isIncludeSubmodule) {
            if (rootEntity == null) {
                throw PpExpection.newsPpExpection(ENTITY_ID_NOT_EXIST);
            }
            list.add(rootEntity);
            return list;
        }
        if (rootEntity != null) {
            list.add(rootEntity);
        }

        List<ModuleEntity> allModule = moduleMapper.selectAllModule();
        selectModuleAndSubmodule(moduleId, list, allModule);
        return list;
    }

    @Override
    public List<ModuleDTO> selectNextModules(Long moduleId) {
        boolean b = QueryUtils.ObjIsExists(moduleMapper, moduleId);
        //如果不存在
        if (!b){
            throw PpExpection.newsPpExpection(ENTITY_ID_NOT_EXIST);
        }
        LambdaQueryWrapper<ModuleEntity> lambdaQueryWrapper = new LambdaQueryWrapper<ModuleEntity>()
                .eq(ModuleEntity::getParentId,moduleId);
        List<ModuleEntity> moduleEntities = moduleMapper.selectList(lambdaQueryWrapper);
        List<ModuleDTO> moduleDTOS = BeanUtils.listObjectCopyProperty(moduleEntities, ModuleDTO.class);
        return moduleDTOS;
    }

    @Override
    public List<ModuleDTO> selectAllParentModules(ModuleDTO moduleDTO) {
        List<ModuleDTO> moduleDTOS = new ArrayList<>();
        ModuleDTO parentModuleDTO = copyProperty(moduleMapper.selectById(moduleDTO.getParentId()), ModuleDTO.class);
        while (parentModuleDTO != null) {
            moduleDTOS.add(parentModuleDTO);
            parentModuleDTO = copyProperty(moduleMapper.selectById(parentModuleDTO.getParentId()), ModuleDTO.class);
        }
        return moduleDTOS;
    }

    @Override
    public int batchUpdateModules(List<ModuleDTO> moduleDTOS) {
        List<ModuleEntity> moduleEntities = listObjectCopyProperty(moduleDTOS, ModuleEntity.class);
        return moduleMapper.updateBatchById(moduleEntities);
    }

    /**
     * 查找所有子模块
     *
     * @param rootModuleId
     * @param list
     * @param allModule
     */
    public void selectModuleAndSubmodule(Long rootModuleId, List<ModuleEntity> list, List<ModuleEntity> allModule) {
        for (ModuleEntity moduleEntity : allModule) {
            if (moduleEntity.getParentId().equals(rootModuleId)) {
                list.add(moduleEntity);
                selectModuleAndSubmodule(moduleEntity.getId(), list, allModule);
            } else {
                continue;
            }
        }
    }

    /***
     * 查询模块集合数据
     * @param ModulesIds
     * @return
     */
    @Override
    public List<ModuleEntity> selectModulesByIds(List<Long> ModulesIds) {
        if (ModulesIds.isEmpty()){
            return new ArrayList<>();
        }
        return moduleMapper.selectModulesByIds(ModulesIds);
    }

    @Override
    public List<ModuleDTO> newSelectAllParentModules(List<Long> ModulesIds) {
        List<ModuleDTO> moduleDTOS = new ArrayList<>();
        List<ModuleDTO> moduleEntities = listObjectCopyProperty(moduleMapper.selectModulesByIds(ModulesIds),ModuleDTO.class);
        assert moduleEntities != null;
        if (moduleEntities.isEmpty()){
            return moduleDTOS;
        }
        while (moduleEntities != null){
            moduleDTOS.addAll(moduleEntities.stream().distinct().collect(Collectors.toList()));
            List<Long> newModulesIds = new ArrayList<>();
            moduleEntities.stream().distinct().forEach(e -> newModulesIds.add(e.getParentId()));
            moduleEntities = listObjectCopyProperty(moduleMapper.selectModulesByIds(newModulesIds),ModuleDTO.class);
            log.info("数据为："+moduleEntities);
        }
        return moduleDTOS;
    }
}
