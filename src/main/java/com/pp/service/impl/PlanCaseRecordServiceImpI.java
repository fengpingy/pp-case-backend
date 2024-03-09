package com.pp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pp.common.UserHolder;
import com.pp.common.enums.system.RoleType;
import com.pp.dao.PlanCaseRecordMapper;
import com.pp.dao.UserMapper;
import com.pp.dto.PlanCaseDTO;
import com.pp.dto.PlanCaseRecordsDTO;
import com.pp.dto.request.TestPlanStatisticsDTO;
import com.pp.dto.response.statement.*;
import com.pp.entity.*;
import com.pp.service.CaseImageService;
import com.pp.service.PlanCaseRecordService;
import com.pp.service.PlanCaseService;
import com.pp.utils.DistinctUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.pp.utils.BeanUtils.listObjectCopyProperty;


@Service
@Slf4j
public class PlanCaseRecordServiceImpI implements PlanCaseRecordService {

    @Resource
    private PlanCaseRecordMapper planCaseRecordMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PlanCaseRecordService planCaseRecordService;

    @Resource
    private TestPlanServiceImpl testPlanService ;

    @Resource
    private PlanCaseService planCaseService;

    @Resource
    private CaseImageService caseImageService;

    /**
     * 记录列表模式下cases执行的操作记录
     * @param planCaseRecordsDTOS
     * @return
     */
    @Override
    public int batchAddPlanCaseRecord(List<PlanCaseRecordsDTO> planCaseRecordsDTOS) {
        List<PlanCaseRecordsEntity> planCaseRecordsEntities = listObjectCopyProperty(planCaseRecordsDTOS, PlanCaseRecordsEntity.class);
        String userName = UserHolder.get().getUsername();
        UserEntity userEntity = userMapper.getUserByName(userName);

        if (CollectionUtils.isEmpty(planCaseRecordsEntities)) {
            return 0;
        }
        for (PlanCaseRecordsEntity planCaseRecord : planCaseRecordsEntities) {
            planCaseRecord.setId(IdWorker.getId());
            planCaseRecord.setExecuteName(userName);
            planCaseRecord.setExecuteRole(userEntity.getRoleType());
        }

        return planCaseRecordMapper.batchInsertPlanCaseRecord(planCaseRecordsEntities);
    }

    /**
     * 根据测试计划Id 查询cases执行记录
     * @param planCaseDTO
     * @return
     */
    @Override
    public List<PlanCaseRecordsDTO> searchPlanCaseRecordsByPlanId(PlanCaseDTO planCaseDTO) {
        long planId = planCaseDTO.getPlanId();
        List<PlanCaseRecordsEntity> planCaseRecordsEntityList = planCaseRecordMapper.searchPlanCaseRecordsByPlanId(planId);
        List<PlanCaseRecordsDTO> planCaseRecordsDTOList = listObjectCopyProperty(planCaseRecordsEntityList, PlanCaseRecordsDTO.class);

        return planCaseRecordsDTOList;
    }

