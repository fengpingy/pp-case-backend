package com.pp.ldap;

import lombok.Data;

import java.util.List;



@Data
public class LdapResponse {
    private Boolean hit_cache;
    private String moka_test_jwt;
    private String waring;
    private List<Permission> permissions;
    private List<Role> roles;
    private User user;
}
