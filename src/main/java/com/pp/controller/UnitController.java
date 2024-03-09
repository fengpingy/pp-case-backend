package com.pp.controller;


import com.pp.common.Result;
import com.pp.dto.UnitDTO;
import com.pp.dto.response.ModuleTreeDTO;
import com.pp.service.UnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/moka/case-platform/unit")
@Api(tags = {"组织管理"})
public class UnitController {

    @Resource
    private UnitService unitService;

    @PostMapping()
    @ApiOperation("增加组织")
    public Result addUnit(@RequestBody
                              @Valid UnitDTO unitDTO){
        Long aLong = unitService.addUnit(unitDTO);
        return Result.ok().setContent(aLong);
    }

    @PostMapping("/tree/{unitId}")
    @ApiOperation("获取当前组织下的module树")
    public Result unitTree(@PathVariable Long unitId){
        ModuleTreeDTO moduleTreeDTO = unitService.selectModuleTree(unitId);
        return Result.ok().setContent(moduleTreeDTO);
    }

}
