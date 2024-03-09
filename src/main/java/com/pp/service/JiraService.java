package com.pp.service;

import com.pp.dto.jiraData.BaseJiraDTO;
import com.pp.dto.jiraData.SearchJiraDTO;

import java.util.List;

public interface JiraService {
    BaseJiraDTO getIssueInfo(String key);

    List<BaseJiraDTO> searchIssues(SearchJiraDTO searchKey);
}
