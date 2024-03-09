package com.pp.dto;

import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.XmindNodeType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
public class TestPlanXmindCaseDTO {
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
     * 测试计划ID
     */
    private Long planId;
    /**
     * 镜像ID
     */
    private Long caseImageId;
    /**
     * 是否执行
     */
    private Integer isExecute;
    /**
     * 执行时间
     */
    private Date executeTime;
    /**
     * 是否通过
     */
    private Integer isPass;
    /**
     * case等级
     */
    private CaseLevel level;
    /**
     * 子节点
     */
    private List<TestPlanXmindCaseDTO> children = new ArrayList<>();
}
