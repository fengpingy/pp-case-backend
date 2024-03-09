package com.pp.controller;

import com.pp.common.Result;
import com.pp.dto.ExportDTO;
import com.pp.service.ExportService;
import com.pp.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.xmind.core.CoreException;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/api/moka/case-platform/export")
@Api(tags = {"导出"})
public class ExportCaseController {
    @Resource
    private ExportService exportService;


    @ApiOperation("导出case")
    @PostMapping("/v1")
    public Result exportCase(@RequestBody ExportDTO dto) throws IOException, CoreException {
        if(dto.getType()==0){
            if((dto.getModuleId()==null||dto.getModuleId()==0)&&(dto.getCaseIds()==null||dto.getCaseIds().size()==0)){
                return Result.fail().setContent("模块ID或者caseID没传");
            }
        }else if(dto.getType()>1){
            return Result.fail().setContent("类型错误");
        }
        String url = exportService.getCase(dto);
        byte[] data=FileUtils.fileUrlToBytes(url);
        if(data==null||data.length==0){
            return Result.fail().setContent("不能选择根节点或者默认模块");
        }
        return Result.ok().setContent(data).putContent("url",url);
    }

}
