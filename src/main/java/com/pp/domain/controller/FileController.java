package com.pp.domain.controller;


import com.pp.common.Result;
import com.pp.domain.service.api.CsvService;
import com.pp.domain.service.api.XmindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/moka/case-platform/file")
@Api(tags = {"文件相关"})
public class FileController {

    @Resource
    private XmindService xmindService;
    @Resource
    private CsvService csvService;

    @ApiOperation("解析xmind")
    @PostMapping("/xmind/upload")
    public Result xmindParse(@RequestParam("file") MultipartFile file,
                             @RequestParam Long unitId, @RequestParam(value = "cover", required = false, defaultValue = "false") boolean cover) {
        Boolean aBoolean = xmindService.parseCaseAndModule(file, unitId, cover);
        return Result.ok().setSuccess(aBoolean);
    }

    @ApiOperation("解析禅道csv")
    @PostMapping("/csv/upload")
    public Result csvParse(@RequestParam("file") MultipartFile file,
                           @RequestParam Long unitId) {
        Boolean aBoolean = csvService.parseCaseAndModule(file, unitId);
        return Result.ok().setSuccess(aBoolean);
    }

    @ApiOperation("下载xmind用例模板")
    @GetMapping("/download/{name}")
    public Result getXmindTemplate(@PathVariable String name, HttpServletResponse response) throws Exception {
        Boolean xmindTemplate = xmindService.getXmindTemplate(name, response);
        return Result.ok().setSuccess(xmindTemplate);
    }
}
