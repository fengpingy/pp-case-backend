package com.pp.service.impl;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.pp.dto.jiraData.BaseJiraDTO;
import com.pp.dto.jiraData.SearchJiraDTO;
import com.pp.jira.JiraAuth;
import com.pp.service.JiraService;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

@Service
public class JiraServiceImpl implements JiraService {


    @Override
    public BaseJiraDTO getIssueInfo(String key) {
        try {
            JiraRestClient jiraRestClient = JiraAuth.loginJira();
            return JiraAuth.getIssueInfo(jiraRestClient, key);
        }catch (URISyntaxException uriSyntaxException){
            uriSyntaxException.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BaseJiraDTO> searchIssues(SearchJiraDTO searchKey) {
        try{
            JiraRestClient jiraRestClient = JiraAuth.loginJira();
            return JiraAuth.searchIssues(jiraRestClient, searchKey.getLabels());
        }catch (URISyntaxException uriSyntaxException){
            uriSyntaxException.printStackTrace();
        }
        return null;
    }
}
