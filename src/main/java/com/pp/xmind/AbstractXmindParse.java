package com.pp.xmind;


import com.pp.common.constants.SystemConst;
import com.pp.entity.CaseEntity;
import com.pp.utils.FileUtils;
import com.pp.utils.SystemUtils;
import lombok.Data;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Data
public abstract class AbstractXmindParse implements XmindParse {


    protected Map<String, List<CaseEntity>> caseModuleMap = new LinkedHashMap<>();


    protected StringBuffer initModuleName = new StringBuffer(SystemConst.DEFAULT_MODULE_NAME);
    /**
     * 内容
     */
    protected String content;

    /**
     * 第二个节点的内容
     */
    protected String secondFloorContent;

    /**
     * 内容文件所在目录
     */
    protected String contentFileDir;

    /**
     * 获取版本
     * @return
     */

    /**
     * 处理所有内容
     *
     * @return
     */
    public void parseContent() {
        this.content =  FileUtils.readFileToString(contentFileDir + SystemConst.PATH_SEPARATOR + XmindUtils.version(contentFileDir).getContentMainFile());
    }


    @Override
    //获取XMIND版本
    public XmindVersion version() {
        if (XmindUtils.isXmind2021(contentFileDir)) {
            return XmindVersion.XMIND2021;
        }
        return XmindVersion.XMIND8_UPDATE9;
    }

    /**
     * 获取次二层内容
     *
     * @return
     */
    abstract void parseSecondFloor();

    public AbstractXmindParse(String contentFileDir) {
        this.contentFileDir = contentFileDir;
        parseContent();
        parseSecondFloor();

    }
}
