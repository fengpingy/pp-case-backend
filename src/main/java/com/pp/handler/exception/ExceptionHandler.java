package com.pp.handler.exception;


import com.pp.common.Result;
import com.pp.expection.PpExpection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * 异常处理器
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandler {


    /**
     * 自定义异常处理
     * @param mokaExpection
     * @return
     */
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(PpExpection.class)
    public Result mokaE(PpExpection mokaExpection){
        log.error("枚举异常，异常类型：{}",mokaExpection.getMsg());
        return Result.fail().setCode(mokaExpection.getCode()).setMessage(mokaExpection.getMsg());
    }



    /**
     * jsr303
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型：{}",e.getMessage());
        BindingResult result = e.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        result.getFieldErrors().forEach(fieldError -> {
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(" ");
        });
        return Result.fail().setContent(stringBuilder.toString());
    }

    /**
     *
     *
     *
     *
     * 全局异常
     * @param exception
     * @return
     */
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public Result ExpE(Exception exception){
        exception.printStackTrace();
        log.error("全局未知异常，异常类型：{}",exception.getMessage());
        return Result.fail().setContent(exception.getMessage());
    }
}
