package com.pp.common.enums.business;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum XmindNodeType {
    MODULE(1,"模块"),
    CASE(2,"用例"),
    CASE_DESCRIPTION(3,"用例描述"),
    CASE_STEP(4, "用例步骤"),
    CASE_PRECONDITION(5, "前置条件"),
    CASE_REMARK(6, "备注"),
    CASE_EXPECT(7, "预期结果");


    private final Integer code;
    private final String name;


    public static XmindNodeType of(Integer code){
        if (code == null){
            return null;
        }
        for (XmindNodeType value : values()) {
            if (value.code.equals(code)){
                return value;
            }
        }
        return null;
    }
}
