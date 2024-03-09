package com.pp.dto.response.page;

import com.pp.common.enums.system.RoleType;
import lombok.Data;


@Data
public class UserPage {
    private Long id;
    private String username;
    private RoleType roleType;
    private String nickname;
}
