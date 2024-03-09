package com.pp.dto.request;


import lombok.Data;
import org.springframework.core.annotation.Order;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}
