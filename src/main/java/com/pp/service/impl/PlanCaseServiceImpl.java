package com.pp.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pp.common.UserHolder;
import com.pp.common.enums.business.XmindNodeType;
import com.pp.dao.*;
import com.pp.dto.*;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.PlanCaseQuery;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.PlanCasePage;
import com.pp.entity.*;
import com.pp.entity.other.CaseStepExpect;
import com.pp.expection.PpExpection;
import com.pp.service.*;
import com.pp.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.pp.common.constants.CommonError.EXECUTE_INFO_ERROR;
import static com.pp.utils.BeanUtils.listObjectCopyProperty;
import static com.pp.utils.PageUtils.pageInfoToPageResponse;
import static com.pp.utils.BeanUtils.copyProperty;

@Service
@Slf4j
public class PlanCaseServiceImpl implements PlanCaseService {
    @Resource
    private PlanCaseMapper planCaseMapper;
    @Resource
    private CaseImageService caseImageService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private CaseService caseService;
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private ImageCaseMapper imageCaseMapper;
    @Resource
    private PlanCaseService planCaseService;
    @Resource
    private UserService userService;
    @Resource
    private PlanCaseRecordService planCaseRecordService;

    @Override
    public List<PlanCaseDTO> getPlanCaseByPlanId(Long id) {
        List<PlanCaseEntity> planCaseEntities = planCaseMapper.selectByTestPlanId(id);
        return listObjectCopyProperty(planCaseEntities, PlanCaseDTO.class);
    }

//    @Override
//    public PageResponse<PlanCasePage> getPlanCaseList(PageDTO<PlanCaseQuery> planCaseQueryPageDTO) {
//        List<PlanCaseEntity> planCaseList = planCaseMapper.selectByTestPlanId(planCaseQueryPageDTO.getParams().getPlanId());
//        log.info("cases总数："+planCaseList.size());
//        int num = 0;
//        if ((planCaseList.size() == 0) || (null == planCaseList)) {
//            return null;
//        }
//        List<PlanCasePage> PlanCasePageList = new ArrayList<>();
//        for (PlanCaseEntity planCase : planCaseList) {
//            CaseImageEntity caseImageEntity = caseImageService.getCase(planCase.getCaseImageId());
//            if (caseImageEntity == null) {
//                continue;
//            }
//            PlanCasePage planCasePage = new PlanCasePage();
//            org.springframework.beans.BeanUtils.copyProperties(caseImageEntity, planCasePage);
//            planCasePage.setId(planCase.getId());
//            planCasePage.setCaseImageId(planCase.getCaseImageId());
//            planCasePage.setIsPass(planCase.getIsPass());
//            planCasePage.setIsExecute(planCase.getIsExecute());
//            planCasePage.setExecuteName(planCase.getExecuteName());
//            planCasePage.setUpdateTime(planCase.getUpdateTime());
//            ModuleDTO moduleDTO = moduleService.getModule(planCasePage.getModuleId());
//            if ((moduleDTO != null) && (StringUtils.isNotBlank(moduleDTO.getName()))) {
//                planCasePage.setModuleName(moduleDTO.getName());
//            }
//            if (null != planCasePage.getTesterId()) {
//                UserDTO user = userService.getUser(planCasePage.getTesterId());
//                planCasePage.setTesterName(user.getNickname());
//            }
//            PlanCasePageList.add(planCasePage);
//        }
//        PageInfo<PlanCasePage> pagePageInfoV2 = new PageInfo<>(PlanCasePageList);
//        //return pageInfoToPageResponse(pagePageInfo);
//        return pageInfoToPageResponse(pagePageInfoV2);
//    }

    @Override
    public PageResponse<PlanCasePage> getPlanCaseList(PageDTO<PlanCaseQuery> planCaseQueryPageDTO) {
        PageHelper.startPage(planCaseQueryPageDTO.getPageNum(), planCaseQueryPageDTO.getPageSize());
        log.info("参数为："+planCaseQueryPageDTO.getParams().toString());
        Page<PlanCaseEntity> planCaseList = planCaseMapper.planCasePageLists(planCaseQueryPageDTO.getParams());
        Page<PlanCasePage> planCasePageList = new Page<>();
        if (planCaseList.isEmpty()) {
            return pageInfoToPageResponse(new PageInfo<>(planCasePageList));
        }
        List<Long> caseImageIds = new ArrayList<>();
        for (PlanCaseEntity planCase : planCaseList) {
            caseImageIds.add(planCase.getCaseImageId());
        }

        List<CaseImageEntity> caseImageList = caseImageService.getCaseList(caseImageIds);
        List<Long> modulesIds = caseImageList.stream().map(CaseImageEntity::getModuleId).distinct().collect(Collectors.toList());

        List<ModuleEntity> modules = moduleService.selectModulesByIds(modulesIds);

        Map<Long,String> moduleMaps = modules.stream().collect(Collectors.toMap(ModuleEntity::getId,ModuleEntity::getName));

        Map<Long, CaseImageEntity> caseImageMap = new HashMap<>();
        for (CaseImageEntity caseImage : caseImageList) {
            caseImageMap.put(caseImage.getId(), caseImage);
        }

        List<Long> testerIds = caseImageList.stream().map(CaseImageEntity::getTesterId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        List<Long> OwnerIds = planCaseList.stream().map(PlanCaseEntity::getOwnerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        List<Long> Ids = new ArrayList<>();
        Ids.addAll(testerIds);
        Ids.addAll(OwnerIds);
        List<UserEntity> userEntities = userService.selectUsersByIds(Ids.stream().distinct().collect(Collectors.toList()));
        Map<Long,String> UserMaps = userEntities.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getNickname));
        log.info("cases总数：" + planCaseList.size());


        for (PlanCaseEntity planCase : planCaseList) {
            CaseImageEntity caseImage = caseImageMap.get(planCase.getCaseImageId());
            if (caseImage == null) {
                continue;
            }

            PlanCasePage planCasePage = new PlanCasePage();
            BeanUtils.copyProperty(caseImage, planCasePage);
            planCasePage.setId(planCase.getId());
            planCasePage.setCaseImageId(planCase.getCaseImageId());
            planCasePage.setIsPass(planCase.getIsPass());
            planCasePage.setIsExecute(planCase.getIsExecute());
            planCasePage.setExecuteName(planCase.getExecuteName());
            planCasePage.setUpdateTime(planCase.getUpdateTime());
            planCasePage.setOwnerId(planCase.getOwnerId());
            planCasePage.setModuleName(moduleMaps.get(planCasePage.getModuleId()));


            if (planCasePage.getTesterId() != null) {
                planCasePage.setTesterName(UserMaps.get(planCasePage.getTesterId()));
            }

            if (null != planCasePage.getOwnerId()&&planCasePage.getOwnerId()>0) {
                planCasePage.setOwnerName(UserMaps.get(planCasePage.getOwnerId()));
            }
            planCasePageList.add(planCasePage);
        }
        planCasePageList.setPageSize(planCaseList.getPageSize());
        planCasePageList.setPageNum(planCaseList.getPageNum());
        planCasePageList.setTotal(planCaseList.getTotal());

        PageInfo<PlanCasePage> pageInfo = new PageInfo<>(planCasePageList);
        return pageInfoToPageResponse(pageInfo);
    }