    /**
     * 统计研发，测试cases执行记录的差异统计
     * @param planCaseDTO
     * @return
     */
    @Override
    public ContrastStatisticsDTO devTestContrastStatistics(PlanCaseDTO planCaseDTO) {
        try {
            long planId = planCaseDTO.getPlanId();
            AtomicLong equalityPassAmount = new AtomicLong(0);
            AtomicLong unequalPassAmount = new AtomicLong(0);
            //查询测试计划下cases，过滤掉已删除的cases
            List<PlanCaseEntity> planCaseEntityList = planCaseService.filterIsDeletePlanCase(planId);

            List<Long> caseImageIds = new ArrayList<>();
            planCaseEntityList.forEach(e -> caseImageIds.add(e.getCaseImageId()));
            long casesAmount = planCaseEntityList.size();


            List<DevTestContrastDTO> devTestContrasts = new ArrayList<>();
            ContrastStatisticsDTO.ContrastStatisticsDTOBuilder statisticsDTOBuilder = ContrastStatisticsDTO.builder();
            statisticsDTOBuilder.casesAmount(casesAmount);

            List<PlanCaseRecordsEntity> planCaseRecordsEntityList = planCaseRecordMapper.searchPlanCaseRecordsByPlanId(planId);


            if (planCaseRecordsEntityList.isEmpty()){
                return statisticsDTOBuilder.devExecuteAmount(0).testExecuteAmount(0).unequalPassAmount(0).equalityPassAmount(0).build();
            }

            TestExecuteStatisticsDTO.TestExecuteStatisticsDTOBuilder testExecuteStatisticsDTO = TestExecuteStatisticsDTO.builder();
            List<Long> caseFailList = new ArrayList<>();
            //研发执行的cases记录
            List<PlanCaseRecordsEntity> devPlanCaseRecords = planCaseRecordsEntityList.stream().
                    filter(e -> e.getExecuteRole().equals(RoleType.DEVELOP)).
                    filter(DistinctUtils.distinctByVariable(PlanCaseEntity::getCaseImageId)).filter(e -> caseImageIds.contains(e.getCaseImageId())).collect(Collectors.toList());
            log.info("devPlanCaseRecords数据："+devPlanCaseRecords);

            //测试执行的cases记录
            List<PlanCaseRecordsEntity> testPlanCaseRecords = planCaseRecordsEntityList.stream().
                    filter(e -> e.getExecuteRole().equals(RoleType.TEST)).
                    filter(DistinctUtils.distinctByVariable(PlanCaseEntity::getCaseImageId)).filter(e -> caseImageIds.contains(e.getCaseImageId())).collect(Collectors.toList());
            int testExecuteFailAmount = Math.toIntExact(testPlanCaseRecords.stream().filter(e -> e.getIsExecute() != null && e.getIsExecute() != 0 && e.getIsPass() != null && e.getIsPass().equals(1)).count());
            int testExecutePassAmount = testPlanCaseRecords.size()-testExecuteFailAmount;
            caseFailList = testPlanCaseRecords.stream().filter(e ->e.getIsPass().equals(1)).map(PlanCaseRecordsEntity::getCaseImageId).collect(Collectors.toList());

            testExecuteStatisticsDTO.testExecuteFailAmount(testExecuteFailAmount).testExecutePassAmount(testExecutePassAmount).testFailCaseImageId(caseFailList);
            statisticsDTOBuilder.testExecuteStatistics(testExecuteStatisticsDTO.build());

            //统计执行的总数
            statisticsDTOBuilder.devExecuteAmount(devPlanCaseRecords.size()).testExecuteAmount(testPlanCaseRecords.size());
            //统计相同case 研发和测试执行的差异数据
            if (CollectionUtils.isNotEmpty(devPlanCaseRecords)) {
                List<Long> caseIds = devPlanCaseRecords.stream().map(PlanCaseRecordsEntity::getCaseImageId).collect(Collectors.toList());
                List<CaseImageEntity> caseImageEntities = caseImageService.getCaseList(caseIds);
                devPlanCaseRecords.forEach(e -> {
                    DevTestContrastDTO.DevTestContrastDTOBuilder dtoBuilder = DevTestContrastDTO.builder();
                    Optional<PlanCaseRecordsEntity> newTestRecords = testPlanCaseRecords.stream().filter(cases -> cases.getCaseImageId().equals(e.getCaseImageId())).findFirst();
                    dtoBuilder.caseImageId(e.getCaseImageId()).devIsPass(e.getIsPass());
                    newTestRecords.ifPresent(planCaseRecordsEntity -> dtoBuilder.testIsPass(planCaseRecordsEntity.getIsPass()));
                    newTestRecords.ifPresent(planCaseRecordsEntity -> dtoBuilder.equalityPass(e.getIsPass().equals(planCaseRecordsEntity.getIsPass())));
                    devTestContrasts.add(dtoBuilder.build());
                });

                devTestContrasts.forEach(e -> {
                    e.setCaseImageEntity(caseImageEntities.stream().filter(t -> t.getId().equals(e.getCaseImageId())).findFirst().get());
                    if (e.isEqualityPass()){
                        equalityPassAmount.getAndIncrement();
                    }else if (!e.isEqualityPass() && e.getTestIsPass() != null){
                        unequalPassAmount.getAndIncrement();
                    }
                });
                statisticsDTOBuilder.devTestContrasts(devTestContrasts).
                        equalityPassAmount(equalityPassAmount.get()).unequalPassAmount(unequalPassAmount.get());



            }

            return statisticsDTOBuilder.build();

        } catch (Exception e) {
            e.printStackTrace();
            return ContrastStatisticsDTO.builder().build();
        }
    }

//    @Override
//    public ContrastStatisticsDTO devTestContrastStatistics(PlanCaseDTO planCaseDTO) {
//        try {
//            long planId = planCaseDTO.getPlanId();
//            List<PlanCaseEntity> planCaseEntityList = planCaseService.filterIsDeletePlanCase(planId);
//            List<Long> caseImageIds = planCaseEntityList.stream()
//                    .map(PlanCaseEntity::getCaseImageId)
//                    .collect(Collectors.toList());
//
//            long casesAmount = planCaseEntityList.size();
//            List<PlanCaseRecordsEntity> planCaseRecordsEntityList = planCaseRecordMapper.searchPlanCaseRecordsByPlanId(planId);
//
//            if (planCaseRecordsEntityList.isEmpty()) {
//                return ContrastStatisticsDTO.builder()
//                        .casesAmount(casesAmount)
//                        .devExecuteAmount(0)
//                        .testExecuteAmount(0)
//                        .unequalPassAmount(0)
//                        .equalityPassAmount(0)
//                        .build();
//            }
//
//            List<PlanCaseRecordsEntity> devPlanCaseRecords = planCaseRecordsEntityList.stream()
//                    .filter(e -> e.getExecuteRole().equals(RoleType.DEVELOP))
//                    .filter(DistinctUtils.distinctByVariable(PlanCaseEntity::getCaseImageId))
//                    .filter(e -> caseImageIds.contains(e.getCaseImageId()))
//                    .collect(Collectors.toList());
//
//            List<PlanCaseRecordsEntity> testPlanCaseRecords = planCaseRecordsEntityList.stream()
//                    .filter(e -> e.getExecuteRole().equals(RoleType.TEST))
//                    .filter(DistinctUtils.distinctByVariable(PlanCaseEntity::getCaseImageId))
//                    .filter(e -> caseImageIds.contains(e.getCaseImageId()))
//                    .collect(Collectors.toList());
//
//            int devExecuteAmount = devPlanCaseRecords.size();
//            int testExecuteAmount = testPlanCaseRecords.size();
//
//            long equalityPassAmount = devPlanCaseRecords.stream()
//                    .filter(devRecord -> {
//                        Optional<PlanCaseRecordsEntity> testRecord = testPlanCaseRecords.stream()
//                                .filter(testCase -> testCase.getCaseImageId().equals(devRecord.getCaseImageId()))
//                                .findFirst();
//                        return testRecord.isPresent() && devRecord.getIsPass().equals(testRecord.get().getIsPass());
//                    })
//                    .count();
//
//            long unequalPassAmount = devPlanCaseRecords.stream()
//                    .filter(devRecord -> {
//                        Optional<PlanCaseRecordsEntity> testRecord = testPlanCaseRecords.stream()
//                                .filter(testCase -> testCase.getCaseImageId().equals(devRecord.getCaseImageId()))
//                                .findFirst();
//                        return testRecord.isPresent() && !devRecord.getIsPass().equals(testRecord.get().getIsPass());
//                    })
//                    .count();
//
//            return ContrastStatisticsDTO.builder()
//                    .casesAmount(casesAmount)
//                    .devExecuteAmount(devExecuteAmount)
//                    .testExecuteAmount(testExecuteAmount)
//                    .equalityPassAmount(equalityPassAmount)
//                    .unequalPassAmount(unequalPassAmount)
//                    .build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ContrastStatisticsDTO.builder().build();
//        }
//    }


