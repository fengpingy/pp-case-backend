package com.pp.common.enums.business;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@AllArgsConstructor
@Getter
public enum CaseLevel {
    P0(0, "p0"),
    P1(1, "p1"),
    P2(2, "p2"),
    P3(3, "p3");


    private final int code;
    private final String name;

    public static CaseLevel of(Integer code) {
        if (code == null) {
            return null;
        }
        for (CaseLevel value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }


    public static CaseLevel nameOf(String name){
        if (name==null)return null;
        for (CaseLevel value : values()) {
            if (value.name.toUpperCase(Locale.ROOT).equals(name.toUpperCase(Locale.ROOT))){
                return value;
            }
        }
        return null;
    }
}
