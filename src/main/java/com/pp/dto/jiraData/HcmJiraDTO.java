package com.pp.dto.jiraData;

import lombok.*;

/**
 * people 测试环境jira字段
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HcmJiraDTO extends BaseJiraDTO{
    /**
     * 前后端问题
     */
    private String bugType;
}