    @Override
    public PlanCaseStatisticsDTO planCaseStatistics(PlanCaseDTO planCaseDTO) {
        long planId = planCaseDTO.getPlanId();
        PlanCaseStatisticsDTO.PlanCaseStatisticsDTOBuilder planCaseStatisticsDTOBuilder = PlanCaseStatisticsDTO.builder();
        List<PlanCaseEntity> planCaseEntityList = planCaseService.filterIsDeletePlanCase(planId);
        long casesAmount = planCaseEntityList.size();
        //cases通过的总数
        long casesPassAmount = planCaseEntityList.stream().filter(e -> e.getIsExecute() !=null && e.getIsExecute() !=0 && e.getIsPass() != null && e.getIsPass().equals(0)).count();
        //cases失败的总数
        long casesFailAmount =  planCaseEntityList.stream().filter(e -> e.getIsExecute() !=null && e.getIsExecute() !=0 && e.getIsPass() != null && e.getIsPass().equals(1)).count();
        // cases执行人分布统计
        Map<String, Long> mapList = planCaseEntityList.stream().filter(e -> e.getIsExecute() !=null && e.getIsExecute() !=0 && e.getIsExecute().equals(1) ).collect(Collectors.groupingBy(PlanCaseEntity::getExecuteName,Collectors.counting()));

        planCaseStatisticsDTOBuilder.casesAmount(casesAmount).casesPassAmount(casesPassAmount).casesFailAmount(casesFailAmount).executeName(mapList);

        return planCaseStatisticsDTOBuilder.build();
    }

