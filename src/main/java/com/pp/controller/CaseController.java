package com.pp.controller;

import com.pp.common.Result;
import com.pp.common.UserHolder;
import com.pp.domain.service.api.XmindService;
import com.pp.dto.CaseDTO;
import com.pp.dto.DeleteCase;
import com.pp.dto.XmindCaseDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.CasePageQuery;
import com.pp.dto.response.page.CasePage;
import com.pp.dto.response.page.PageResponse;
import com.pp.entity.UserEntity;
import com.pp.service.CaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/moka/case-platform/case")
@Api(tags = {"用例管理"})
public class CaseController {
    @Resource
    private CaseService caseService;
    @Resource
    private XmindService xmindService;


    @ApiOperation("增加用例")
    @PostMapping()
    public Result addCase(@RequestBody @Valid CaseDTO caseDTO) {
        UserEntity userEntity = UserHolder.get(); // 获取当前创建人
        caseDTO.setTesterId(userEntity.getId());
        Long aLong = caseService.addCase(caseDTO);
        return Result.ok().setContent(aLong);
    }

    @ApiOperation("批量添加用例")
    @PostMapping("/batch")
    public Result batchAddCase(@RequestBody @Valid List<CaseDTO> caseDTOS) {
        int i = caseService.batchAddCase(caseDTOS);
        return Result.ok().setContent(i);
    }


    @ApiOperation("查询用例详情")
    @GetMapping("/{id}")
    public Result getCase(@PathVariable Long id) {
        CaseDTO aCase = caseService.getCase(id);
        return Result.ok().setContent(aCase);
    }

    @ApiOperation("编辑用例")
    @PutMapping()
    public Result editCase(@RequestBody @Valid CaseDTO caseDTO) {
        int i = caseService.editCase(caseDTO);
        return Result.ok().setContent(i);
    }

    @ApiOperation("删除用例")
    @DeleteMapping("/{id}")
    public Result deleteCase(@PathVariable Long id) {
        int i = caseService.deleteCase(id);
        return Result.ok().setContent(i);
    }


    @ApiOperation("用例分页")
    @PostMapping("/list")
    public Result pageList(@RequestBody PageDTO<CasePageQuery> pageQueryPageDTO) {
        PageResponse<CasePage> casePagePageResponse = caseService.pageList(pageQueryPageDTO);
        return Result.ok().setContent(casePagePageResponse);
    }

    @ApiOperation("根据模块ID查询所属的case,包含子模块下的case")
    @GetMapping("/list/by-module/{moduleId}")
    public Result moduleCaseList(@PathVariable Long moduleId) {
        List<CasePage> caseByModuleId = caseService.getCaseByModuleId(moduleId);
        return Result.ok().setContent(caseByModuleId);
    }
    @ApiOperation("根据模块ID查询所属xmindCase")
    @GetMapping("/xmind-case/{moduleId}")
    public Result moduleCaseXmind(@PathVariable Long moduleId) {
        XmindCaseDTO xmindCase = xmindService.getXmindCase(moduleId);
        return Result.ok().setContent(xmindCase);
    }

    @ApiOperation("编辑xmindCase")
    @PostMapping("/xmind-case/edit")
    public Result editXmindCase(@RequestBody XmindCaseDTO xmindCaseDTO) {
        Boolean aBoolean = xmindService.editXmindCase(xmindCaseDTO);
        return Result.ok().setContent(aBoolean);
    }

    @ApiOperation("批量删除case")
    @PostMapping("/batchDelete")
    public Result batchDelete(@RequestBody DeleteCase caseIds) {

        int i = caseService.batchDeleteCase(caseIds);
        return Result.ok().setContent(i);
    }
}
