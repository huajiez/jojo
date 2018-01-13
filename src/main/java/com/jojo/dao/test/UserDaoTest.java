package com.jojo.dao.test;

import com.jojo.dao.UserDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDaoTest extends BaseTest{
    @Autowired
    private UserDao userDao;
    @Test
    public void test(){
        String name = "zhj";
        String password = "123456";
        System.out.println(userDao.getUserInfo(name,password));
    }
}
