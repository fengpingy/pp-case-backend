package com.pp.dto;


import com.pp.common.enums.system.RoleType;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
//    @NotBlank(message = "用户名不能为空")
    private String username;
//    @NotNull(message = "角色不能为空")
    private RoleType roleType;
//    @NotNull(message = "昵称不能为空")
    private String nickname;
}
