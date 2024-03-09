package com.pp.controller;

import com.pp.common.Result;
import com.pp.dto.request.LabelDTO;
import com.pp.entity.LabelEntity;
import com.pp.service.LabelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/moka/case-platform/label")
@Api(tags = {"标签"})
public class LabelController {
    @Resource
    private LabelService labelService;


    @ApiOperation("获取标签列表")
    @PostMapping("/list")
    public Result getLabelList(){
        List<LabelEntity> planCaseList = labelService.getList();
        return Result.ok().setContent(planCaseList);
    }

    @ApiOperation("更新case的标签")
    @PostMapping("/updateLabel")
    public Result addLabel(@RequestBody LabelDTO labelDTO){
        int a=labelService.updateLabel(labelDTO.getCaseId(),labelDTO.getLabelIds().toString());
        return Result.ok().setContent(a);
    }

}
