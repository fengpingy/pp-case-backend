package com.pp.domain.controller;

import com.pp.common.Result;
import com.pp.common.annotation.IgnoreAuth;
import com.pp.common.enums.business.CaseLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.pp.utils.EnumUtils.enumToObj;

@RestController
@RequestMapping("/api/moka/case-platform/other")
@Api(tags = {"其他"})
public class OtherController {


    @GetMapping("case-level")
    @IgnoreAuth
    @ApiOperation("获取用例等级")
    public Result caseLevel(){
        return Result.ok().setContent(enumToObj(CaseLevel.class));
    }
}
