package com.vinson.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vinson.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
