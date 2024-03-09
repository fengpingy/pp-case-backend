package com.pp.dto.request.query.page;

import com.pp.common.enums.system.RoleType;
import lombok.Data;

@Data
public class UserPageQuery {
    private String userName;
    private RoleType roleType;
    private String nickName;
    private Long id;
}
