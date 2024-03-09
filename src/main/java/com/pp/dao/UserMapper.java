package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.dto.UserDTO;
import com.pp.dto.request.query.page.UserPageQuery;
import com.pp.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<UserEntity> {

    List<UserEntity> selectUserList(UserPageQuery userPageQuery);

    UserEntity getUserByName(String name);

    int updateRoleType(UserDTO userDTO);

    List<UserEntity> selectUsersByIds(List<Long> Ids);

}
