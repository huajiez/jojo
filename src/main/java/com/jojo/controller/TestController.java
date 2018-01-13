package com.jojo.controller;

import com.jojo.dao.UserDao;
import com.jojo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class TestController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("/api/test.do")
    public String test(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response){
        return  "sssss";

    }
}
