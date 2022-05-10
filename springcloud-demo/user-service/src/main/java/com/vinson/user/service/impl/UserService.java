package com.vinson.user.service.impl;

import com.vinson.user.dao.UserDao;
import com.vinson.user.domain.User;
import com.vinson.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getById(Integer id) {
        return userDao.selectById(id);
    }
}