    @Override
    public int deletePlanCaseByPlanId(Long id) {
        return planCaseMapper.deleteByPlanId(id);
    }

    @Override
    public int deleteByCaseImageId(Long id) {
        return planCaseMapper.deleteByCaseImageId(id);
    }

    @Override
    public List<PlanCaseEntity> addPlanCase(TestPlanEntity testPlanEntity, List<CaseImageEntity> caseImageEntities) {
        List<PlanCaseEntity> planCaseEntities = new ArrayList<>();
        for (CaseImageEntity caseImageEntity : caseImageEntities) {
            Date date = new Date();
            PlanCaseEntity build = PlanCaseEntity.builder()
                    .planId(testPlanEntity.getId())
                    .caseImageId(caseImageEntity.getId())
                    .build();
            build.setId(IdWorker.getId());
            build.setCreateTime(date);
            build.setUpdateTime(date);
            planCaseEntities.add(build);
        }
        if (planCaseEntities.size() > 0) {
            planCaseMapper.batchInsert(planCaseEntities);
        }
        return planCaseEntities;
    }

    @Override
    public PlanCaseEntity addOnePlanCase(TestPlanEntity testPlanEntity, CaseImageEntity caseImageEntity) {
        PlanCaseEntity planCaseEntity = new PlanCaseEntity();
        Date date = new Date();
        PlanCaseEntity build = PlanCaseEntity.builder().
                planId(testPlanEntity.getId())
                .caseImageId(caseImageEntity.getId())
                .build();
        build.setId(IdWorker.getId());
        build.setCreateTime(date);
        build.setUpdateTime(date);
        planCaseMapper.insert(build);
        return planCaseEntity;

    }

    @Override
    public int editPlanCase(PlanCaseDTO planCaseDTO) {
        PlanCaseEntity planCaseEntity = copyProperty(planCaseDTO, PlanCaseEntity.class);
        PlanCaseRecordsDTO planCaseRecordsDTO = copyProperty(planCaseDTO, PlanCaseRecordsDTO.class);
        List<PlanCaseRecordsDTO> planCaseRecordsDTOS = new ArrayList<>();
        planCaseRecordsDTOS.add(planCaseRecordsDTO);
        UserEntity user = UserHolder.get();
        if (null != user) {
            planCaseEntity.setExecuteName(user.getNickname());
        }
        planCaseRecordService.batchAddPlanCaseRecord(planCaseRecordsDTOS);
        return planCaseMapper.updatePlanCaseByCaseImageId(planCaseEntity);
    }

    @Override
    public int batchUpdatePlanCaseByCaseImageIds(List<PlanCaseDTO> planCaseDTOS) {
        if (planCaseDTOS == null) {
            return 0;
        }
        List<PlanCaseEntity> planCaseEntities = listObjectCopyProperty(planCaseDTOS, PlanCaseEntity.class);
        assert planCaseEntities != null;
        for (PlanCaseEntity planCaseEntity : planCaseEntities) {
            if (planCaseEntity.getIsPass() == null && planCaseEntity.getIsExecute() == null && planCaseEntity.getExecuteTime() == null) {
                throw new PpExpection(EXECUTE_INFO_ERROR);
            }
        }
        return planCaseMapper.batchUpdatePlanCaseByCaseImageIds(planCaseEntities);
    }

    @Override
    public TestPlanXmindCaseDTO getPlanXmindCaseByPlanId(Long id) {
        List<PlanCaseEntity> planCaseEntities = planCaseMapper.selectByTestPlanId(id); // 用例执行状态
        if (planCaseEntities.size() == 0) {
            return null;
        }
        // 获取镜像caseID
        List<Long> imageCaseIds = planCaseEntities.stream().map(PlanCaseEntity::getCaseImageId).distinct().collect(Collectors.toList());
        List<CaseImageEntity> caseImageEntities = imageCaseMapper.selectBatchIds(imageCaseIds); // 批量查询镜像case列表
        List<CaseImageDTO> caseImageDTOS = listObjectCopyProperty(caseImageEntities, CaseImageDTO.class);
        // 获取moduleID
        List<Long> moduleIds = caseImageEntities.stream().map(CaseImageEntity::getModuleId).collect(Collectors.toList());
        List<Long> collect = moduleIds.stream().distinct().collect(Collectors.toList()); // 模块ID列表去重
        log.info("m去重后的模块："+collect.toString());
        // 批量查询module列表
        if (collect.size() == 0) {
            return null;
        }
        List<ModuleEntity> moduleEntities = moduleMapper.selectBatchIds(collect);
        List<ModuleDTO> moduleDTOS = listObjectCopyProperty(moduleEntities, ModuleDTO.class);
        assert moduleDTOS != null;
        Long minCommonModuleId = findRoot(moduleDTOS);
        // 通过最小父节点ID去查询当前节点的模块
        ModuleDTO module = moduleService.getModule(minCommonModuleId);
        // 组装xmind
        TestPlanXmindCaseDTO testPlanXmindCaseDTO = new TestPlanXmindCaseDTO();
        testPlanXmindCaseDTO.setId(minCommonModuleId);
        testPlanXmindCaseDTO.setTitle(module.getName());
        testPlanXmindCaseDTO.setType(XmindNodeType.MODULE);
        testPlanXmindCaseDTO.setPlanId(id);
        // 由于存在最小公共节点与所有case所属模块存在隔级的可能，直接组装树会找不到子节点
        // 需要查询所有case的父节点来组成树
        List<Long> ModulesIds = new ArrayList<>();
        moduleDTOS.forEach(e -> ModulesIds.add(e.getParentId()));
//        for (ModuleDTO newModuleDTO : moduleDTOS) {
//            List<ModuleDTO> parentModules = moduleService.selectAllParentModules(newModuleDTO);
//            treeModuleDTOS.addAll(parentModules);
//        }
        List<ModuleDTO> parentModules = moduleService.newSelectAllParentModules(moduleIds);
        List<ModuleDTO> treeModuleDTOS = new ArrayList<>(parentModules);
        log.info("父节点树："+treeModuleDTOS.toString());
        treeModuleDTOS.addAll(moduleDTOS);
        // 去重
        List<ModuleDTO> newTreeModuleDTOS = treeModuleDTOS.stream().distinct().collect(Collectors.toList());
        // 组装模块树
        log.info("testPlanXmindCaseDTO: "+testPlanXmindCaseDTO);
        buildModuleTree(testPlanXmindCaseDTO, newTreeModuleDTOS);
        log.info("组装模块树！");
        // 组装case树
        buildCaseTree(testPlanXmindCaseDTO, caseImageDTOS);
        log.info("case树组装完毕！");
        // 组装case执行情况
        buildCaseExecuteInfo(testPlanXmindCaseDTO, planCaseEntities);
        log.info("case执行情况组装完毕！");
        return testPlanXmindCaseDTO;
    }

