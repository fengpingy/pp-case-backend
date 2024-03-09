package com.pp.ldap;

import lombok.Data;

import java.util.List;
@Data
public class Permission {
    private String id;
    private List<String> operation;
}
