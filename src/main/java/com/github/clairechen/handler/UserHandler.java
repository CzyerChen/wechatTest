package com.github.clairechen.handler;

import com.github.clairechen.builder.TextBuilder;
import com.github.clairechen.config.WechatMpProperties;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Claire.Chen
 */
@Component
public class UserHandler extends AbstractHandler {
    public static String USER_INFO_MAPPING = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    public static String ACCESS_TOKEN="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    @Autowired
    private WechatMpProperties properties;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        String url = USER_INFO_MAPPING ;
        String openId = (String)map.get("openId");
        if(StringUtils.isNotBlank(openId)) {
            try {
                url = url.replace("KEYWORD", URLEncoder.encode(openId, "UTF-8"));
                String result = wxMpService.get(url,null);
                TextBuilder builder = new TextBuilder();
                return builder.build(result,wxMpXmlMessage,wxMpService);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