    @Override
    public TestPlanStatisticsResponseDTO itemPlanCaseStatistics(Long planId, TestPlanStatisticsResponseDTO testPlanStatisticsResponseDTO) {
        PlanCaseDTO planCaseDTO = new PlanCaseDTO();
        planCaseDTO.setPlanId(planId);

        PlanCaseStatisticsDTO planCaseStatisticsDTO = planCaseRecordService.planCaseStatistics(planCaseDTO);

        ContrastStatisticsDTO contrastStatisticsDTO = planCaseRecordService.devTestContrastStatistics(planCaseDTO);

        long casesAmount = testPlanStatisticsResponseDTO.getCasesAmount()+planCaseStatisticsDTO.getCasesAmount();
        long casesPassAmount = testPlanStatisticsResponseDTO.getCasesPassAmount()+planCaseStatisticsDTO.getCasesPassAmount();
        long casesFailAmount = testPlanStatisticsResponseDTO.getCasesFailAmount()+planCaseStatisticsDTO.getCasesFailAmount();
        long devExecuteAmount = testPlanStatisticsResponseDTO.getDevExecuteAmount()+contrastStatisticsDTO.getDevExecuteAmount();
        long testExecuteAmount = testPlanStatisticsResponseDTO.getTestExecuteAmount()+contrastStatisticsDTO.getTestExecuteAmount();
        long equalityPassAmount = testPlanStatisticsResponseDTO.getEqualityPassAmount()+contrastStatisticsDTO.getEqualityPassAmount();
        long unequalPassAmount = testPlanStatisticsResponseDTO.getUnequalPassAmount()+contrastStatisticsDTO.getUnequalPassAmount();


        testPlanStatisticsResponseDTO.setCasesAmount(casesAmount);
        testPlanStatisticsResponseDTO.setCasesPassAmount(casesPassAmount);
        testPlanStatisticsResponseDTO.setCasesFailAmount(casesFailAmount);
        testPlanStatisticsResponseDTO.setDevExecuteAmount(devExecuteAmount);
        testPlanStatisticsResponseDTO.setTestExecuteAmount(testExecuteAmount);
        testPlanStatisticsResponseDTO.setEqualityPassAmount(equalityPassAmount);
        testPlanStatisticsResponseDTO.setUnequalPassAmount(unequalPassAmount);
        if (Objects.nonNull(contrastStatisticsDTO.getTestExecuteStatistics())){
            int TestExecutePassAmount = testPlanStatisticsResponseDTO.getTestExecutePassAmount()+contrastStatisticsDTO.getTestExecuteStatistics().getTestExecutePassAmount();
            int TestExecuteFailAmount = testPlanStatisticsResponseDTO.getTestExecuteFailAmount()+contrastStatisticsDTO.getTestExecuteStatistics().getTestExecuteFailAmount();
            testPlanStatisticsResponseDTO.setTestExecutePassAmount(TestExecutePassAmount);
            testPlanStatisticsResponseDTO.setTestExecuteFailAmount(TestExecuteFailAmount);
        }


        return testPlanStatisticsResponseDTO;
    }

//    @Override
//    public TestPlanStatisticsResponseDTO itemPlanCaseStatistics(Long planId, TestPlanStatisticsResponseDTO testPlanStatisticsResponseDTO) {
//        PlanCaseDTO planCaseDTO = new PlanCaseDTO();
//        planCaseDTO.setPlanId(planId);
//
//        PlanCaseStatisticsDTO planCaseStatisticsDTO = planCaseRecordService.planCaseStatistics(planCaseDTO);
//        ContrastStatisticsDTO contrastStatisticsDTO = planCaseRecordService.devTestContrastStatistics(planCaseDTO);
//
//        testPlanStatisticsResponseDTO.setCasesAmount(testPlanStatisticsResponseDTO.getCasesAmount() + planCaseStatisticsDTO.getCasesAmount());
//        testPlanStatisticsResponseDTO.setCasesPassAmount(testPlanStatisticsResponseDTO.getCasesPassAmount() + planCaseStatisticsDTO.getCasesPassAmount());
//        testPlanStatisticsResponseDTO.setCasesFailAmount(testPlanStatisticsResponseDTO.getCasesFailAmount() + planCaseStatisticsDTO.getCasesFailAmount());
//        testPlanStatisticsResponseDTO.setDevExecuteAmount(testPlanStatisticsResponseDTO.getDevExecuteAmount() + contrastStatisticsDTO.getDevExecuteAmount());
//        testPlanStatisticsResponseDTO.setTestExecuteAmount(testPlanStatisticsResponseDTO.getTestExecuteAmount() + contrastStatisticsDTO.getTestExecuteAmount());
//        testPlanStatisticsResponseDTO.setEqualityPassAmount(testPlanStatisticsResponseDTO.getEqualityPassAmount() + contrastStatisticsDTO.getEqualityPassAmount());
//        testPlanStatisticsResponseDTO.setUnequalPassAmount(testPlanStatisticsResponseDTO.getUnequalPassAmount() + contrastStatisticsDTO.getUnequalPassAmount());
//
//        return testPlanStatisticsResponseDTO;
//    }


