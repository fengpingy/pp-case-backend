package com.pp.dto.response;


import lombok.Data;

@Data
public class LoginResponseDTO {
    private Boolean isFirstLogin;
    private Long id;
    private String name;
    private String token;
    private int type;
}
