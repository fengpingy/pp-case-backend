package com.pp.dto;

import com.pp.common.enums.business.XmindNodeType;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class XmindCaseDTO {
    /**
     * 节点ID，模块ID或者caseID(前置条件、步骤、预期结果、备注等节点ID随机生成)
     */
    private Long id;
    /**
     * 节点名称
     */
    private String title;
    /**
     * 节点类型，（模块、用例、前置条件、步骤、预期结果、备注）
     */
    private XmindNodeType type;
    /**
     * 父节点ID
     */
    private Long parentId;
    /**
     * 子节点
     */
    private List<XmindCaseDTO> children = new ArrayList<>();

}
