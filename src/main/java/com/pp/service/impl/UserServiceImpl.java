package com.pp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pp.dao.UserMapper;
import com.pp.dto.UserDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.UserPageQuery;
import com.pp.dto.response.LoginResponseDTO;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.UserPage;
import com.pp.entity.UserEntity;
import com.pp.expection.PpExpection;
import com.pp.service.UserService;
import com.pp.utils.BeanUtils;
import com.pp.utils.JWTUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

import static com.pp.common.constants.CommonError.USER_NAME_EXIST;
import static com.pp.utils.BeanUtils.listObjectCopyProperty;
import static com.pp.utils.PageUtils.pageInfoToPageResponse;


@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;


    @Override
    public LoginResponseDTO addUser(UserDTO userDTO) {
        LambdaQueryWrapper<UserEntity> user = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, userDTO.getUsername());
        UserEntity userInfo = userMapper.selectOne(user);
        if (userInfo != null) {
            throw new PpExpection(USER_NAME_EXIST);
        }
        UserEntity userEntity = BeanUtils.copyProperty(userDTO, UserEntity.class);
        int insert = userMapper.insert(userEntity);
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        if (insert==1){
            loginResponse.setName(userEntity.getNickname());
            loginResponse.setToken(JWTUtils.getJwtToken(String.valueOf(userEntity.getId()),userEntity.getNickname(),userEntity.getRoleType().name(),
                    userDTO.getUsername(),userDTO.getUsername()));
        }
        return loginResponse;
    }

    @Override
    public PageResponse<UserPage> userList(PageDTO<UserPageQuery> userPageQueryPageDTO) {
        Page<Object> page = PageHelper.startPage(userPageQueryPageDTO.getPageNum(), userPageQueryPageDTO.getPageSize());
        List<UserEntity> userEntities = userMapper.selectUserList(userPageQueryPageDTO.getParams());
        List<UserPage> userPages = listObjectCopyProperty(userEntities, UserPage.class);
        PageInfo<UserPage> userPageInfo = new PageInfo<>(userPages);
        userPageInfo.setTotal(page.getTotal());
        return pageInfoToPageResponse(userPageInfo);
    }

    @Override
    public UserDTO getUser(Long id) {
        UserEntity userEntity = userMapper.selectById(id);
        return BeanUtils.copyProperty(userEntity, UserDTO.class);
    }

    @Override
    public UserDTO getUserByName(String name) {
        UserEntity userEntity = userMapper.getUserByName(name);
        return BeanUtils.copyProperty(userEntity, UserDTO.class);
    }

    @Override
    public int updateRoleType(UserDTO userDTO) {
        return userMapper.updateRoleType(userDTO);
    }

    @Override
    public List<UserEntity> selectUsersByIds(List<Long> Ids) {
        return userMapper.selectUsersByIds(Ids);
    }
}
