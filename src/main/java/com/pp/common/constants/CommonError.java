package com.pp.common.constants;


import lombok.Getter;

@Getter
public enum CommonError {

    HTTP_NOT_SUPPORT("不支持的http请求方法", 500, "HTTP_NOT_SUPPORT"),
    /**
     * 实体id不存在
     */
    ENTITY_ID_NOT_EXIST("实体id不存在", 500, "ENTITY_ID_NOT_EXIST"),
    /**
     * 参数属性错误
     */
    ARG_PROPERTY_ERROR("参数属性错误", 200, "ARG_PROPERTY_ERROR"),
    /**
     * 上错失败
     */
    UPLOADS_FAIL("上错失败", 500, "UPLOADS_FAIL"),
    /**
     * 下载失败
     */
    DOWNLOADS_FAIL("下载失败", 500, "DOWNLOADS_FAIL"),
    /**
     * 文件操作错误
     */
    FILE_ACTION_ERROR("文件操作错误", 500, "FILE_ACTION_ERROR"),
    /**
     * 不支持的断言或提取位置
     */
    API_NOT_SUPPORT_BODY("操作位置不正确", 500, "ACTION_BODY_ERROR"),

    /**
     * JSON操作异常
     */
    JSON_ACTION_EXCEPTION("json操作异常", 500, "JSON_ACTION_EXCEPTION"),

    /**
     * 不支持的请求类型
     */
    REQUEST_NOT_SUPPORT("不支持的请求类型", 500, "REQUEST_NOT_SUPPORT"),


    /**
     * 认证错误，密码或用户名不存在
     */

    USERNAME_OR_PASSWORD_ERROR("密码或用户名错误", 500, "USERNAME_OR_PASSWORD_ERROR"),

    /**
     * 认证校验失败
     */
    TOKEN_AUTH_ERROR("认证错误", 401, "TOKEN_AUTH_ERROR"),


    /**
     * 文件类型不正确
     */

    FILE_TYPE_ERROR("文件类型不正确", 500, "FILE_TYPE_ERROR"),


    FILE_IS_EMPTY("文件是空的", 500, "FILE_IS_EMPTY"),
    USER_NAME_EXIST("用户已存在",500,"USER_NAME_EXIST"),
    CASE_IS_EXIST("模块下存在case，不允许删除模块", 200, "CASE_IS_EXIST"),
    TEMPLATE_IS_ERROR("模板格式错误,暂不支持该格式模板", 200, "TEMPLATE_IS_ERROR"),

    EXECUTE_INFO_ERROR("case是否执行，是否通过或执行时间不能为空",200, "EXECUTE_INFO_ERROR");
    private final String message;
    private final Integer code;
    private final String status;


    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }


    CommonError(String message, Integer code, String status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }


}
