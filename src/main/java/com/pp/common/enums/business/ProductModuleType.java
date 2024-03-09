package com.pp.common.enums.business;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@AllArgsConstructor
@Getter
public enum ProductModuleType {
    ATS(0,"ats","ats业务线"),
    PP(1,"pp","people业务线"),
    PA(2,"pa","平台架构"),
    BI(3,"bi","bi报表业务");


    private final int code;
    private final String name;
    private final String description;

    public static ProductModuleType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (ProductModuleType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }


    public static ProductModuleType nameOf(String name){
        if (name==null)return null;
        for (ProductModuleType value : values()) {
            if (value.name.toUpperCase(Locale.ROOT).equals(name.toUpperCase(Locale.ROOT))){
                return value;
            }
        }
        return null;
    }

}
