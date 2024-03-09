package com.pp.dto.jiraData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseJiraDTO {
    /**
     * jira id
     */
    private long jiraID;
    /**
     * jira key
     */
    private String key;
    /**
     * jira url
     */
    private String jiraUrl;
    /**
     * jira标题
     */
    private String summary;
    /**
     * jira 状态
     */
    private String status;
    /**
     * jira优先级
     */
    private String priority;
    /**
     * 报告人
     */
    private String Reporter;
    /**
     * 经办人
     */
    private String assignee;
    /**
     * 所属模块
     */
    private String components;
    /**
     * 标签
     */
    private String labels;
    /**
     * 创建jira时间
     */
    private Date createDate;
    /**
     * 更新jira时间
     */
    private Date updateDate;

}