    /**
     * 更新xmindCase
     *
     * @param testPlanXmindCaseDTO
     * @return
     */
    @Override
    public Boolean editPlanXmindCase(TestPlanXmindCaseDTO testPlanXmindCaseDTO) {
        List<ModuleDTO> moduleDTOS = new ArrayList<>();
        List<CaseDTO> caseDTOS = new ArrayList<>();
//        List<PlanCaseDTO> planCaseDTOS = new ArrayList<>();
//        List<PlanCaseDTO> updatePlanCaseDTOS = new ArrayList<>();
//        List<PlanCaseRecordsDTO> planCaseRecordsDTOS;
//        UserEntity user = UserHolder.get();
        analysisModuleTree(testPlanXmindCaseDTO, moduleDTOS);     // 收集module 变更信息
        analysisCaseTree(testPlanXmindCaseDTO, caseDTOS);        // 收集case 变更信息
//        analysisCaseStatus(testPlanXmindCaseDTO, planCaseDTOS);  // 收集case执行状态

//        Collections.sort(planCaseDTOS, new Comparator<PlanCaseDTO>() {
//            @Override
//            public int compare(PlanCaseDTO o1, PlanCaseDTO o2) {
//                return o1.getCaseImageId().compareTo(o2.getCaseImageId());
//            }
//        });
//
//        log.info("排序后的数据：" + planCaseDTOS);
//        // 更新模块
//
//        List<PlanCaseEntity> planCaseEntityList = planCaseMapper.selectByTestPlanId(testPlanXmindCaseDTO.getPlanId());
//
//        Collections.sort(planCaseEntityList, new Comparator<PlanCaseEntity>() {
//            @Override
//            public int compare(PlanCaseEntity o1, PlanCaseEntity o2) {
//                return o1.getCaseImageId().compareTo(o2.getCaseImageId());
//            }
//        });
//
//        log.info("排序后的数据：" + planCaseEntityList);
//
//        for (int i = 0; i < planCaseDTOS.size(); i++) {
//            PlanCaseEntity existedPlanCase = planCaseEntityList.get(i);
//
//            /**
//             * 判断条件
//             */
//            if (!planCaseDTOS.get(i).toString().equals(existedPlanCase.toString())
//                    && planCaseDTOS.get(i).getIsExecute() != null && planCaseDTOS.get(i).getIsExecute() != 0) {
//                planCaseDTOS.get(i).setExecuteName(user.getNickname());
//                log.info("planCaseDTOS.get(i).toString():" + planCaseDTOS.get(i).toString());
//                log.info("existedPlanCase.toString():" + existedPlanCase.toString());
////            if (!(planCaseDTOS.get(i).toString().equals(existedPlanCase.toString())) ||
//                    (Objects.nonNull(planCaseDTOS.get(i).getIsExecute()) &&
//                            !(Objects.requireNonNull(user).getNickname().equals(existedPlanCase.getExecuteName())))){



//                updatePlanCaseDTOS.add(planCaseDTOS.get(i));
//            }
//
//        }
//        log.info("入参为：" + planCaseDTOS);

        moduleService.batchUpdateModules(moduleDTOS);
        // 更新case
        caseService.batchUpdateCases(caseDTOS);

//        if (CollectionUtils.isEmpty(updatePlanCaseDTOS)){
//            return true;
//        }
//
//        List<PlanCaseDTO> newPlanCaseDTOS = updatePlanCaseDTOS.stream().filter(i -> Objects.nonNull(i.getIsExecute())).collect(Collectors.toList());
//        planCaseRecordsDTOS = listObjectCopyProperty(newPlanCaseDTOS, PlanCaseRecordsDTO.class);

//        planCaseRecordService.batchAddPlanCaseRecord(planCaseRecordsDTOS);

//        log.info("有修改的数据：" + updatePlanCaseDTOS);


        // 更新case执行状态
//        planCaseService.batchUpdatePlanCaseByCaseImageIds(updatePlanCaseDTOS);
        return true;
    }

    @Override
    public Long findLCA(Long planId) {
        List<PlanCaseEntity> planCaseEntities = planCaseMapper.selectByTestPlanId(planId);
        // 获取镜像caseID
        List<Long> imageCaseIds = planCaseEntities.stream().map(PlanCaseEntity::getCaseImageId).collect(Collectors.toList());
        List<CaseImageEntity> caseImageEntities = imageCaseMapper.selectBatchIds(imageCaseIds); // 批量查询镜像case列表
        // 获取moduleID
        List<Long> moduleIds = caseImageEntities.stream().map(CaseImageEntity::getModuleId).collect(Collectors.toList());
        List<Long> collect = moduleIds.stream().distinct().collect(Collectors.toList()); // 模块ID列表去重
        // 批量查询module列表
        List<ModuleEntity> moduleEntities = moduleMapper.selectBatchIds(collect);
        List<ModuleDTO> moduleDTOS = listObjectCopyProperty(moduleEntities, ModuleDTO.class);
        assert moduleDTOS != null;
        return findRoot(moduleDTOS);
    }

