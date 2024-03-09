package com.pp.dto.request;

import lombok.Data;

@Data
public class JiraParamDTO {
    private String description;

    private String assigneeName;

    private String projectKey;

    private String accountName;

    private String summary;
}
