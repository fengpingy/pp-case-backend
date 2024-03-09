package com.pp.service;

import com.pp.dto.UserDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.UserPageQuery;
import com.pp.dto.response.LoginResponseDTO;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.UserPage;
import com.pp.entity.UserEntity;

import java.util.List;

public interface UserService {
    /**
     * 新增用户
     * @param userDTO
     * @return
     */
    LoginResponseDTO addUser(UserDTO userDTO);

    /**
     *
     * @param userPageQueryPageDTO
     * @return
     */
    PageResponse<UserPage> userList(PageDTO<UserPageQuery> userPageQueryPageDTO);

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    UserDTO getUser(Long id);

    /**
     * 通过用户名获取用户信息
     */
    UserDTO getUserByName(String name);

    /**
     * 修改成员角色
     */
    int updateRoleType(UserDTO userDTO);

    List<UserEntity> selectUsersByIds(List<Long> Ids);
}
