package com.pp.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pp.common.EventParamHolder;
import com.pp.common.UserHolder;
import com.pp.common.enums.business.XmindNodeType;
import com.pp.common.enums.system.EventType;
import com.pp.dao.CaseMapper;
import com.pp.dao.ImageCaseMapper;
import com.pp.dao.ModuleMapper;
import com.pp.dao.UserMapper;
import com.pp.dto.CaseDTO;
import com.pp.dto.DeleteCase;
import com.pp.dto.XmindCaseDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.CasePageQuery;
import com.pp.dto.response.page.CasePage;
import com.pp.dto.response.page.PageResponse;
import com.pp.entity.CaseEntity;
import com.pp.entity.CaseImageEntity;
import com.pp.entity.ModuleEntity;
import com.pp.entity.UserEntity;
import com.pp.entity.other.CaseStepExpect;
import com.pp.service.CaseImageService;
import com.pp.service.CaseService;
import com.pp.service.ModuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.pp.utils.BeanUtils.copyProperty;
import static com.pp.utils.BeanUtils.listObjectCopyProperty;
import static com.pp.utils.PageUtils.pageInfoToPageResponse;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CaseServiceImpl implements CaseService {
    @Resource
    private CaseMapper caseMapper;

    @Resource
    private UserMapper userMapper;
    @Resource
    private CaseImageService caseImageService;
    @Resource
    private ModuleService moduleService;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private ImageCaseMapper imageCaseMapper;

    @Override
    public Long addCase(CaseDTO caseDTO) {
        CaseEntity caseEntity = CaseEntity.builder().build();
        BeanUtils.copyProperties(caseDTO, caseEntity);
        caseMapper.insert(caseEntity);
        EventParamHolder.putEvent(EventType.ADD_CASE, caseEntity.getId());
        return caseEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddCase(List<CaseDTO> caseDTOS) {
        List<CaseEntity> caseEntities = listObjectCopyProperty(caseDTOS, CaseEntity.class);
        Long creatorId = UserHolder.get().getId(); // 获取创建人
        if (caseEntities == null) {
            return 0;
        }
        for (CaseEntity caseEntity : caseEntities) {
            caseEntity.setId(IdWorker.getId());
            Date date = new Date();
            caseEntity.setCreateTime(date);
            caseEntity.setUpdateTime(date);
            caseEntity.setTesterId(creatorId);
        }
        return caseMapper.batchInsert(caseEntities);
    }

    @Override
    public CaseDTO getCase(Long id) {
        CaseEntity caseEntity = caseMapper.selectById(id);
        CaseDTO dto= copyProperty(caseEntity, CaseDTO.class);
        String url=selectParentModule(dto.getModuleId());
        dto.setModuleTitle(url);
        return dto;
    }

    /**
     * 查找模块的父节点
     * @param id
     * @return
     */
    private String selectParentModule(Long id){
        boolean flag=true;
        Long currentId=id;
        StringBuilder sb=new StringBuilder();
        while (flag){
            List<ModuleEntity> ls=moduleMapper.selectOneParentId(currentId);
            if(ls==null||ls.size()==0){//没有父目录
                flag=false;
            }else{
                sb.insert(0,"/"+ls.get(0).getName());
                currentId=ls.get(0).getParentId();
            }
        }
       String url=sb.toString().replaceFirst("/默认模块/","");
        url=url.replaceFirst("ATS/渠道候选人/候选人/","");
        url=url.replaceFirst("ATS/渠道候选人/人才库/","");
        url=url.replaceFirst("ATS/渠道候选人/渠道/","");
        url=url.replaceFirst("ATS/流程/主流程/","");
        url=url.replaceFirst("ATS/流程/考试测评/","");
        url=url.replaceFirst("ATS/流程/面试/","");
        url=url.replaceFirst("ATS/流程/Offer/","");
        url=url.replaceFirst("ATS/ATS一体化/","");
        url=url.replaceFirst("ATS/平台架构/生态/","");
        url=url.replaceFirst("ATS/平台架构/架构/","");
        url=url.replaceFirst("绩效/","");
        url=url.replaceFirst("假勤/","");
        url=url.replaceFirst("组织人事/","");
        url=url.replaceFirst("BI/","");
        String a=url.substring(0,url.indexOf("/")+1);
        url=url.replaceFirst(a,"");
        return url==null?"":url;
    }

    @Override
    public PageResponse<CasePage> pageList(PageDTO<CasePageQuery> pageQueryPageDTO) {

        //所有用户
        List<UserEntity> userEntities = userMapper.selectList(new QueryWrapper<>());
        //查询关联module信息
        List<ModuleEntity> moduleEntities = moduleService.selectModuleAndSubmodule(pageQueryPageDTO.getParams().getModuleId(), true);
        List<Long> moduleIds = moduleEntities.stream().map(ModuleEntity::getId).collect(Collectors.toList());
        // 分页查询
        Page<Object> page = PageHelper.startPage(pageQueryPageDTO.getPageNum(), pageQueryPageDTO.getPageSize());
        // 根据moduleId去查询关联的case
        log.info("参数为：" + pageQueryPageDTO.getParams().toString());
        assert pageQueryPageDTO.getParams() != null;
        List<CaseEntity> caseEntities = caseMapper.selectPageList(moduleIds, pageQueryPageDTO.getParams());
        List<CasePage> casePages = listObjectCopyProperty(caseEntities, CasePage.class);
        // 添加创建人和模块信息
        if (casePages != null) {
            casePages.forEach(casePage -> {
                userEntities.forEach(userEntity -> {
                    if (Objects.equals(casePage.getTesterId(), userEntity.getId())) {
                        casePage.setCreator(userEntity.getNickname());
                    }
                });
                moduleEntities.forEach(moduleEntity -> {
                    if (Objects.equals(moduleEntity.getId(), casePage.getModuleId())) {
                        casePage.setModuleName(moduleEntity.getName());
                    }
                });
            });
        }

        if (pageQueryPageDTO.getParams().getCreator() != null && !pageQueryPageDTO.getParams().getCreator().equals("")) {
            List<CasePage> newCasePages = casePages.stream().filter(CasePage ->
                    CasePage.getCreator().equals(pageQueryPageDTO.getParams().getCreator())).collect(Collectors.toList());
            PageInfo<CasePage> pagePageInfo = new PageInfo<>(newCasePages);
            return pageInfoToPageResponse(pagePageInfo);
        }

        PageInfo<CasePage> pagePageInfo = new PageInfo<>(casePages);
        pagePageInfo.setTotal(page.getTotal());
        return pageInfoToPageResponse(pagePageInfo);
    }

    @Override
    public List<CasePage> getCaseByModuleId(Long id) {
        //所有用户
        List<UserEntity> userEntities = userMapper.selectList(new QueryWrapper<>());
        //匹配module
        List<ModuleEntity> moduleEntities = moduleService.selectModuleAndSubmodule(id, true);
        List<Long> moduleIds = moduleEntities.stream().map(ModuleEntity::getId).collect(Collectors.toList());
        // 根据模块ID匹配case
        List<CaseEntity> caseEntities = caseMapper.selectByModuleIds(moduleIds);
        // set 创建人和所属模块
        List<CasePage> casePages = listObjectCopyProperty(caseEntities, CasePage.class);
        if (casePages != null) {
            casePages.forEach(casePage -> {
                userEntities.forEach(userEntity -> {
                    if (Objects.equals(casePage.getTesterId(), userEntity.getId())) {
                        casePage.setCreator(userEntity.getNickname());
                    }
                });
                moduleEntities.forEach(moduleEntity -> {
                    if (Objects.equals(moduleEntity.getId(), casePage.getModuleId())) {
                        casePage.setModuleName(moduleEntity.getName());
                    }
                });
            });
        }
        return casePages;
    }

    @Override
    public List<CaseDTO> getCaseByCaseIds(List<Long> caseIdList) {
        if (caseIdList.isEmpty()) {
            return null;
        }
        List<CaseEntity> caseEntities = caseMapper.selectBatchIds(caseIdList);
        return listObjectCopyProperty(caseEntities, CaseDTO.class);
    }

    @Override
    public List<CaseDTO> getCaseByModuleIds(List<ModuleEntity> moduleEntities) {
        List<CaseEntity> caseEntities = new ArrayList<>();
        List<Long> moduleIds = moduleEntities.stream().map(ModuleEntity::getId).collect(Collectors.toList());
        if(!moduleIds.isEmpty()){
            caseEntities = caseMapper.selectByModuleIds(moduleIds);
        }

//        for (ModuleEntity moduleEntity : moduleEntities) {
//            caseEntities.addAll(caseMapper.selectByModuleId(moduleEntity.getId()));
//        }
        return listObjectCopyProperty(caseEntities, CaseDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int editCase(CaseDTO caseDTO) {
        CaseEntity caseEntity = copyProperty(caseDTO, CaseEntity.class);
        CaseImageEntity caseImageEntity = copyProperty(caseEntity, CaseImageEntity.class);
        caseImageEntity.setOriginalCaseId(caseEntity.getId());
        caseImageService.updateCaseByOriginalCaseId(caseImageEntity);
        return caseMapper.updateById(caseEntity);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteCase(Long id) {
        caseImageService.deleteCaseByOriginalCaseId(id);
        return caseMapper.deleteById(id);
    }

    @Override
    public int batchDeleteCase(DeleteCase caseIdS) {
        if (caseIdS == null || caseIdS.getCaseIds().size() == 0) {
            return 0;
        }
//        List<CaseEntity> caseEntities = listObjectCopyProperty(caseDTOS, CaseEntity.class);
//        assert caseEntities != null;
        List<String> stringCollect = caseIdS.getCaseIds();
        List<Long> collect = stringCollect.stream().mapToLong(s -> Long.parseLong(s.trim())).boxed().collect(Collectors.toList());
        // 删除镜像表数据
        imageCaseMapper.batchDeleteByOriginalCaseId(collect);
        return caseMapper.deleteBatchIds(collect);
    }

    @Override
    public Boolean checkCaseByModuleId(Long moduleId) {
        //匹配module
        List<ModuleEntity> moduleEntities = moduleService.selectModuleAndSubmodule(moduleId, true);
        //所有case
        List<CaseEntity> caseEntities = caseMapper.selectList(new QueryWrapper<>());
        // 检查传入的moduleId是否存在case中
        boolean b = caseEntities.stream().anyMatch(caseEntity -> moduleEntities.stream().anyMatch(
                moduleEntity -> moduleEntity.getId().equals(caseEntity.getModuleId())
        ));
        return b;
    }

    @Override
    public int batchUpdateCases(List<CaseDTO> caseDTOS) {
        if (caseDTOS.size() == 0) {
            return 0;
        }
        List<CaseEntity> caseEntities = listObjectCopyProperty(caseDTOS, CaseEntity.class);
        // 更新case镜像表数据
        List<CaseImageEntity> caseImageEntities = listObjectCopyProperty(caseDTOS, CaseImageEntity.class);
        assert caseImageEntities != null;
        for (CaseImageEntity caseImageEntity : caseImageEntities) {
            caseImageEntity.setOriginalCaseId(caseImageEntity.getId());
            caseImageService.updateCaseByOriginalCaseId(caseImageEntity);
        }
        return caseMapper.batchUpdate(caseEntities);
    }

    @Override
    public int deleteCaseByModule(long moduleId) {

        return caseMapper.deleteCaseByModuleId(moduleId);
    }


    @Override
    public Map<Long, List<XmindCaseDTO>> spliceCaseTree(List<CaseDTO> caseDTOS) {
        Map<Long, List<CaseDTO>> caseMaps = caseDTOS.stream().collect(Collectors.groupingBy(CaseDTO::getModuleId));
        Map<Long, List<XmindCaseDTO>> caseTreeMaps = new HashMap<>();
        String[] nullString = {""};
        caseMaps.forEach((k, v) -> {
            List<XmindCaseDTO> caseDTOList = new ArrayList<>();
            if (!v.isEmpty()){
                v.forEach(caseDTO -> {
                    // 用例
                    XmindCaseDTO sonXmindCaseDTO = new XmindCaseDTO();
                    sonXmindCaseDTO.setTitle(caseDTO.getTitle());
                    sonXmindCaseDTO.setType(XmindNodeType.CASE);
                    sonXmindCaseDTO.setId(caseDTO.getId());
                    sonXmindCaseDTO.setParentId(caseDTO.getModuleId());

                    // 前置条件
                    String precondition = caseDTO.getPrecondition();

                    XmindCaseDTO xmindPrecondition = new XmindCaseDTO();
                    xmindPrecondition.setId(IdWorker.getId());
                    xmindPrecondition.setTitle(precondition);
                    xmindPrecondition.setType(XmindNodeType.CASE_PRECONDITION);
                    xmindPrecondition.setParentId(caseDTO.getId());
                    sonXmindCaseDTO.getChildren().add(xmindPrecondition);

                    // 备注
                    String remark = caseDTO.getRemark();

                    XmindCaseDTO xmindRemark = new XmindCaseDTO();
                    xmindRemark.setId(IdWorker.getId());
                    xmindRemark.setTitle(remark);
                    xmindRemark.setType(XmindNodeType.CASE_REMARK);
                    xmindRemark.setParentId(caseDTO.getId());
                    sonXmindCaseDTO.getChildren().add(xmindRemark);


                    List<CaseStepExpect> stepExpect = caseDTO.getStepExpect();
                    String s1 = JSON.toJSONString(stepExpect);
                    List<CaseStepExpect> caseStepExpects = JSON.parseArray(s1, CaseStepExpect.class);
                    for (CaseStepExpect caseStepExpect : caseStepExpects) {
                        XmindCaseDTO xmindStep = new XmindCaseDTO();
                        // 步骤
                        String step = caseStepExpect.getStep();
                        xmindStep.setId(IdWorker.getId());
                        xmindStep.setType(XmindNodeType.CASE_STEP);
                        xmindStep.setTitle(step);
                        xmindStep.setParentId(caseDTO.getId());
                        sonXmindCaseDTO.getChildren().add(xmindStep);

                        // 预期结果
                        String expect = caseStepExpect.getExpect();
                        String[] split = expect != null ? expect.split("\\r\\n") : nullString;
                        for (String s : split) {
                            XmindCaseDTO xmindExpect = new XmindCaseDTO();
                            xmindExpect.setId(IdWorker.getId());
                            xmindExpect.setTitle(s);
                            xmindExpect.setType(XmindNodeType.CASE_EXPECT);
                            xmindExpect.setParentId(xmindStep.getId());
                            xmindStep.getChildren().add(xmindExpect);
                        }
                    }
                    caseDTOList.add(sonXmindCaseDTO);
                });
            }

            caseTreeMaps.put(k, caseDTOList);
        });

        return caseTreeMaps;

    }


}
