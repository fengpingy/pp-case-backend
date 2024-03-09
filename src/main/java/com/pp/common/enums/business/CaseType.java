package com.pp.common.enums.business;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CaseType {
    FUNCTION("功能测试"),
    INTERFACE("接口测试");

    private final String name;


    public static CaseType of(String type) {
        if (type == null) {
            return null;
        }
        for (CaseType value : values()) {
            if (value.name.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