    /**
     * 找到最小公共父节点
     *
     * @param moduleDTOS
     * @return
     */
    public Long findRoot(List<ModuleDTO> moduleDTOS) {
        // 如果只有一个module直接返回
        if (moduleDTOS.size() == 1) {
            return moduleDTOS.stream().findFirst().get().getId();
        }
        // 根据模块的parentId去分组
        Map<Long, List<ModuleDTO>> groupModuleDTOMap = groupByParentId(moduleDTOS);
        log.info("过滤前的moduleDTOS为："+moduleDTOS.toString());
        // 如果moduleDTOS存在某个module的ID==groupModuleDTOMap中的Key，说明moduleDTOS中存在父级与子级的关系，删除子模块
        for (ModuleDTO moduleDTO : moduleDTOS) {
            groupModuleDTOMap.remove(moduleDTO.getId());
        }
        // 取出过滤后的moduleDTOS
        List<ModuleDTO> newModuleDTOS = new ArrayList<>();
        for (Map.Entry<Long, List<ModuleDTO>> entry : groupModuleDTOMap.entrySet()) {
            List<ModuleDTO> value = entry.getValue();
            newModuleDTOS.addAll(value);
        }
        log.info("过滤后的moduleDTOS为："+newModuleDTOS.toString());
        // 查询每个module的所有父节点,当前模块ID为key,放在map中
        Map<Long, List<ModuleDTO>> parentMap = new HashMap<>();
        List<Long> ModulesIds = new ArrayList<>();
        moduleDTOS.forEach(e -> ModulesIds.add(e.getParentId()));
        List<ModuleDTO> allParentModules = moduleService.newSelectAllParentModules(ModulesIds);

        newModuleDTOS.forEach(e -> {
            List<ModuleDTO> parentModules = new ArrayList<>();

            ModuleDTO parentModule = allParentModules.stream().filter(b -> e.getParentId().equals(b.getId())).findFirst().orElse(null);
            while (parentModule != null){
                parentModules.add(parentModule);
                ModuleDTO finalParentModule = parentModule;
                parentModule = allParentModules.stream().filter(b -> finalParentModule.getParentId().equals(b.getId())).findFirst().orElse(null);
            }
            parentMap.put(e.getId(),parentModules);
        });

        log.info("模块映射："+parentMap);


//        for (ModuleDTO newModuleDTO : newModuleDTOS) {
//            List<ModuleDTO> parentModules = moduleService.selectAllParentModules(newModuleDTO);
//            parentMap.put(newModuleDTO.getId(), parentModules);
//        }
        List<ModuleDTO> maxSize = new ArrayList<>();
        // 找父节点最多的模块
        for (Map.Entry<Long, List<ModuleDTO>> entry : parentMap.entrySet()) {
            List<ModuleDTO> value = entry.getValue();
            if (value.size() > maxSize.size()) {
                maxSize = value;
            }
        }
        // 找父节点最少的模块
        List<ModuleDTO> minSize = maxSize;
        for (Map.Entry<Long, List<ModuleDTO>> entry : parentMap.entrySet()) {
            List<ModuleDTO> value = entry.getValue();
            if (value.size() <= minSize.size()) {
                minSize = value;
            }
        }
        List<Long> maxCollect = maxSize.stream().map(ModuleDTO::getId).collect(Collectors.toList());
        List<Long> minCollect = minSize.stream().map(ModuleDTO::getId).collect(Collectors.toList());
        log.info("最长："+maxCollect);
        log.info("最短："+maxCollect);
        // 遍历长的List去与短的List做比较，他们的父节点相同就返回
        for (Long aLong : maxCollect) {
            if (minCollect.contains(aLong)) {
                return aLong;
            }
        }
        return null;
    }

    private Map<Long, List<ModuleDTO>> groupByParentId(List<ModuleDTO> moduleDTOS) {
        // 根据ParentId分组,合并父节点相同的的module
        Map<Long, List<ModuleDTO>> groupModuleDTOMap = new HashMap<>();
        for (ModuleDTO moduleDTO : moduleDTOS) {
            if (groupModuleDTOMap.containsKey(moduleDTO.getParentId())) {
                groupModuleDTOMap.get(moduleDTO.getParentId()).add(moduleDTO);
            } else {
                List<ModuleDTO> newModuleDTOs = new ArrayList<>();
                newModuleDTOs.add(moduleDTO);
                groupModuleDTOMap.put(moduleDTO.getParentId(), newModuleDTOs);
            }
        }
        return groupModuleDTOMap;
    }

    /**
     * 组装module树
     *
     * @param testPlanXmindCaseDTO
     * @param allModule
     */
    private void buildModuleTree(TestPlanXmindCaseDTO testPlanXmindCaseDTO, List<ModuleDTO> allModule) {
        Long planId = testPlanXmindCaseDTO.getPlanId(); // 测试计划ID
        // 子节点ID

        List<Long> childrenIds = testPlanXmindCaseDTO.getChildren() !=null?testPlanXmindCaseDTO.getChildren().stream().map(TestPlanXmindCaseDTO::getId).collect(Collectors.toList()):new ArrayList<>();
       for (ModuleDTO dto : allModule) {
            if (dto.getParentId().equals(testPlanXmindCaseDTO.getId()) && !childrenIds.contains(dto.getId())) {
                TestPlanXmindCaseDTO sonXmindCaseDTO = new TestPlanXmindCaseDTO();
                BeanUtils.copyProperty(dto, sonXmindCaseDTO);
                sonXmindCaseDTO.setPlanId(planId);
                sonXmindCaseDTO.setTitle(dto.getName());
                sonXmindCaseDTO.setType(XmindNodeType.MODULE);
                testPlanXmindCaseDTO.getChildren().add(sonXmindCaseDTO);
                buildModuleTree(sonXmindCaseDTO, allModule);
            } else {
                continue;
            }
        }
    }

