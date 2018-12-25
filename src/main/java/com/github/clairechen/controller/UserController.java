package com.github.clairechen.controller;

import com.github.clairechen.handler.UserHandler;
import com.github.clairechen.utils.HttpsUtil;
import com.google.gson.Gson;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Claire.Chen
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserHandler userHandler;

    @RequestMapping(value = "/id",method = RequestMethod.GET)
    public String getUserInfo(@RequestParam String openId) throws IOException {
        if(StringUtils.isNotBlank(openId)){
            String access = UserHandler.ACCESS_TOKEN;
            String tmp1 = access.replaceAll("APPID", URLEncoder.encode("xxx", "UTF-8"));
            String tmp2 = tmp1.replaceAll("APPSECRET",URLEncoder.encode("xxx", "UTF-8"));
            byte[] bytes = HttpsUtil.doGet(tmp2);
            String s = new String(bytes);
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = WxMpOAuth2AccessToken.fromJson(s);
            String token = wxMpOAuth2AccessToken.getAccessToken();

            String url = UserHandler.USER_INFO_MAPPING;
            String tmp3 = url.replace("ACCESS_TOKEN", URLEncoder.encode(token, "UTF-8"));
            String tmp4 = tmp3.replace("OPENID",URLEncoder.encode(openId, "UTF-8"));
            byte[] bytes1 = HttpsUtil.doGet(tmp4);
            String ss = new String(bytes1);
            Gson gson = new Gson();
            WxMpUser user = gson.fromJson(ss,WxMpUser.class);
            user.getOpenId();

        }
        return null;
    }
}
