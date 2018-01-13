package com.jojo.service;
import com.jojo.entity.UserEntity;


public interface UserService {
    UserEntity getUserInfo(String name, String password);
}
