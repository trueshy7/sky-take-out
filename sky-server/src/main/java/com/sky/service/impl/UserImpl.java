package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserImpl  implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserMapper userMapper;

    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String openid = getOpenId(userLoginDTO.getCode());
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.selectByopenId(openid);
        if(user == null){
            user = User.builder()
                        .openid(openid)
                        .createTime(LocalDateTime.now())
                        .build();

            userMapper.insert(user);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        UserLoginVO  userLoginVO = UserLoginVO.builder()
                                                .id(user.getId())
                                                .openid(openid)
                                                .token(jwt)
                                                .build();
        return userLoginVO;
    }
    public String getOpenId(String code) {
        HashMap map = new HashMap();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
