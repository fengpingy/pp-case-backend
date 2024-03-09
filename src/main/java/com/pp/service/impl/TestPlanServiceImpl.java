package com.pp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pp.common.UserHolder;
import com.pp.dao.*;
import com.pp.dto.CaseDTO;
import com.pp.dto.PageInfoStatus;
import com.pp.dto.PlanCaseDTO;
import com.pp.dto.TestPlanDTO;
import com.pp.dto.request.TestPlanStatisticsDTO;
import com.pp.dto.request.TimeDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.TestPlanQuery;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.TestPlanPage;
import com.pp.entity.*;
import com.pp.expection.PpExpection;
import com.pp.service.*;
import com.pp.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pp.common.constants.CommonError.ENTITY_ID_NOT_EXIST;
import static com.pp.utils.PageUtils.pageInfoToPageResponse;

@Service
@Slf4j
public class TestPlanServiceImpl implements TestPlanService {
    @Resource
    private TestPlanMapper testPlanMapper;
    @Resource
    private ModuleService moduleService;
    @Resource
    private CaseService caseService;
    @Resource
    private PlanCaseService planCaseService;
    @Resource
    private CaseImageService caseImageService;

    @Resource
    private ImageCaseMapper imageCaseMapper;

    @Resource
    private UserMapper userMapper;


