package com.pp.dto.request;


import lombok.Data;

@Data
public class TimeDTO {
    private String startTime;
    private String endTime;
    private Long id;
    private int type;//区分业务线，1代表ats 不传或者传0为全部
}
