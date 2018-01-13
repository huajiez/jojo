package com.jojo.service.impl;

import com.jojo.dao.UserDao;
import com.jojo.entity.UserEntity;
import com.jojo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    public UserEntity getUserInfo(String name, String password) {
        UserEntity userEntity = userDao.getUserInfo(name,password);
        System.out.println(userEntity);
        return userEntity;
    }
}