    /**
     * 组装xmind用例
     *
     * @param testPlanXmindCaseDTO
     * @param caseImageDTOS
     */
//    public void buildCaseTree(TestPlanXmindCaseDTO testPlanXmindCaseDTO, List<CaseImageDTO> caseImageDTOS) {
//        Long id = testPlanXmindCaseDTO.getId();
//        Long planId = testPlanXmindCaseDTO.getPlanId(); // 获取测试计划ID
//        List<TestPlanXmindCaseDTO> children = testPlanXmindCaseDTO.getChildren();
//        String defaultPrecondition = "前置条件";
//        String defaultRemark = "备注";
//        // 获取子节点ID
//        List<Long> childrenIds = children.stream().map(TestPlanXmindCaseDTO::getId).collect(Collectors.toList());
//        for (CaseImageDTO caseImageDTO : caseImageDTOS) {
//            if (Objects.equals(caseImageDTO.getModuleId(), id) && !childrenIds.contains(caseImageDTO.getOriginalCaseId())) {
//
//                // 用例
//                TestPlanXmindCaseDTO sonXmindCaseDTO = new TestPlanXmindCaseDTO();
//                sonXmindCaseDTO.setPlanId(planId);
//                sonXmindCaseDTO.setTitle(caseImageDTO.getTitle());
//                sonXmindCaseDTO.setType(XmindNodeType.CASE);
//                sonXmindCaseDTO.setId(caseImageDTO.getOriginalCaseId());
//                sonXmindCaseDTO.setCaseImageId(caseImageDTO.getId());
//                children.add(sonXmindCaseDTO);
//
//                // 前置条件
//                String precondition = caseImageDTO.getPrecondition();
//                if (Objects.nonNull(precondition)){
//                    String[] pr = precondition.split("：");
//                    precondition = pr[0].equalsIgnoreCase("PC") && pr.length==1 ?precondition+defaultPrecondition:precondition;
//
//                }
//
//                TestPlanXmindCaseDTO xmindPrecondition = new TestPlanXmindCaseDTO();
//                xmindPrecondition.setPlanId(planId);
//                xmindPrecondition.setId(IdWorker.getId());
//                xmindPrecondition.setTitle(precondition);
//                xmindPrecondition.setType(XmindNodeType.CASE_PRECONDITION);
//                sonXmindCaseDTO.getChildren().add(xmindPrecondition);
//
//                // 备注
//                String remark = caseImageDTO.getRemark();
//                if (Objects.nonNull(remark)){
//                    String[] re = remark.split("：");
//                    remark = re[0].equalsIgnoreCase("RC") && re.length==1 ?remark+defaultRemark:remark;
//
//                }
//
//                TestPlanXmindCaseDTO xmindRemark = new TestPlanXmindCaseDTO();
//                xmindRemark.setPlanId(planId);
//                xmindRemark.setId(IdWorker.getId());
//                xmindRemark.setTitle(remark);
//                xmindRemark.setType(XmindNodeType.CASE_REMARK);
//                sonXmindCaseDTO.getChildren().add(xmindRemark);
//
//
//                List<CaseStepExpect> stepExpect = caseImageDTO.getStepExpect();
//                String s1 = JSON.toJSONString(stepExpect);
//                List<CaseStepExpect> caseStepExpects = JSON.parseArray(s1, CaseStepExpect.class);
//                for (CaseStepExpect caseStepExpect : caseStepExpects) {
//                    TestPlanXmindCaseDTO xmindStep = new TestPlanXmindCaseDTO();
//                    // 步骤
//                    String step = caseStepExpect.getStep();
//                    xmindStep.setId(IdWorker.getId());
//                    xmindStep.setPlanId(planId);
//                    xmindStep.setType(XmindNodeType.CASE_STEP);
//                    xmindStep.setTitle(step);
//                    sonXmindCaseDTO.getChildren().add(xmindStep);
//
//                    TestPlanXmindCaseDTO xmindExpect = new TestPlanXmindCaseDTO();
//                    // 预期结果
//                    String expect = caseStepExpect.getExpect();
//                    String[] nullString = {""};
//                    String[] split = expect !=null?expect.split("\\r\\n"):nullString;
//                    for (String s : split) {
//                        xmindExpect.setId(IdWorker.getId());
//                        xmindExpect.setPlanId(planId);
//                        xmindExpect.setTitle(s);
//                        xmindExpect.setType(XmindNodeType.CASE_EXPECT);
//                        xmindStep.getChildren().add(xmindExpect);
//                    }
//                }
//            }
//
//            // 递归组装xmindCase
//            for (TestPlanXmindCaseDTO child : children) {
//                if (child.getType().equals(XmindNodeType.MODULE)) {
//                    buildCaseTree(child, caseImageDTOS);
//                }
//            }
//        }
//    }
    public void buildCaseTree(TestPlanXmindCaseDTO testPlanXmindCaseDTO, List<CaseImageDTO> caseImageDTOS) {
        Long planId = testPlanXmindCaseDTO.getPlanId(); // 获取测试计划ID
        Stack<TestPlanXmindCaseDTO> stack = new Stack<>();
        stack.push(testPlanXmindCaseDTO);
        Map<Long, List<TestPlanXmindCaseDTO>> XmindCaseData = spliceCaseTree(planId,caseImageDTOS);
        Set<Long> allModule = XmindCaseData.keySet();
        log.info("模块集合："+allModule);

        while (!stack.isEmpty()){
            TestPlanXmindCaseDTO current = stack.pop();
            Long id = current.getId();
            List<TestPlanXmindCaseDTO> children = current.getChildren();
            if (allModule.contains(id)){
                children.addAll(XmindCaseData.get(id));
                log.info("添加了cases集："+XmindCaseData.get(id));
            }

//            添加子节点到栈中
            for (TestPlanXmindCaseDTO child : children) {
                if (child.getType().equals(XmindNodeType.MODULE)) {
                    stack.push(child);
                }
            }
        }


//        while (!stack.isEmpty() && caseLength !=0) {
//            TestPlanXmindCaseDTO current = stack.pop();
//            Long id = current.getId();
//            List<TestPlanXmindCaseDTO> children = current.getChildren();
//            List<Long> childrenIds = children.stream().map(TestPlanXmindCaseDTO::getId).collect(Collectors.toList());
//            for (CaseImageDTO caseImageDTO : caseImageDTOS) {
//
//                if (Objects.equals(caseImageDTO.getModuleId(), id) && !childrenIds.contains(caseImageDTO.getOriginalCaseId())) {
//                    log.info("拼接的用例"+caseImageDTO);
//                    // 用例
//                    TestPlanXmindCaseDTO sonXmindCaseDTO = new TestPlanXmindCaseDTO();
//                    sonXmindCaseDTO.setPlanId(planId);
//                    sonXmindCaseDTO.setTitle(caseImageDTO.getTitle());
//                    sonXmindCaseDTO.setType(XmindNodeType.CASE);
//                    sonXmindCaseDTO.setId(caseImageDTO.getOriginalCaseId());
//                    sonXmindCaseDTO.setCaseImageId(caseImageDTO.getId());
//                    sonXmindCaseDTO.setLevel(caseImageDTO.getLevel());
//                    children.add(sonXmindCaseDTO);
//
//                    // 前置条件
//                    String precondition = caseImageDTO.getPrecondition();
//                    if (Objects.nonNull(precondition)){
//                        String[] pr = precondition.split("：");
//                        precondition = pr[0].equalsIgnoreCase("PC") && pr.length==1 ?precondition+defaultPrecondition:precondition;
//
//                    }
//
//                    TestPlanXmindCaseDTO xmindPrecondition = new TestPlanXmindCaseDTO();
//                    xmindPrecondition.setPlanId(planId);
//                    xmindPrecondition.setId(IdWorker.getId());
//                    xmindPrecondition.setTitle(precondition);
//                    xmindPrecondition.setType(XmindNodeType.CASE_PRECONDITION);
//                    sonXmindCaseDTO.getChildren().add(xmindPrecondition);
//
//                    // 备注
//                    String remark = caseImageDTO.getRemark();
//                    if (Objects.nonNull(remark)){
//                        String[] re = remark.split("：");
//                        remark = re[0].equalsIgnoreCase("RC") && re.length==1 ?remark+defaultRemark:remark;
//
//                    }
//
//                    TestPlanXmindCaseDTO xmindRemark = new TestPlanXmindCaseDTO();
//                    xmindRemark.setPlanId(planId);
//                    xmindRemark.setId(IdWorker.getId());
//                    xmindRemark.setTitle(remark);
//                    xmindRemark.setType(XmindNodeType.CASE_REMARK);
//                    sonXmindCaseDTO.getChildren().add(xmindRemark);
//
//                    List<CaseStepExpect> stepExpect = caseImageDTO.getStepExpect();
//                    String s1 = JSON.toJSONString(stepExpect);
//                    List<CaseStepExpect> caseStepExpects = JSON.parseArray(s1, CaseStepExpect.class);
//                    for (CaseStepExpect caseStepExpect : caseStepExpects) {
//                        TestPlanXmindCaseDTO xmindStep = new TestPlanXmindCaseDTO();
//                        // 步骤
//                        String step = caseStepExpect.getStep();
//                        xmindStep.setId(IdWorker.getId());
//                        xmindStep.setPlanId(planId);
//                        xmindStep.setType(XmindNodeType.CASE_STEP);
//                        xmindStep.setTitle(step);
//                        sonXmindCaseDTO.getChildren().add(xmindStep);
//
//                        TestPlanXmindCaseDTO xmindExpect = new TestPlanXmindCaseDTO();
//                        // 预期结果
//                        String expect = caseStepExpect.getExpect();
//                        String[] nullString = {""};
//                        String[] split = expect !=null?expect.split("\\r\\n"):nullString;
//                        for (String s : split) {
//                            xmindExpect.setId(IdWorker.getId());
//                            xmindExpect.setPlanId(planId);
//                            xmindExpect.setTitle(s);
//                            xmindExpect.setType(XmindNodeType.CASE_EXPECT);
//                            xmindStep.getChildren().add(xmindExpect);
//                        }
//                    }
//                    caseLength--;
//                }
//
//                // 添加子节点到栈中
//                for (TestPlanXmindCaseDTO child : children) {
//                    if (child.getType().equals(XmindNodeType.MODULE)) {
//                        stack.push(child);
//                    }
//                }
//            }
//        }
    }


