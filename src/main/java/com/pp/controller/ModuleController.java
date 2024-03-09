package com.pp.controller;


import com.pp.common.Result;
import com.pp.dto.ModuleDTO;
import com.pp.service.ModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = {"模块管理"})
@RequestMapping("/api/moka/case-platform/module")
public class ModuleController {

    @Resource
    private ModuleService moduleService;


    @PostMapping()
    @ApiOperation("新增模块")
    public Result addModule(@RequestBody ModuleDTO moduleDTO) {
        Long aLong = moduleService.addModule(moduleDTO);
        return Result.ok().setContent(aLong);
    }

    @PutMapping()
    @ApiOperation("编辑模块")
    public Result editModule(@RequestBody ModuleDTO moduleDTO) {
        int i = moduleService.editModule(moduleDTO);
        return Result.ok().setContent(i);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除模块")
    public Result deleteModule(@PathVariable Long id) {
        int i = moduleService.deleteModule(id);
        return Result.ok().setContent(i);
    }


    @GetMapping("/{id}")
    @ApiOperation("获取模块详情")
    public Result getModule(@PathVariable Long id) {
        ModuleDTO module = moduleService.getModule(id);
        return Result.ok().setContent(module);
    }


    @GetMapping("/next/{moduleId}")
    @ApiOperation("获取当前module下的子模块")
    public Result moduleNextModule(@PathVariable Long moduleId) {
        List<ModuleDTO> moduleDTOS = moduleService.selectNextModules(moduleId);
        return Result.ok().setContent(moduleDTOS);
    }

    @PostMapping("/parent/")
    @ApiOperation("查询所有父模块")
    public Result getParentModules(@RequestBody ModuleDTO moduleDTO) {
        List<ModuleDTO> moduleDTOS = moduleService.selectAllParentModules(moduleDTO);
        return Result.ok().setContent(moduleDTOS);
    }
}
