package com.pp.service;

import com.pp.dto.*;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.PlanCaseQuery;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.PlanCasePage;
import com.pp.entity.CaseImageEntity;
import com.pp.entity.PlanCaseEntity;
import com.pp.entity.TestPlanEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface PlanCaseService {
    /**
     * 获取测试计划中的case
     * @param id
     * @return
     */
    List<PlanCaseDTO> getPlanCaseByPlanId(Long id);

    /**
     * 测试计划下用例分页
     * @param planCaseQueryPageDTO
     * @return
     */
    PageResponse<PlanCasePage> getPlanCaseList(PageDTO<PlanCaseQuery> planCaseQueryPageDTO);

    /**
     * 删除测试计划下关联的用例
     * @param id
     * @return
     */
    int deletePlanCaseByPlanId(Long id);

    /**
     * 根据镜像caseID删除关联表的镜像case
     * @param id
     * @return
     */
    int deleteByCaseImageId(Long id);

    /**
     * 关联表批量插入数据
     * @param testPlanEntity
     * @param caseImageEntities
     * @return
     */
    List<PlanCaseEntity> addPlanCase(TestPlanEntity testPlanEntity, List<CaseImageEntity> caseImageEntities);

    /**
     * 关联表插入数据
     * @param testPlanEntity
     * @param caseImageEntity
     * @return
     */
    PlanCaseEntity addOnePlanCase(TestPlanEntity testPlanEntity, CaseImageEntity caseImageEntity);

    /**
     * 更新关联表数据
     * @param planCaseDTO
     * @return
     */
    int editPlanCase(PlanCaseDTO planCaseDTO);


    /**
     * 批量更新
     * @param planCaseDTOS
     * @return
     */
    int batchUpdatePlanCaseByCaseImageIds(List<PlanCaseDTO> planCaseDTOS);


    /**
     * 根据测试计划ID查询所属的case，返回xmind结构数据
     * @param planId
     * @return
     */
    TestPlanXmindCaseDTO getPlanXmindCaseByPlanId(Long planId);

    /**
     * 测试接口
     * @param planId
     * @return
     */
    Long findLCA(Long planId);

    /**
     * 更新测试计划中的xmindCase
     * @param testPlanXmindCaseDTO
     * @return
     */
    Boolean editPlanXmindCase(TestPlanXmindCaseDTO testPlanXmindCaseDTO);


    /**
     * 过滤测试计划中已删除的case
     * @param planId 测试计划Id
     * @return
     */
    List<PlanCaseEntity> filterIsDeletePlanCase(Long planId);


    /**
     * 分配经办人
     * @param ownerDTO
     * @return
     */
    int distributeOwner(@RequestBody OwnerDTO ownerDTO);

    /**
     * 组装用例树
     * @param caseImageDTOS
     * @return List<TestPlanXmindCaseDTO>
     */
    Map<Long, List<TestPlanXmindCaseDTO>> spliceCaseTree(Long planId, List<CaseImageDTO> caseImageDTOS);

}