    @Resource
    private PlanCaseCountMapper planCaseCountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTestPlan(TestPlanDTO testPlanDTO) {
        // 1.定义一个case列表
        // 2.遍历caseIds去moka_cae查询所有的case
        // 3.遍历moduleIds列表取出单个moduleId
        // 4.根据moduleId去moka_module表递归查询到所有的子模块moduleId包含
        // 5.根据moduleId 去moka_case表查询所属的所有case
        // 6.将查询出来的所有case对象添加到原有的case对象
        // 7.先去重，批量添加到moka_case_image表中
        // 8.插入moka_plan_case表
        TestPlanEntity testPlanEntity = BeanUtils.copyProperty(testPlanDTO, TestPlanEntity.class);
        UserEntity userEntity = UserHolder.get();
        testPlanEntity.setCreatorId(userEntity.getId());
        testPlanMapper.insert(testPlanEntity);  // 插入数据到moka_test_plan数据表
        List<Long> caseIds = testPlanDTO.getCaseIds(); // 从DTO拿到caseIdS列表
        List<Long> moduleIds = testPlanDTO.getModuleIds(); // 从DTO拿到moduleIds列表
        List<CaseDTO> caseByCaseIds = new ArrayList<>();
        List<CaseDTO> caseDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(caseIds)) {
            caseByCaseIds = caseService.getCaseByCaseIds(caseIds); //通过caseId查询case
        }
        if (caseByCaseIds != null) {
            caseDTOS.addAll(caseByCaseIds);
        }
        List<ModuleEntity> moduleEntityList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(moduleIds)) {
            for (Long moduleId : moduleIds) {
                moduleEntityList.addAll(moduleService.selectModuleAndSubmodule(moduleId, testPlanDTO.getIsIncludeSubset()));  // 查询子模块
            }
        }
        List<ModuleEntity> collect = moduleEntityList.stream().distinct().collect(Collectors.toList()); // 去重
        List<CaseDTO> caseByModuleIds = caseService.getCaseByModuleIds(collect); // 拿到模块下的用例
        if (caseByModuleIds != null) {
            caseDTOS.addAll(caseByModuleIds);  // 合并根据caseID和模块ID查询到的所有case
        }
        List<CaseDTO> collectCaseDTO = caseDTOS.stream().distinct().collect(Collectors.toList()); // 去重;
        if (!CollectionUtils.isEmpty(collectCaseDTO)) {
            List<CaseImageEntity> caseImageEntities = new ArrayList<>(Objects.requireNonNull(BeanUtils.listObjectCopyProperty(collectCaseDTO, CaseImageEntity.class)));
            List<CaseImageEntity> newCaseImageEntities = caseImageService.addCaseByCaseIds(caseImageEntities);
            planCaseService.addPlanCase(testPlanEntity, newCaseImageEntities);
        }
        return testPlanEntity.getId();
    }

    @Override
    public TestPlanDTO getTestPlan(Long id) {
        TestPlanEntity testPlanEntity = testPlanMapper.selectById(id);
        if (testPlanEntity == null) {
            throw new PpExpection(ENTITY_ID_NOT_EXIST);
        }
        TestPlanDTO testPlanDTO = BeanUtils.copyProperty(testPlanEntity, TestPlanDTO.class);
        List<PlanCaseDTO> planCaseByPlanId = planCaseService.getPlanCaseByPlanId(id);
        if (planCaseByPlanId != null) {
            List<Long> collect = planCaseByPlanId.stream().map(PlanCaseDTO::getCaseImageId).collect(Collectors.toList());
            List<CaseImageEntity> caseImageEntities = imageCaseMapper.selectBatchByIds(collect);
            List<Long> OriginalCaseIds = caseImageEntities.stream().map(CaseImageEntity::getOriginalCaseId).collect(Collectors.toList());
            testPlanDTO.setCaseIds(OriginalCaseIds);
        }
        return testPlanDTO;
    }

    @Override
    public int delTestPlan(Long id) {
        TestPlanEntity testPlanEntity = testPlanMapper.selectById(id);
        return testPlanMapper.deleteById(testPlanEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editTestPlan(TestPlanDTO testPlanDTO) {
        TestPlanEntity newTestPlanEntity = BeanUtils.copyProperty(testPlanDTO, TestPlanEntity.class);
        int i = testPlanMapper.updateById(newTestPlanEntity);
        // 1.根据前端返回的testPlanDTO中的caseIds和moduleIds查询到所有关联的新caseIds；
        // 2.根据前端返回的testPlanDTO中的planId去moka_plan_case表查询所有关联的旧case_image_id；
        // 3.根据case_image_id去moka_case_image查询所有的旧caseIds
        // 4.2个caseIds做diff,从而来新增或者删除部分case
        List<Long> caseIds = testPlanDTO.getCaseIds(); // 从DTO拿到caseIdS列表
        List<Long> moduleIds = testPlanDTO.getModuleIds(); // 从DTO拿到moduleIds列表
        List<CaseDTO> newCaseDTOS = new ArrayList<>();  // 总数据
        // 通过caseIDs去查询数据
        List<CaseDTO> caseByCaseIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(caseIds)) {
            caseByCaseIds = caseService.getCaseByCaseIds(caseIds); //通过caseId查询case
        }
        if (caseByCaseIds != null) {
            newCaseDTOS.addAll(caseByCaseIds);
        }
        // 通过moduleIds去查询数据
        List<ModuleEntity> moduleEntityList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(moduleIds)) {
            for (Long moduleId : moduleIds) {
                moduleEntityList.addAll(moduleService.selectModuleAndSubmodule(moduleId, testPlanDTO.getIsIncludeSubset()));  // 查询子模块
            }
        }
        List<ModuleEntity> collect = moduleEntityList.stream().distinct().collect(Collectors.toList()); // 去重
        List<CaseDTO> caseByModuleIds = caseService.getCaseByModuleIds(collect); // 拿到模块下的用例
        if (caseByModuleIds != null) {
            newCaseDTOS.addAll(caseByModuleIds);
        }
        List<PlanCaseDTO> planCases = planCaseService.getPlanCaseByPlanId(testPlanDTO.getId());
        List<CaseImageEntity> oldCaseImageEntities = new ArrayList<>();  // 老的caseImage列表
        if (planCases != null) {
            for (PlanCaseDTO planCase : planCases) {
                CaseImageEntity aCase = caseImageService.getCase(planCase.getCaseImageId());
                oldCaseImageEntities.add(aCase);
            }
        }
        // 原来测试计划关联的用例为空，新的关联的测试用也为空
        if (oldCaseImageEntities.isEmpty() && newCaseDTOS.isEmpty()) {
            return i;
        }
        // 原来测试计划关联的用例为空，则全部做新增用例
        if (oldCaseImageEntities.isEmpty()) {
            List<CaseImageEntity> caseImageEntities = new ArrayList<>(Objects.requireNonNull(BeanUtils.listObjectCopyProperty(newCaseDTOS, CaseImageEntity.class)));
            // 7.批量添加到moka_case_image表中
            List<CaseImageEntity> addCaseImageEntities = caseImageService.addCaseByCaseIds(caseImageEntities);
            // 8.插入moka_plan_case表
            planCaseService.addPlanCase(newTestPlanEntity, addCaseImageEntities);
            return i;
        }
        // 新的测试计划关联用例为空，则根据planId全部做删除moka_plan_case数据，同时删除moka_case_image数据
        if (newCaseDTOS.isEmpty()) {
            planCaseService.deletePlanCaseByPlanId(newTestPlanEntity.getId());
            List<PlanCaseDTO> planCaseByPlanId = planCaseService.getPlanCaseByPlanId(newTestPlanEntity.getId());
            for (PlanCaseDTO planCaseDTO : planCaseByPlanId) {
                caseImageService.deleteCase(planCaseDTO.getCaseImageId());
            }
            return i;
        }

        // 修改后的测试计划关联用例与原有的测试计划关联的用例都不为空
        // 删除旧数据的空元素，主要避免脏数据造成空指针
        oldCaseImageEntities.removeIf(Objects::isNull);
        Map<Long, CaseImageEntity> oldMap = oldCaseImageEntities.stream().collect(Collectors.toMap(CaseImageEntity::getOriginalCaseId, Function.identity()));
        Map<Long, CaseDTO> newMap = newCaseDTOS.stream().collect(Collectors.toMap(CaseDTO::getId, Function.identity()));
        for (CaseDTO newCaseDTO : newCaseDTOS) {
            CaseImageEntity caseImageEntity = oldMap.get(newCaseDTO.getId());
            if (caseImageEntity == null) {
                CaseEntity caseEntity = BeanUtils.copyProperty(newCaseDTO, CaseEntity.class);
                CaseImageEntity addCaseImageEntity = caseImageService.addCase(caseEntity);
                planCaseService.addOnePlanCase(newTestPlanEntity, addCaseImageEntity);
            }
        }
        for (CaseImageEntity oldCaseImageEntity : oldCaseImageEntities) {
            if (!newMap.containsKey(oldCaseImageEntity.getOriginalCaseId())) {
                planCaseService.deleteByCaseImageId(oldCaseImageEntity.getId());
                caseImageService.deleteCase(oldCaseImageEntity.getId());

            }
        }
        return i;
    }

    @Override
    public PageResponse<TestPlanPage> testPlanList(PageDTO<TestPlanQuery> planQueryPageDTO) {
        // 分页查询
        Page<Object> page = PageHelper.startPage(planQueryPageDTO.getPageNum(), planQueryPageDTO.getPageSize());
        List<TestPlanEntity> testPlanEntities = testPlanMapper.selectTestPlanList(planQueryPageDTO.getParams());
        List<TestPlanPage> testPlanPages = BeanUtils.listObjectCopyProperty(testPlanEntities, TestPlanPage.class);
        List<TestPlanPage>  resList=new ArrayList<>();
        Map<Long, String> userMaps=userInfoMap();
        testPlanPages.forEach(item->{
            if( StringUtils.isBlank(item.getCreatorName())  ||   StringUtils.isBlank(item.getExecutorName()) ){
                item.setCreatorName(userMaps.get(item.getCreatorId()));
                item.setExecutorName(userMaps.get(item.getExecutorId()));
            }

            //未执行记录
            int noExectCount= planCaseCountMapper.selectCaseNoExectCount(item.getId()).get(0).getCount();
            if(item.getStartTime().compareTo(new Date())>0 && noExectCount>0){
                item.setStatus(StatucEnum.NOSTART.getName());
            }else if(item.getEndTime()!=null&&item.getEndTime().compareTo(new Date())>=0&&item.getStartTime().compareTo(new Date())<=0&&noExectCount>0){
                item.setStatus(StatucEnum.ING.getName());//进行中
            }else if(noExectCount>0){
                item.setStatus(StatucEnum.NOEND.getName());
            }else {
                System.out.println(item.getId());
                List<PlanCaseCountEntity> caseCount=planCaseCountMapper.selectPlanNoCase(item.getId());
                if(caseCount==null||caseCount.size()==0||caseCount.get(0)==null||caseCount.get(0).getCount()==0){
                    item.setStatus(StatucEnum.NOCASE.getName());
                }else {
                    List<PlanCaseCountEntity> l = planCaseCountMapper.selectCaseLastTime(item.getId());
                    if (l == null || l.size() == 0 || l.get(0) == null) {
                        List<PlanCaseCountEntity> selectCaseLastTimeNorecord = planCaseCountMapper.selectCaseLastTimeNorecord(item.getId());
                        if(selectCaseLastTimeNorecord==null||selectCaseLastTimeNorecord.size()==0||selectCaseLastTimeNorecord.get(0)==null)
                            item.setStatus(StatucEnum.ERROR.getName());
                        else{
                            Date lastTime = selectCaseLastTimeNorecord.get(0).getExecute_time();
                            item.setCompleteTime(lastTime);
                            if (item.getEndTime() != null && item.getEndTime().compareTo(lastTime) < 0) {
                                item.setStatus(StatucEnum.NOEND.getName());
                            } else {
                                item.setStatus(StatucEnum.END.getName());
                            }}
                    } else {
                        try {
                            Date lastTime = l.get(0).getExecute_time();
                            item.setCompleteTime(lastTime);
                            if (item.getEndTime() != null && item.getEndTime().compareTo(lastTime) < 0) {
                                item.setStatus(StatucEnum.NOEND.getName());
                            } else {
                                item.setStatus(StatucEnum.END.getName());
                            }
                        } catch (Exception e) {
                            item.setStatus(StatucEnum.ERROR.getName());
                            System.out.println("--------");
                        }
                    }
                }
            }
            resList.add(item);
        });
        PageInfo<TestPlanPage> testPlanPageInfo = new PageInfo<>(resList);
        testPlanPageInfo.setTotal(page.getTotal());
        return pageInfoToPageResponse(testPlanPageInfo);
    }

    @Override
    public PageInfoStatus<TestPlanPage> testPlanExecution(TimeDTO timeDTO) {
        if(StringUtils.isEmpty(String.valueOf(timeDTO.getId()))&&StringUtils.isEmpty(String.valueOf(timeDTO.getStartTime()))&&StringUtils.isEmpty(String.valueOf(timeDTO.getEndTime()))){
            return new PageInfoStatus<TestPlanPage>();
        }
        List<TestPlanEntity> testPlanEntities =timeDTO.getType()>0?testPlanMapper.selectTestPlanExecutionForType(timeDTO): testPlanMapper.selectTestPlanExecution(timeDTO);
        List<TestPlanPage> testPlanPages = BeanUtils.listObjectCopyProperty(testPlanEntities, TestPlanPage.class);
        List<TestPlanPage>  completeList=new ArrayList<>();
        List<TestPlanPage>  notStartList=new ArrayList<>();
        List<TestPlanPage>  ingList=new ArrayList<>();
        List<TestPlanPage>  noCompleteList=new ArrayList<>();
        List<TestPlanPage>  noCaseList=new ArrayList<>();
        Map<Long, String> userMaps=userInfoMap();
        if (testPlanPages!=null&&testPlanPages.size()>0) {

            testPlanPages.forEach(item -> {
                if (StringUtils.isBlank(item.getCreatorName()) || StringUtils.isBlank(item.getExecutorName())) {
                    item.setCreatorName(userMaps.get(item.getCreatorId()));
                    item.setExecutorName(userMaps.get(item.getExecutorId()));
                }
                //未执行记录
                int noExectCount = planCaseCountMapper.selectCaseNoExectCount(item.getId()).get(0).getCount();
                if (item.getStartTime().compareTo(new Date()) > 0 && noExectCount > 0) {
                    notStartList.add(item);
                    item.setStatus(StatucEnum.NOSTART.getName());
                } else if (item.getEndTime() != null && item.getEndTime().compareTo(new Date()) >= 0 && item.getStartTime().compareTo(new Date()) <= 0 && noExectCount > 0) {
                    item.setStatus(StatucEnum.ING.getName());//进行中
                    ingList.add(item);
                } else if (noExectCount > 0) {
                    item.setStatus(StatucEnum.NOEND.getName());
                    noCompleteList.add(item);
                } else {
                    List<PlanCaseCountEntity> caseCount = planCaseCountMapper.selectPlanNoCase(item.getId());
                    if (caseCount == null || caseCount.size() == 0 || caseCount.get(0) == null || caseCount.get(0).getCount() == 0) {
                        item.setStatus(StatucEnum.NOCASE.getName());
                        noCaseList.add(item);
                    } else {
                        List<PlanCaseCountEntity> l = planCaseCountMapper.selectCaseLastTime(item.getId());
                        if (l == null || l.size() == 0 || l.get(0) == null) {
                            List<PlanCaseCountEntity> selectCaseLastTimeNorecord = planCaseCountMapper.selectCaseLastTimeNorecord(item.getId());
                            if (selectCaseLastTimeNorecord == null || selectCaseLastTimeNorecord.size() == 0 || selectCaseLastTimeNorecord.get(0) == null)
                                item.setStatus(StatucEnum.ERROR.getName());
                            else {
                                Date lastTime = selectCaseLastTimeNorecord.get(0).getExecute_time();
                                item.setCompleteTime(lastTime);
                                if (item.getEndTime() != null && item.getEndTime().compareTo(lastTime) < 0) {
                                    item.setStatus(StatucEnum.NOEND.getName());
                                    noCompleteList.add(item);
                                } else {
                                    item.setStatus(StatucEnum.ING.getName());
                                    completeList.add(item);
                                }
                            }
                        } else {
                            try {
                                Date lastTime = l.get(0).getExecute_time();
                                item.setCompleteTime(lastTime);
                                if (item.getEndTime() != null && item.getEndTime().compareTo(lastTime) < 0) {
                                    item.setStatus(StatucEnum.NOEND.getName());
                                    noCompleteList.add(item);
                                } else {
                                    item.setStatus(StatucEnum.ING.getName());
                                    completeList.add(item);
                                }
                            } catch (Exception e) {
                                item.setStatus(StatucEnum.ERROR.getName());
                                System.out.println("--------");
                            }
                        }
                    }

                }
            });
        }
        PageInfoStatus<TestPlanPage> testPlanPageInfo = new PageInfoStatus<>();
        testPlanPageInfo.setCompleteCount(completeList.size());
        testPlanPageInfo.setCompleteList(completeList);
        testPlanPageInfo.setNoCompleteCount(noCompleteList.size());
        testPlanPageInfo.setNoCompleteList(noCompleteList);
        testPlanPageInfo.setIngCount(ingList.size());
        testPlanPageInfo.setIngList(ingList);
        testPlanPageInfo.setNotStartCount(notStartList.size());
        testPlanPageInfo.setNotStartList(notStartList);

        testPlanPageInfo.setNoCaseCount(noCaseList.size());
        testPlanPageInfo.setNoCaseList(noCaseList);
        testPlanPageInfo.setTotal(Long.valueOf(testPlanEntities.size()));
        return testPlanPageInfo;
//        return pageInfoTPageResponse(testPlanPageInfo);
    }

    /**
     * 获取所有的用户信息 转为map格式为：id --- nickName
     * @return
     */
    private   Map   userInfoMap(){
        Map<Long, String> userMaps=new HashMap<>();
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getIsDelete,0);
        List<UserEntity> userList=userMapper.selectList(queryWrapper);
       userMaps = userList.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getNickname));
        return  userMaps;
    }

    @Override
    public List<TestPlanEntity> findTestPlanByTimeRange(TestPlanStatisticsDTO testPlanStatisticsDTO) {
        List<TestPlanEntity> testPlanEntities = testPlanMapper.findTestPlanByTimeRange(testPlanStatisticsDTO);
        return testPlanEntities;
    }


}
