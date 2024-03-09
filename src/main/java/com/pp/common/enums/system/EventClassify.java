package com.pp.common.enums.system;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件分类
 */
@Getter
@AllArgsConstructor
public enum EventClassify {
    /**
     * 用例操作
     */
    CASE(5),


    /**
     * 模块操作
     */
    MODULE(10),


    /**
     * 执行操作
     */
    EXECUTE(20),


    /**
     * 计划操作
     */
    PLAN(30),


    /**
     * 认证操作
     */
    AUTHENTICATION(40),


    /**
     * 缺陷操作
     */
    BUG(50),


    /**
     * 组织操作
     */
    UNIT(60),


    /**
     * 文件操作
     */
    FILE(70),

    /**
     * JIRA操作
     */
    JIRA(80),


    /**
     * 用户操作
     */
    USER(90),


    /**
     * 业务其他
     */
    BUSINESS(100);

    private final Integer classify;

    public static EventClassify of(Integer classify) {
        if (classify == null) {
            return null;
        }
        for (EventClassify value : values()) {
            if (value.classify.equals(classify)) {
                return value;
            }
        }
        return null;
    }
}
