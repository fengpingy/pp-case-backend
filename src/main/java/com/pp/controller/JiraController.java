package com.pp.controller;

import com.pp.common.Result;
import com.pp.dto.jiraData.BaseJiraDTO;
import com.pp.dto.jiraData.SearchJiraDTO;
import com.pp.service.JiraService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/moka/case-platform/jira")
@Api(tags = {"jira 管理"})
public class JiraController {

    @Resource
    private JiraService jiraService;

    @ApiOperation("查询jira详情")
    @GetMapping("/{key}")
    public Result getCase(@PathVariable String key) {
        BaseJiraDTO baseJiraDTO = jiraService.getIssueInfo(key);
        return Result.ok().setContent(baseJiraDTO);
    }

    @ApiOperation("通过标签批量查询bug")
    @PostMapping("/searchIssues")
    public Result searchIssues(@RequestBody @Valid SearchJiraDTO searchKey) {
        List<BaseJiraDTO> baseJiraDTOS = jiraService.searchIssues(searchKey);
        return Result.ok().setContent(baseJiraDTOS);
    }


}
