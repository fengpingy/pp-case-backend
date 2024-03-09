package com.pp.xmind;


import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;

/**
 * xmind文件常量
 */
public class XmindConst {
    /**
     * 标志分隔符
     */
    public static final String SPLIT = "[:|：]{1}";

    /**
     * 标题前缀
     */
    public static final String TITLE_PREFIX = String.format("^tc[-|_]{1}p[0|1|2|3]{1}%s.*", SPLIT);
    /**
     * 前置条件前缀
     */
    public static final String PRECONDITION_PREFIX = String.format("^pc%s[\\s\\S]*", SPLIT);
    /**
     * 备注前缀
     */
    public static final String REMARK_PREFIX = String.format("^rc%s[\\s\\S]*", SPLIT);
    /**
     * id前缀
     */
    public static final String ID_PREFIX = String.format("^id%s[\\s\\S]*", SPLIT);

    /**
     * 用例标题前缀
     */
    public static final String TYPE_PREFIX = String.format("^type%s[\\s\\S]*", SPLIT);

    /**
     * Xmind8内容文件名称
     */
    public static final String XMIND8_CONTENT_FILE = "content.xml";

    /**
     * xmind2021内容文件名称
     */
    public static final String XMIND2021_CONTENT_FILE = "content.json";


    /**
     * 模块分层分隔符
     */
    public static final String MODULE_SEPARATOR = "/";


    /**
     * 标题分隔符
     */

    public static final String TITLE_SEPARATOR = "[-|_]{1}";

    /**
     * 默认用例等级
     */
    public static final CaseLevel DEFAULT_CASE_LEVEL = CaseLevel.P1;

    /**
     * 默认用例类型
     */
    public static final CaseType DEFAULT_CASE_TYPE = CaseType.FUNCTION;


    /**
     * xmind2021root
     */

    public static final String XMIND2021_ROOT = "rootTopic";
    /**
     * content文件常量
     */
    public static final String SHEET = "sheet";


    public static final String XML_MAP_STRING = "xmap-content";


    public static final String TOPIC = "topic";

    public static final String TOPICS = "topics";

    public static final String CHILDREN = "children";
}
