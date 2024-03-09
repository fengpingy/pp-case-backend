package com.pp.dto.response.statement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanCaseStatisticsDTO {

    private long casesAmount = 0;

    private long casesPassAmount = 0;

    private long casesFailAmount = 0;

    Map<String, Long> executeName;



}
