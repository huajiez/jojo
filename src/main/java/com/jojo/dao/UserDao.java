package com.jojo.dao;

import com.jojo.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
@MyBatisRepository
public interface UserDao {
    UserEntity getUserInfo(@Param("name") String name, @Param("password") String password);
}
