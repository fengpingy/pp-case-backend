package com.pp.common.aspect;


import com.pp.common.EventParamHolder;
import com.pp.common.annotation.Event;
import com.pp.service.UserLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Aspect
@Order(20)
@Slf4j
public class EventAspect {


    @Resource
    private UserLogService userLogService;

    @Pointcut("@annotation(event)")
    public void eventPoint(Event event) {

    }


    @Before(value = "eventPoint(e)", argNames = "e")
    public void beforeLogPrint(Event e) {
    }

    @AfterReturning(pointcut = "eventPoint(e)", argNames = "e")
    public void afterReturnLogPrint(Event e) {
        userLogService.record(e, true);
        EventParamHolder.remove();
    }

    @AfterThrowing(pointcut = "eventPoint(e)", argNames = "e")
    public void afterThrowingLogPrint(Event e) {
        userLogService.record(e, false);
        EventParamHolder.remove();
    }

}
