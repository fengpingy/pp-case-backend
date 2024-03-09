package com.pp.xmind;

import com.pp.entity.CaseEntity;

import java.util.List;
import java.util.Map;

public interface XmindParse {

    /**
     * 解析case
     */
    Map<String, List<CaseEntity>> analysisCase();


    /**
     * 解压地址
     * @return
     */
    XmindVersion version();



    static XmindParse with(String contentDir){
        if (XmindUtils.version(contentDir).equals(XmindVersion.XMIND2021)){
            return new Xmind2021Parse(contentDir);
        }
        return new Xmind8Parse(contentDir);
    }
}
