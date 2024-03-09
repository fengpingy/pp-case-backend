package com.pp.common.enums.jira;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ComponentsType {
    ABS(10513,"假勤"),

    PTS(10508,"人事"),

    ENT(10600,"入职"),

    IEX(11207,"创新体验"),

    CON(10601,"合同"),

    BSE(11307,"基础服务"),

    EAA(10511,"审批"),

    INE(11516,"技术中台"),

    REP(10740,"报表"),

    PAS(10704,"政策制度"),

    AUT(10512,"权限"),

    ECD(11110,"生态对接"),

    ORG(10509,"组织"),

    PER(10729,"绩效"),

    SAL(11000,"薪酬"),

    CSM(11401,"CSM后台"),

    OPL(11400,"一体化");


    @EnumValue
    private Integer id;
    @EnumValue
    private String name;

    public static ComponentsType of (Integer id){
        if (id==null){
            return null;
        }

        for (ComponentsType value: values()){
            if (value.getId().equals(id)){
                return value;
            }
        }
        return null;
    }



}
