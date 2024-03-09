package com.pp.service;

import com.pp.entity.CaseEntity;
import com.pp.entity.CaseImageEntity;
import java.util.List;


public interface CaseImageService {


    /**
     * 批量添加镜像case
     * @param caseImageEntities
     * @return
     */
    int batchAddCase(List<CaseImageEntity> caseImageEntities);

    /**
     * 删除镜像case
     * @param id
     * @return
     */
    int deleteCase(Long id);

    /**
     * 获取镜像case
     * @param id
     * @return
     */
    CaseImageEntity getCase(Long id);

    /**
     * 批量删除镜像case
     * @param caseImageEntityList
     * @return
     */
    int deleteBatchCase(List<CaseImageEntity> caseImageEntityList);

    /**
     * 批量添加镜像case
     * @param caseImageEntities
     * @return
     */
    List<CaseImageEntity> addCaseByCaseIds(List<CaseImageEntity> caseImageEntities);

    /**
     * 添加镜像case
     * @param caseEntity
     * @return
     */
    CaseImageEntity addCase(CaseEntity caseEntity);

    /**
     * 根据原始caseID获取镜像case
     * @param id
     * @return
     */
    List<CaseImageEntity> getCaseByOriginalCaseId(Long id);

    /**
     * 根据原始caseID更新镜像case
     * @param caseImageEntity
     * @return
     */
    int updateCaseByOriginalCaseId(CaseImageEntity caseImageEntity);

    /**
     * 根据原始caseID删除镜像case
     * @param id
     * @return
     */
    int deleteCaseByOriginalCaseId(Long id);

    /**
     * 根据moduleId删除模块下镜像cases
     * @param moduleId
     * @return
     */
    int deleteImageCaseByModuleId(long moduleId);

    /**
     * 通过caseImageId批量查询镜像cases
     * @param caseImageIds
     * @return
     */
    List<CaseImageEntity> getCaseList(List<Long> caseImageIds);


}
