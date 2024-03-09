package com.pp.controller;


import com.pp.common.Result;
import com.pp.common.annotation.Event;
import com.pp.dto.PageInfoStatus;
import com.pp.dto.TestPlanDTO;
import com.pp.dto.request.TimeDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.TestPlanQuery;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.TestPlanPage;
import com.pp.service.TestPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/moka/case-platform/test-plan")
@Api(tags = {"测试计划管理"})
public class TestPlanController {
    @Resource
    private TestPlanService testPlanService;
    @ApiOperation("添加测试计划")
    @PostMapping()
    public Result addTestPlan(@RequestBody @Valid TestPlanDTO testPlanDTO){
        Long along = testPlanService.addTestPlan(testPlanDTO.setId(null));
        return Result.ok().setContent(along);

    }
    @ApiOperation("获取测试计划")
    @GetMapping("/{id}")
    public Result getTestPlan(@PathVariable Long id){
        TestPlanDTO testPlanDTO = testPlanService.getTestPlan(id);
        return Result.ok().setContent(testPlanDTO);
    }
    @ApiOperation("修改测试计划")
    @Event()
    @PutMapping()
    public Result editTestPlan(@RequestBody @Valid TestPlanDTO testPlanDTO){
        int aLong = testPlanService.editTestPlan(testPlanDTO);
        return Result.ok().setContent(aLong);
    }
    @ApiOperation("删除测试计划")
    @Event()
    @DeleteMapping("/{id}")
    public Result delTestPlan(@PathVariable Long id){
        int i =  testPlanService.delTestPlan(id);
        return Result.ok().setContent(i);
    }

    @ApiOperation("测试计划列表")
    @PostMapping("/list")
    public Result testPlanList(@RequestBody PageDTO<TestPlanQuery> testPlanQueryPageDTO) {
        PageResponse<TestPlanPage> testPlanPageResponse = testPlanService.testPlanList(testPlanQueryPageDTO);
        return Result.ok().setContent(testPlanPageResponse);

    }




    @ApiOperation("获取测试计划的延期情况")
    @PostMapping("/ExecutionEfficiencyList")
    public Result testPlanExecutionEfficiency(@RequestBody TimeDTO timeDTO) {
        PageInfoStatus<TestPlanPage> testPlanPageResponse = testPlanService.testPlanExecution(timeDTO);
        return Result.ok().setContent(testPlanPageResponse);

    }
}
