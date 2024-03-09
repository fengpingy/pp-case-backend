package com.pp.dto;

import lombok.Data;

import java.util.List;

@Data
public class OwnerDTO {
    private Long ownerId;
    private List<Long> caseIds;


}
