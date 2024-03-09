package com.pp.dto.jiraData;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchJiraDTO {
    /**
     *批量查询jira数据
     */
    private String labels;

    private String jql;
}
