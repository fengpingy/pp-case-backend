package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.entity.PlanCaseCountEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanCaseCountMapper extends BaseMapper<PlanCaseCountEntity> {

    List<PlanCaseCountEntity> selectPlanNoCase(Long id);
    List<PlanCaseCountEntity> selectCaseNoExectCount(Long id);
    List<PlanCaseCountEntity> selectCaseLastTime(Long id);
    //兼容历史计划，在moka_plan_case_records表中无执行记录的情况，取moka_plan_case的执行时间
    List<PlanCaseCountEntity> selectCaseLastTimeNorecord(Long id);
}
