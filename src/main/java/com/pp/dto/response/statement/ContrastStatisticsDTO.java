package com.pp.dto.response.statement;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ContrastStatisticsDTO {

    private Integer devExecuteAmount = 0;
    private Integer testExecuteAmount = 0;
    private long casesAmount = 0;

    private long equalityPassAmount = 0;
    private long unequalPassAmount = 0;

    private List<DevTestContrastDTO> devTestContrasts;

    private TestExecuteStatisticsDTO testExecuteStatistics;






}
