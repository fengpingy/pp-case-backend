package com.pp.common.constants;


import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Locale;

/**
 * 系统常量
 */
@Slf4j
public class SystemConst {


    /**
     * 操作系统名称
     */
    private static final String OS_NAME;

    /**
     * xmind文件存储位置
     */
    public static final String XMIND_TO_ZIP_TMP_PATH;

    /**
     * csv文件存储位置
     */
    public static final String CSV_TMP_PATH;

    public static final String WINDOWS_XMIND_TO_ZIP_PATH = "C:\\tmp\\xmind";
    public static final String LINUX_XMIND_TO_ZIP_PATH = "/Users/pp/tmp";

    public static final String WINDOWS_CSV_PATH = "C:\\tmp\\csv";
    public static final String LINUX_CSV_PATH = "/Users/pp/tmp";


    /**
     * 操作系统名称属性
     */
    private static final String OS_NAME_BY = "os.name";

    /**
     * windows操作系统
     */
    private static final String WINDOWS_NAME = "WINDOWS";

    /**
     * linux操作系统
     */
    private static final String LINUX_NAME = "LINUX";

    /**
     * mac操作系统
     */
    private static final String MAC_NAME = "MAC";

    /**
     * token:SECRET
     */
    public static final String SECURITY_STRING = "pp-case";


    public static final String LINE_BREAK = "\r\n";
    /**
     * 头token名称
     */
    public static final String pp_HEADER_TOKEN = "m-token";


    /**
     * 失效时间token
     */
    public static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;


    /**
     * 空串
     */
    public static final String NULL_EMPTY_STRING = "";

    /**
     * 空格
     */
    public static final String SPACE_STRING = " ";

    /**
     * id
     */
    public static final String ID = "id";


    /**
     * subject
     */
    public static final String SUBJECT = "pp_QA";


    /**
     * 昵称
     */

    public static final String NICKNAME = "nickname";


    /**
     * 默认编号
     */

    public static final String DEFAULT_CODE = "pp_default_code";


    /**
     * 默认模块名称
     */

    public static final String DEFAULT_MODULE_NAME = "默认模块";


    /**
     * 角色
     */

    public static final String ROLE = "role";


    /**
     * 默认用户
     */
    public static final Long SYSTEM_USER = 8888888888L;


    /**
     * xmind文件临时存储区
     */


    /**
     * 文件原始名称分隔符
     */
    public static final String FILE_ORIGINAL_SEPARATOR = "\\.";


    /**
     * zip文件后缀
     */

    public static final String ZIP_FILE_SUFFIX = ".zip";

    /**
     * xmind文件后缀
     */
    public static final String XMIND_FILE_SUFFIX = ".xmind";

    /**
     * csv文件后缀
     */
    public static final String CSV_FILE_SUFFIX = ".csv";


    /**
     * 文件路径分隔符
     */
    public static final String PATH_SEPARATOR = File.separator;

    /**
     * 文件编码
     */

    public static final String FILE_ECHARTS = "utf-8";

    /**
     * ladp成功标志
     */
    public static final String LDAP_SUCCESS_FLAG = "登录成功！";

    /**
     * 临时登陆密码
     */
    public static final String PASSWORD = "password";

    /**
     * ldap用户
     */
    public static final String NAME = "name";

    /**
     * pp JIRA 地址
     */
    public static final String JIRA_SERVER = "https://jira.pphr.com/";


    //初始化系统临时路径
    static {
        OS_NAME = System.getProperty(OS_NAME_BY).split(SPACE_STRING)[0].toUpperCase(Locale.ROOT).equals(WINDOWS_NAME) ? WINDOWS_NAME : LINUX_NAME;
        XMIND_TO_ZIP_TMP_PATH = OS_NAME.equals(WINDOWS_NAME) ? WINDOWS_XMIND_TO_ZIP_PATH : LINUX_XMIND_TO_ZIP_PATH;
        CSV_TMP_PATH = OS_NAME.equals(WINDOWS_NAME) ? WINDOWS_CSV_PATH : LINUX_CSV_PATH;
    }

}
