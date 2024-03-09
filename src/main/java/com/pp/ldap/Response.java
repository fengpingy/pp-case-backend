package com.pp.ldap;

import lombok.Data;

@Data
public class Response<T> {
    private Integer code;
    private Boolean success;
    private String msg;
    private T data;
}
