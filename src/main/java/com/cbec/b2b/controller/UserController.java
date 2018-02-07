package com.cbec.b2b.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbec.b2b.api.UserApi;
import com.cbec.b2b.common.RedisUtil;
import com.cbec.b2b.common.TokenUtils;
import com.cbec.b2b.common.Util;
import com.cbec.b2b.entity.request.LoginEntity;
import com.cbec.b2b.entity.response.LoginResponseEntity;
import com.cbec.b2b.entity.response.UserResponseEntity;
import com.cbec.b2b.response.Response;
import com.cbec.b2b.service.IUserService;

@RestController
@RequestMapping(value = "/llback/user")
public class UserController {
    @Autowired
    IUserService service;
    
    @Autowired
    UserApi api;
    
    @Autowired
    private RedisUtil redisUtil;
    
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @RequestMapping(value = "/validate")
    public String validate(@RequestBody LoginEntity loginEntity,HttpServletResponse res) {
        LoginResponseEntity response = api.validate(loginEntity.getUserName(), loginEntity.getPassword());
    	String userId = loginEntity.getUserName();
    	String token = TokenUtils.createToken(userId);
//            List<String> stringList = Arrays.asList(loginEntity.getUserName(), loginEntity.getPassword(), loginEntity.getType());
        boolean setResult = redisUtil.set(userId, token);
        response.setToken(token);
        if(setResult) {
        	logger.info(String.format("保存token成功：[%s][%s]", userId,token));
        }
        Util.responseResultSuccess(res);
        return response.toString();
    }
    
    @RequestMapping(value = "/currentUser")
    public Response<UserResponseEntity> getUser(@RequestHeader(value = "userid") String userid,HttpServletResponse res) {
    	 Util.responseResultSuccess(res);
    	return api.getUser(userid);
    }
}


