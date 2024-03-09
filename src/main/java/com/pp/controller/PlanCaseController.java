package com.pp.controller;

import com.pp.common.Result;
import com.pp.dto.*;
import com.pp.dto.request.TestPlanStatisticsDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.PlanCaseQuery;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.PlanCasePage;
import com.pp.dto.response.statement.ContrastStatisticsDTO;
import com.pp.dto.response.statement.PlanCaseStatisticsDTO;
import com.pp.dto.response.statement.TestPlanStatisticsResponseDTO;
import com.pp.service.ModuleService;
import com.pp.service.PlanCaseRecordService;
import com.pp.service.PlanCaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/moka/case-platform/plan-case")
@Api(tags = {"测试计划关联用例"})
public class PlanCaseController {
    @Resource
    private PlanCaseService planCaseService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private PlanCaseRecordService planCaseRecordService;

    @ApiOperation("测试计划下用例分页")
    @PostMapping("/list")
    public Result getPlanCaseList(@RequestBody PageDTO<PlanCaseQuery> planCaseQueryPageDTO){
        PageResponse<PlanCasePage> planCaseList = planCaseService.getPlanCaseList(planCaseQueryPageDTO);
        return Result.ok().setContent(planCaseList);
    }
    @ApiOperation("执行测试计划下的用例")
    @PutMapping()
    public Result executePlanCase(@RequestBody PlanCaseDTO planCaseDTO) {
        int i = planCaseService.editPlanCase(planCaseDTO);
        return Result.ok().setContent(i);
    }

    @ApiOperation("获取测试计划下的xmind用例")
    @GetMapping("/xmind/{planId}")
    public Result getPlanXmindCase(@PathVariable Long planId) {
        TestPlanXmindCaseDTO testPlanXmindCaseDTO = planCaseService.getPlanXmindCaseByPlanId(planId);
        return Result.ok().setContent(testPlanXmindCaseDTO);
    }
    @ApiOperation("编辑测试计划下的xmind用例")
    @PostMapping("/xmind/edit")
    public Result editPlanXmindCase(@RequestBody TestPlanXmindCaseDTO testPlanXmindCaseDTO) {
        Boolean aBoolean = planCaseService.editPlanXmindCase(testPlanXmindCaseDTO);
        return Result.ok().setContent(aBoolean);
    }

    @ApiOperation("测试接口找到最小公共父节点")
    @GetMapping("/LCA/{planId}")
    public Result getLCA(@PathVariable Long planId) {
        Long lca = planCaseService.findLCA(planId);
        ModuleDTO module = moduleService.getModule(lca);
        return Result.ok().setContent(module);
    }

    @ApiOperation("测试添加执行记录")
    @PostMapping("/test/addRecord")
    public Result addPlanRecords(@RequestBody List<PlanCaseRecordsDTO> planCaseRecordsDTOS) {
        int i = planCaseRecordService.batchAddPlanCaseRecord(planCaseRecordsDTOS);
        return Result.ok().setContent(i);
    }

    @ApiOperation("通过planId获取执行记录")
    @PostMapping("/test/getRecord")
    public Result getPlanRecords(@RequestBody PlanCaseDTO planCaseDTO) {
        List<PlanCaseRecordsDTO> planCaseRecordsEntityList = planCaseRecordService.searchPlanCaseRecordsByPlanId(planCaseDTO);
        return Result.ok().setContent(planCaseRecordsEntityList);
    }

    @ApiOperation("获取测试计划用例研发&测试执行记录对比")
    @PostMapping("/devTestContrast")
    public Result devTestContrastStatistics(@RequestBody PlanCaseDTO planCaseDTO) {
        ContrastStatisticsDTO contrastStatistics = planCaseRecordService.devTestContrastStatistics(planCaseDTO);
        return Result.ok().setContent(contrastStatistics);
    }

    @ApiOperation("获取测试计划用例执行数据统计")
    @PostMapping("/planCaseStatistics")
    public Result planCaseStatistics(@RequestBody PlanCaseDTO planCaseDTO) {
        PlanCaseStatisticsDTO contrastStatistics = planCaseRecordService.planCaseStatistics(planCaseDTO);
        return Result.ok().setContent(contrastStatistics);
    }
    @ApiOperation("获取测试计划用例执行数据统计通过时间范围")
    @PostMapping("/testPlanStatistics")
    public Result testPlanStatistics(@RequestBody TestPlanStatisticsDTO testPlanStatisticsDTO) {
        TestPlanStatisticsResponseDTO testPlanStatisticsResponseDTO = planCaseRecordService.planCaseStatisticsByTimeRange(testPlanStatisticsDTO);
        return Result.ok().setContent(testPlanStatisticsResponseDTO);
    }


    @ApiOperation("给case分配经办人")
    @PostMapping("/distributeOwner")
    public Result distributeOwner(@RequestBody OwnerDTO ownerDTO) {
        if(ownerDTO.getCaseIds()==null||ownerDTO.getCaseIds().size()==0){
            return Result.fail();
        }
        if(ownerDTO.getOwnerId()==null){
            return Result.fail();
        }
        int i = planCaseService.distributeOwner(ownerDTO);
        return Result.ok().setContent(i);
    }
}
