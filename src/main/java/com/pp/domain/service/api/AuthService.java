package com.pp.domain.service.api;

import com.pp.dto.request.LoginRequestDTO;
import com.pp.dto.response.LoginResponseDTO;

public interface AuthService {

    /**
     * 登录
     * @param loginRequestDTO
     * @return
     */
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
