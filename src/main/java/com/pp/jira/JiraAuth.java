package com.pp.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.pp.common.UserHolder;
import com.pp.common.constants.SystemConst;
import com.pp.dto.jiraData.BaseJiraDTO;
import com.pp.utils.SecurityUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class JiraAuth {

    public static JiraRestClient loginJira() throws URISyntaxException {
        URI jiraServer = new URI(SystemConst.JIRA_SERVER);
        String username = UserHolder.get().getUsername();
        String password = UserHolder.get().getPassword();
        AsynchronousJiraRestClientFactory asynchronousJiraRestClientFactory = new AsynchronousJiraRestClientFactory();
        JiraRestClient jiraRestClient = asynchronousJiraRestClientFactory.createWithBasicHttpAuthentication(jiraServer, username, SecurityUtils.Base64ToString(password));
        return jiraRestClient;
    }

    /**
     * 获取指定Issue对象
     */
    public static BaseJiraDTO getIssueInfo(JiraRestClient restClient, String issueNum) throws URISyntaxException {
        try {

            final Issue issue = restClient.getIssueClient().getIssue(issueNum).claim();
            System.out.println(issue.getComponents());
            String componentsName = issue.getComponents().iterator().hasNext() ? issue.getComponents().iterator().next().getName() : "";
            BaseJiraDTO.BaseJiraDTOBuilder jiraDTO = BaseJiraDTO.builder().jiraID(issue.getId())
                    .key(issue.getKey()).
                            assignee(Objects.requireNonNull(issue.getAssignee())
                                    .getDisplayName()).summary(issue.getSummary())
                    .priority(Objects.requireNonNull(issue.getPriority())
                            .getName()).status(issue.getStatus().getName())
                    .jiraUrl(SystemConst.JIRA_SERVER + "browse/" + issue.getKey())
                    .labels(Objects.requireNonNull(issue.getLabels()).toString())
                    .components(componentsName)
                    .Reporter(Objects.requireNonNull(issue.getReporter()).getDisplayName())
                    .createDate(issue.getCreationDate().toDate()).updateDate(issue.getUpdateDate().toDate());
            return jiraDTO.build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<BaseJiraDTO> searchIssues(JiraRestClient restClient, String jql) throws URISyntaxException {
        String labelsKey = String.format("labels in (%s)", jql);
        SearchResult searchResult = restClient.getSearchClient().searchJql(labelsKey).claim();
        List<BaseJiraDTO> baseJiraDTOList = new ArrayList<>();
        Iterable<Issue> iterIssue = searchResult.getIssues();
        if (!iterIssue.iterator().hasNext()) {
            return baseJiraDTOList;
        }
        BaseJiraDTO baseJiraDTO = BaseJiraDTO.builder().build();
        iterIssue.forEach(issue -> {
            String componentsName = issue.getComponents().iterator().hasNext() ? issue.getComponents().iterator().next().getName() : "";
            baseJiraDTO.setJiraID(issue.getId());
            baseJiraDTO.setKey(issue.getKey());
            baseJiraDTO.setAssignee(Objects.requireNonNull(issue.getAssignee())
                    .getDisplayName());
            baseJiraDTO.setSummary(issue.getSummary());
            baseJiraDTO.setPriority(Objects.requireNonNull(issue.getPriority())
                    .getName());
            baseJiraDTO.setStatus(issue.getStatus().getName());
            baseJiraDTO.setJiraUrl(SystemConst.JIRA_SERVER + "browse/" + issue.getKey());
            baseJiraDTO.setLabels(Objects.requireNonNull(issue.getLabels()).toString());
            baseJiraDTO.setComponents(componentsName);
            baseJiraDTO.setReporter(Objects.requireNonNull(issue.getReporter()).getDisplayName());
            baseJiraDTO.setCreateDate(issue.getCreationDate().toDate());
            baseJiraDTO.setUpdateDate(issue.getUpdateDate().toDate());
            baseJiraDTOList.add(baseJiraDTO);
        });

        return baseJiraDTOList;

    }


}
