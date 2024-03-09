package com.pp.common;

import com.pp.common.constants.ResultCode;
import lombok.Data;
import lombok.experimental.Accessors;

import static com.pp.common.constants.ResultCode.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回类
 */
@Data
@Accessors(chain = true)
public class Result{
    private Integer code;
    private Boolean success;
    private String message;
    private Map<String,Object> data = new HashMap<>();


    private Result(Integer code, boolean success){
        this.code = code;
        this.success = success;
    }


    public static Result ok (){
        Result result = new Result(ResultCode.success, true);
        result.setMessage("success");
        return result;
    }

    public static Result fail(){
        Result result = new Result(fail, false);
        result.setMessage("fail");
        return result;
    }

    public Result setMessage(String message){
        this.message = message;
        return this;
    }

    public Result setCode(Integer code){
        this.code = code;
        return this;
    }

    public Result setContent(Map<String,Object> content){
        this.data = content;
        return this;
    }

    public Result setContent(Object data){
        this.data.put("content",data);
        return this;
    }


    public Result putContent(String key,Object value){
        this.data.put(key,value);
        return this;
    }
}
