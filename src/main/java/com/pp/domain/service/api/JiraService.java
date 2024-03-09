package com.pp.domain.service.api;

import com.pp.dto.request.JiraParamDTO;
import com.pp.dto.request.JiraResultDTO;

public interface JiraService {
    JiraResultDTO createIssue(JiraParamDTO jiraParamDTO);
}
