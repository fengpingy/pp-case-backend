//package com.moka.domain.service.impl;
//
//import com.atlassian.jira.rest.client.api.JiraRestClient;
//import com.atlassian.jira.rest.client.api.domain.BasicComponent;
//import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
//import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
//import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
//import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
//import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
//import com.moka.domain.service.api.JiraService;
//import com.moka.dto.request.JiraParamDTO;
//import com.moka.dto.request.JiraResultDTO;
//import com.moka.jira.JiraAuth;
//import java.net.URI;
//
//public class JiraServiceImpl implements JiraService {
//    @Override
//    public JiraResultDTO createIssue(JiraParamDTO jiraParamDTO) {
//        // 创建JIRA客户端
//        JiraRestClient jiraRestClient = JiraAuth.createJiraRestClient("tianpeng", "Tp+123456");
//        // 组装jira issue
//        IssueInputBuilder builder = new IssueInputBuilder();
//        builder.setSummary(jiraParamDTO.getSummary());
//        builder.setProjectKey(jiraParamDTO.getProjectKey());
//        builder.setFieldInput(new FieldInput(IssueFieldId.ISSUE_TYPE_FIELD, ComplexIssueInputFieldValue.with("name", "故障")));
//        builder.setFieldInput(new FieldInput(IssueFieldId.COMPONENTS_FIELD, ComplexIssueInputFieldValue.with("name", "绩效")));
//        builder.setAssigneeName(jiraParamDTO.getAssigneeName());
//        builder.setComponents(new BasicComponent(URI.create("https://jira.mokahr.com/rest/api/2/component/10729"), 10729L, "绩效", null));
//        builder.setDescription(jiraParamDTO.getDescription());
//        IssueInput issueInput = builder.build();
//        // 组装返回结果
//        JiraResultDTO jiraResultDTO = new JiraResultDTO();
//        try {
////            BasicIssue issue = jiraRestClient.getIssueClient().createIssue(issueInput).claim();
////            jiraResultDTO.setId(issue.getId());
////            jiraResultDTO.setSelf(issue.getSelf().toString());
////            jiraResultDTO.setKey(issue.getKey());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return jiraResultDTO;
//    }
//}
