package com.pp.service;

import com.pp.dto.ModuleDTO;
import com.pp.entity.ModuleEntity;

import java.util.List;

public interface ModuleService {
    /**
     * 添加模块
     *
     * @param moduleDTO
     * @return
     */
    Long addModule(ModuleDTO moduleDTO);

    /**
     * 编辑模块
     *
     * @param moduleDTO
     * @return
     */
    int editModule(ModuleDTO moduleDTO);

    /**
     * 获取模块详情
     *
     * @param id
     * @return
     */
    ModuleDTO getModule(Long id);

    /**
     * 删除模块，如果模块下有用例不允许删除
     * @param moduleId
     * @return
     */
    int deleteModule(Long moduleId);

    /**
     * 查询模块下的子模块
     *
     * @param moduleId
     * @param isIncludeSubmodule
     * @return
     */

    List<ModuleEntity> selectModuleAndSubmodule(Long moduleId, Boolean isIncludeSubmodule);

    /**
     * 查询模块下的子模块
     *
     * @param moduleId
     * @return
     */
    List<ModuleDTO> selectNextModules(Long moduleId);


    /**
     * 查询模块的所有父节点，一直到根节点
     * @param moduleDTO
     * @return
     */
    List<ModuleDTO> selectAllParentModules(ModuleDTO moduleDTO);

    int batchUpdateModules(List<ModuleDTO> moduleDTOS);

    List<ModuleEntity> selectModulesByIds(List<Long> ModulesIds);

    List<ModuleDTO> newSelectAllParentModules(List<Long> ModulesIds);
}
