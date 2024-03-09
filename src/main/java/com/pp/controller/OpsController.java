package com.pp.controller;


import com.pp.common.annotation.IgnoreAuth;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *ops健康检查
 */
@RestController
@RequestMapping()
@Api(tags = {"健康检查"})
public class OpsController {
    @GetMapping("/actuator/health")
    @IgnoreAuth
    public String actuator() {
        return "oK";
    }
}
