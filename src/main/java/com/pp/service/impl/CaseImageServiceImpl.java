package com.pp.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pp.dao.ImageCaseMapper;
import com.pp.entity.CaseEntity;
import com.pp.entity.CaseImageEntity;
import com.pp.service.CaseImageService;
import com.pp.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CaseImageServiceImpl implements CaseImageService {
    @Resource
    private ImageCaseMapper imageCaseMapper;

    @Override
    public int batchAddCase(List<CaseImageEntity> caseImageEntities) {
        return imageCaseMapper.batchInsert(caseImageEntities);
    }

    @Override
    public int deleteCase(Long id) {
        return imageCaseMapper.deleteById(id);
    }

    @Override
    public CaseImageEntity getCase(Long id) {
        return imageCaseMapper.selectById(id);
    }

    @Override
    public int deleteBatchCase(List<CaseImageEntity> caseImageEntityList) {
        return imageCaseMapper.batchDelete(caseImageEntityList);
    }

    @Override
    public List<CaseImageEntity> addCaseByCaseIds(List<CaseImageEntity> caseImageEntities) {
        // 1.先去重
        List<CaseImageEntity> caseImageEntityList = caseImageEntities.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(CaseImageEntity::getId))
                )
                , ArrayList:: new)
        );
        // 2.将caseImageEntityList的溯源ID替换成cases对象的ID，caseImageEntityList替换成雪花ID
        for (CaseImageEntity caseImageEntity : caseImageEntityList) {
            caseImageEntity.setOriginalCaseId(caseImageEntity.getId());
            caseImageEntity.setId(IdWorker.getId());
            Date date = new Date();
            caseImageEntity.setCreateTime(date);
            caseImageEntity.setUpdateTime(date);
        }
        // 3.批量添加到moka_case_image表中
        if (caseImageEntityList.size() > 0) {
            imageCaseMapper.batchInsert(caseImageEntityList);
        }
        return caseImageEntityList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseImageEntity addCase(CaseEntity caseEntity) {
        CaseImageEntity caseImageEntity = BeanUtils.copyProperty(caseEntity, CaseImageEntity.class);
        caseImageEntity.setOriginalCaseId(caseImageEntity.getId());
        caseImageEntity.setId(IdWorker.getId());
        imageCaseMapper.insert(caseImageEntity);
        return caseImageEntity;
    }

    @Override
    public List<CaseImageEntity> getCaseByOriginalCaseId(Long id) {
        return imageCaseMapper.selectByOriginalCaseId(id);
    }

    @Override
    public int updateCaseByOriginalCaseId(CaseImageEntity caseImageEntity ) {
        UpdateWrapper<CaseImageEntity> caseImageEntityUpdateWrapper = new UpdateWrapper<>();
        caseImageEntityUpdateWrapper.eq("original_case_id", caseImageEntity.getOriginalCaseId());
        return imageCaseMapper.update(caseImageEntity, caseImageEntityUpdateWrapper);
    }

    @Override
    public int deleteCaseByOriginalCaseId(Long id) {
        return imageCaseMapper.deleteByOriginalCaseId(id);
    }

    @Override
    public int deleteImageCaseByModuleId(long moduleId) {
        return imageCaseMapper.deleteImageCaseByModuleId(moduleId);
    }

    @Override
    public List<CaseImageEntity> getCaseList(List<Long> caseImageIds) {
        return imageCaseMapper.selectBatchByIds(caseImageIds);
    }
}