    /**
     * 组装case的执行情况
     *
     * @param testPlanXmindCaseDTO
     * @param planCaseEntities
     */
    private void buildCaseExecuteInfo(TestPlanXmindCaseDTO testPlanXmindCaseDTO, List<PlanCaseEntity> planCaseEntities) {
        List<TestPlanXmindCaseDTO> children = testPlanXmindCaseDTO.getChildren();
        for (PlanCaseEntity planCaseEntity : planCaseEntities) {
            if (planCaseEntity.getCaseImageId().equals(testPlanXmindCaseDTO.getCaseImageId())) {
                testPlanXmindCaseDTO.setIsExecute(planCaseEntity.getIsExecute());
                testPlanXmindCaseDTO.setExecuteTime(planCaseEntity.getExecuteTime());
                testPlanXmindCaseDTO.setIsPass(planCaseEntity.getIsPass());
            }
        }
        // 递归塞入执行信息
        for (TestPlanXmindCaseDTO child : children) {
            if (child.getType().equals(XmindNodeType.CASE) || child.getType().equals(XmindNodeType.MODULE)) {
                buildCaseExecuteInfo(child, planCaseEntities);
            }
        }
    }

//    private void buildCaseExecuteInfo(TestPlanXmindCaseDTO testPlanXmindCaseDTO, List<PlanCaseEntity> planCaseEntities) {
//        List<TestPlanXmindCaseDTO> children = new ArrayList<>();
//        Stack<TestPlanXmindCaseDTO> stack = new Stack<>();
//        stack.push(testPlanXmindCaseDTO);
//        long planCaseSize = planCaseEntities.size();
//        while (!stack.isEmpty() && planCaseSize != 0){
//            TestPlanXmindCaseDTO testPlanXmindCaseDTO1 = stack.pop();
//            children = testPlanXmindCaseDTO1.getChildren();
//            for (PlanCaseEntity planCaseEntity : planCaseEntities) {
//                log.info("测试计划："+planCaseEntity.getIsPass());
//                log.info("测试计划xmind："+testPlanXmindCaseDTO1);
//                if (planCaseEntity.getCaseImageId().equals(testPlanXmindCaseDTO1.getCaseImageId())) {
//                    log.info("测试计划cases："+planCaseEntity.getIsPass());
//                    testPlanXmindCaseDTO1.setIsExecute(planCaseEntity.getIsExecute());
//                    testPlanXmindCaseDTO1.setExecuteTime(planCaseEntity.getExecuteTime());
//                    testPlanXmindCaseDTO1.setIsPass(planCaseEntity.getIsPass());
//                    planCaseSize--;
//                }
//        }
//        }
//        // 递归塞入执行信息
//        for (TestPlanXmindCaseDTO child : children) {
//            if (child.getType().equals(XmindNodeType.CASE) || child.getType().equals(XmindNodeType.MODULE)) {
//                stack.push(child);
//            }
//        }
//    }




