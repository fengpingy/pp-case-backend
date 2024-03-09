package com.pp.entity;

public enum StatucEnum {
    NOSTART("未开始"),
    ING("进行中"),
    END("按时完成"),
    NOEND("未按时完成"),
    NOCASE("计划未关联用例"),
    ERROR("异常");

    private final String name;

    private StatucEnum(String name)
    {
        this.name = name;

    } 
    public String getName() {
        return name;

    }


}
