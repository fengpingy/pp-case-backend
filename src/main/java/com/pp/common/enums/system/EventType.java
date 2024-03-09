package com.pp.common.enums.system;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件类型
 */
@AllArgsConstructor
@Getter
public enum EventType {
//    --------------------------- 用例操作 ---------------------------

    ADD_CASE(110,EventClassify.CASE,"新增用例id为-> [%s]"),


    UPDATE_CASE(120,EventClassify.CASE,"更新用例id为-> [%s]"),


    DELETE_CASE(130,EventClassify.CASE,"删除用例id为-> [%s]"),


//    --------------------------- 模块操作 ---------------------------

    ADD_MODULE(210,EventClassify.MODULE,"%s新增了模块: %s"),


    UPDATE_MODULE(220,EventClassify.MODULE,"%s更新了模块: %s"),


    DELETE_MODULE(230,EventClassify.MODULE,"%s删除了模块: %s"),

    //    --------------------------- 组织操作 ---------------------------

    ADD_UNIT(310,EventClassify.UNIT,"%s新增了组织: %s"),


    UPDATE_UNIT(320,EventClassify.UNIT,"%s更新了组织: %s"),


    DELETE_UNIT(330,EventClassify.UNIT,"%s删除了组织: %s"),


    //    --------------------------- 认证操作 ---------------------------
    AUTH_LOGIN(430,EventClassify.AUTHENTICATION,"%s登录了系统：%s"),

    AUTH_LOGOUT(530,EventClassify.AUTHENTICATION,"%s退出了系统：%s"),

    //    --------------------------- 其他操作 ---------------------------
    BUSINESS_OTHER(530,EventClassify.BUSINESS,"%s操作了业务：%s"),

    //    --------------------------- 其他操作 ---------------------------
    XMIND_PARSE(610,EventClassify.FILE,"上传了xmind"),


    //    --------------------------- 计划操作 ---------------------------
    UPDATE_PLAN(610,EventClassify.PLAN,"%s更新了计划："),
    DELETE_PLAN(620,EventClassify.PLAN,"%s删除了计划：");


    private final Integer code;
    private final EventClassify classify;
    private final String template;
}