    /**
     * 收集测试计划对模块树的修改 todo 目前只能支持对模块进行修改名称操作，（删除模块，修改模块的父节点还不支持）
     *
     * @param testPlanXmindCaseDTO
     * @param moduleDTOS
     */
    public void analysisModuleTree(TestPlanXmindCaseDTO testPlanXmindCaseDTO, List<ModuleDTO> moduleDTOS) {
        List<TestPlanXmindCaseDTO> children = testPlanXmindCaseDTO.getChildren();
        if (testPlanXmindCaseDTO.getType() == XmindNodeType.MODULE) {
            ModuleDTO moduleDTO = new ModuleDTO();
            moduleDTO.setId(testPlanXmindCaseDTO.getId());
            moduleDTO.setName(testPlanXmindCaseDTO.getTitle());
            moduleDTOS.add(moduleDTO);
        }
        for (TestPlanXmindCaseDTO child : children) {
            if (child.getType().equals(XmindNodeType.MODULE)) {
                analysisModuleTree(child, moduleDTOS);
            }
        }
    }


    /**
     * 收集测试计划中用用例的修改，用于更新
     *
     * @param testPlanXmindCaseDTO
     * @param caseDTOS
     */
    public void analysisCaseTree(TestPlanXmindCaseDTO testPlanXmindCaseDTO, List<CaseDTO> caseDTOS) {
        List<TestPlanXmindCaseDTO> children = testPlanXmindCaseDTO.getChildren();
        List<CaseStepExpect> caseStepExpects = new ArrayList<>();
        if (testPlanXmindCaseDTO.getType() == XmindNodeType.CASE) {
            CaseDTO caseDTO = new CaseDTO();
            caseDTO.setId(testPlanXmindCaseDTO.getId()); // 用例ID
            caseDTO.setTitle(testPlanXmindCaseDTO.getTitle()); // 用例标题
            List<TestPlanXmindCaseDTO> caseChildren = testPlanXmindCaseDTO.getChildren();
            for (TestPlanXmindCaseDTO caseChild : caseChildren) {
                if (caseChild.getType().equals(XmindNodeType.CASE_PRECONDITION)) {
                    caseDTO.setPrecondition(caseChild.getTitle()); // 前置条件
                } else if (caseChild.getType().equals(XmindNodeType.CASE_REMARK)) {
                    caseDTO.setRemark(caseChild.getTitle()); // 备注
                } else if (caseChild.getType().equals(XmindNodeType.CASE_STEP)) {
//                    List<CaseStepExpect> caseStepExpects = new ArrayList<>(); 对象作用域定义错误，导致对象被覆盖
                    CaseStepExpect caseStepExpect = new CaseStepExpect();
                    caseStepExpect.setStep(caseChild.getTitle()); // 步骤
                    List<TestPlanXmindCaseDTO> expectList = caseChild.getChildren();
                    for (TestPlanXmindCaseDTO dto : expectList) {
                        caseStepExpect.setExpect(dto.getTitle()); // 预期结果
                    }
                    caseStepExpects.add(caseStepExpect);
                    caseDTO.setStepExpect(caseStepExpects);
                }
            }
            caseDTOS.add(caseDTO);
        }
        for (TestPlanXmindCaseDTO child : children) {
            if (child.getType().equals(XmindNodeType.CASE) || child.getType().equals(XmindNodeType.MODULE)) {
                analysisCaseTree(child, caseDTOS);
            }
        }
    }

    /**
     * 收集测试计划中的用例执行情况
     *
     * @param testPlanXmindCaseDTO
     * @param planCaseDTOS
     */
    public void analysisCaseStatus(TestPlanXmindCaseDTO testPlanXmindCaseDTO, List<PlanCaseDTO> planCaseDTOS) {
        List<TestPlanXmindCaseDTO> children = testPlanXmindCaseDTO.getChildren();
        if (testPlanXmindCaseDTO.getType() == XmindNodeType.CASE) {
            // 记录执行状态
            PlanCaseDTO planCaseDTO = new PlanCaseDTO();
            planCaseDTO.setCaseImageId(testPlanXmindCaseDTO.getCaseImageId());
            planCaseDTO.setPlanId(testPlanXmindCaseDTO.getPlanId());
            planCaseDTO.setIsExecute(testPlanXmindCaseDTO.getIsExecute());
            planCaseDTO.setExecuteTime(testPlanXmindCaseDTO.getExecuteTime());
            planCaseDTO.setIsPass(testPlanXmindCaseDTO.getIsPass());
            planCaseDTOS.add(planCaseDTO);
        }
        for (TestPlanXmindCaseDTO child : children) {
            if (child.getType().equals(XmindNodeType.CASE) || child.getType().equals(XmindNodeType.MODULE)) {
                analysisCaseStatus(child, planCaseDTOS);
            }
        }
    }