    @Override
    public TestPlanStatisticsResponseDTO planCaseStatisticsByTimeRange(TestPlanStatisticsDTO testPlanStatisticsDTO) {
        TestPlanStatisticsResponseDTO testPlanStatisticsResponseDTO = new TestPlanStatisticsResponseDTO();

        List<TestPlanEntity> planIds = testPlanService.findTestPlanByTimeRange(testPlanStatisticsDTO);
        List<TestPlanResponseDTO> testPlanResponseDTOS = new ArrayList<>();

        if (planIds.isEmpty()){
            return testPlanStatisticsResponseDTO;
        }
        planIds.forEach(bo -> {
            UserEntity creator = userMapper.selectById(bo.getCreatorId());
            UserEntity executor = userMapper.selectById(bo.getExecutorId());

            TestPlanResponseDTO.TestPlanResponseDTOBuilder testPlanResponseDTOBuilder = TestPlanResponseDTO.builder();
            testPlanResponseDTOBuilder.id(bo.getId()).name(bo.getName()).createTime(bo.getCreateTime()).
                    startTime(bo.getStartTime()).endTime(bo.getEndTime()).productModule(bo.getProductModule());
            if (Objects.nonNull(creator)){
                testPlanResponseDTOBuilder.creatorName(creator.getNickname());
            }
            if (Objects.nonNull(executor)){
                testPlanResponseDTOBuilder.executorName(executor.getNickname());

            }
            testPlanResponseDTOS.add(testPlanResponseDTOBuilder.build());

            planCaseRecordService.itemPlanCaseStatistics(bo.getId(), testPlanStatisticsResponseDTO);

        });

//        planIds.forEach(bo -> planCaseRecordService.itemPlanCaseStatistics(bo.getId(), testPlanStatisticsResponseDTO));

        testPlanStatisticsResponseDTO.setTestPlanLists(testPlanResponseDTOS);
        return testPlanStatisticsResponseDTO;
    }
}
