package com.pp.ldap;

import lombok.Data;

import java.util.List;


@Data
public class Role {
    private String id;
    private List<String> operation;
}