    /**
     * 过滤已删除的测试计划中的cases
     * @param planId 测试计划Id
     * @return
     */
    @Override
    public List<PlanCaseEntity> filterIsDeletePlanCase(Long planId) {

        List<PlanCaseEntity> planCaseEntities = planCaseMapper.selectByTestPlanId(planId);

        if (CollectionUtils.isEmpty(planCaseEntities)) {
            return new ArrayList<>();
        }

        List<Long> caseImageIds = new ArrayList<>();
        planCaseEntities.forEach(e -> caseImageIds.add(e.getCaseImageId()));


        List<CaseImageEntity> caseImageList = caseImageService.getCaseList(caseImageIds);

        if (CollectionUtils.isNotEmpty(caseImageList)) {


            if (planCaseEntities.size() == caseImageList.size()) {
                return planCaseEntities;
            }

            List<Long> newCaseImageIds = new ArrayList<>();
            caseImageList.forEach(e -> newCaseImageIds.add(e.getId()));


            List<PlanCaseEntity> newPlanCaseEntities = new ArrayList<>();

//        for (PlanCaseEntity planCase:planCaseEntities) {
//            CaseImageEntity caseImageEntity = caseImageService.getCase(planCase.getCaseImageId());
//            if (Objects.isNull(caseImageEntity)){
//                continue;
//            }
//
//            newPlanCaseEntities.add(planCase);
//
//        }
            for (PlanCaseEntity planCase : planCaseEntities) {
                if (newCaseImageIds.contains(planCase.getCaseImageId())) {
                    newPlanCaseEntities.add(planCase);
                }

            }

            return newPlanCaseEntities;
        }

        return new ArrayList<>();
    }

    @Override
    public int distributeOwner(OwnerDTO ownerDTO) {
        for(Long id:ownerDTO.getCaseIds()){
            planCaseMapper.updateOwnerId(id,ownerDTO.getOwnerId());
        }
        return 1;
    }

    /**
     * 组装用例树
     * @param caseImageDTOS
     * @return List<TestPlanXmindCaseDTO>
     */
    @Override
    public Map<Long, List<TestPlanXmindCaseDTO>> spliceCaseTree(Long planId, List<CaseImageDTO> caseImageDTOS) {
        // 根据模块分组用例
        Map<Long,List<CaseImageDTO>> CaseMaps = caseImageDTOS.stream().collect(Collectors.groupingBy(CaseImageDTO::getModuleId));
        log.info("分组用例: "+CaseMaps);
        log.info("分组后长度"+CaseMaps);
        if (CaseMaps.isEmpty()){
            return new HashMap<>();
        }
        String defaultPrecondition = "前置条件";
        String defaultRemark = "备注";
        Map<Long, List<TestPlanXmindCaseDTO>> testPlanXmindData = new HashMap<>();
        CaseMaps.forEach((k,v) -> {
            List<TestPlanXmindCaseDTO> PlanXmindCaseList = new ArrayList<>();
            v.forEach(caseImageDTO -> {
                TestPlanXmindCaseDTO sonXmindCaseDTO = new TestPlanXmindCaseDTO();
                sonXmindCaseDTO.setTitle(caseImageDTO.getTitle());
                sonXmindCaseDTO.setType(XmindNodeType.CASE);
                sonXmindCaseDTO.setId(caseImageDTO.getOriginalCaseId());
                sonXmindCaseDTO.setCaseImageId(caseImageDTO.getId());
                sonXmindCaseDTO.setLevel(caseImageDTO.getLevel());
                sonXmindCaseDTO.setPlanId(planId);
//                    children.add(sonXmindCaseDTO);

                    // 前置条件
                String precondition = caseImageDTO.getPrecondition();
                if (Objects.nonNull(precondition)){
                    String[] pr = precondition.split("：");
                    precondition = pr[0].equalsIgnoreCase("PC") && pr.length==1 ?precondition+defaultPrecondition:precondition;

                }

                TestPlanXmindCaseDTO xmindPrecondition = new TestPlanXmindCaseDTO();
                xmindPrecondition.setPlanId(planId);
                xmindPrecondition.setId(IdWorker.getId());
                xmindPrecondition.setTitle(precondition);
                xmindPrecondition.setType(XmindNodeType.CASE_PRECONDITION);
                sonXmindCaseDTO.getChildren().add(xmindPrecondition);

                // 备注
                String remark = caseImageDTO.getRemark();
                if (Objects.nonNull(remark)){
                    String[] re = remark.split("：");
                    remark = re[0].equalsIgnoreCase("RC") && re.length==1 ?remark+defaultRemark:remark;

                }

                TestPlanXmindCaseDTO xmindRemark = new TestPlanXmindCaseDTO();
                xmindRemark.setId(IdWorker.getId());
                xmindRemark.setTitle(remark);
                xmindRemark.setType(XmindNodeType.CASE_REMARK);
                xmindRemark.setPlanId(planId);
                sonXmindCaseDTO.getChildren().add(xmindRemark);


                List<CaseStepExpect> stepExpect = caseImageDTO.getStepExpect();
                String s1 = JSON.toJSONString(stepExpect);
                List<CaseStepExpect> caseStepExpects = JSON.parseArray(s1, CaseStepExpect.class);
                for (CaseStepExpect caseStepExpect : caseStepExpects) {
                    TestPlanXmindCaseDTO xmindStep = new TestPlanXmindCaseDTO();
                    // 步骤
                    String step = caseStepExpect.getStep();
                    xmindStep.setId(IdWorker.getId());
                    xmindStep.setPlanId(planId);
                    xmindStep.setType(XmindNodeType.CASE_STEP);
                    xmindStep.setTitle(step);
                    sonXmindCaseDTO.getChildren().add(xmindStep);
                    // 预期结果
                    String expect = caseStepExpect.getExpect();
                    String[] nullString = {""};
                    String[] split = expect !=null?expect.split("\\r\\n"):nullString;
                    for (String s : split) {
                        TestPlanXmindCaseDTO xmindExpect = new TestPlanXmindCaseDTO();
                        xmindExpect.setId(IdWorker.getId());
                        xmindExpect.setPlanId(planId);
                        xmindExpect.setTitle(s);
                        xmindExpect.setType(XmindNodeType.CASE_EXPECT);
                        xmindStep.getChildren().add(xmindExpect);
                    }
                };
                PlanXmindCaseList.add(sonXmindCaseDTO);
            }
            );
            testPlanXmindData.put(k,PlanXmindCaseList);
        });

        return testPlanXmindData;
    }
}
