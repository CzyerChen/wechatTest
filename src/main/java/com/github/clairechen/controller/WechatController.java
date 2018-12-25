package com.github.clairechen.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.clairechen.builder.TextBuilder;
import com.github.clairechen.config.TokenProperties;
import com.github.clairechen.handler.TokenHandler;
import com.github.clairechen.job.TokenSchedule;
import com.github.clairechen.utils.HttpUtils;
import com.google.gson.Gson;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Claire.Chen
 */
@RestController
@RequestMapping("/wechat/portal")
public class WechatController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxMpService wxService;
    @Autowired
    private WxMpMessageRouter router;
    @Autowired
    private TokenHandler tokenHandler;
    private static ConcurrentHashMap<Long,Long> msgMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long,String> eventMap = new ConcurrentHashMap<>();


    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(
            @RequestParam(name = "signature",
                    required = false) String signature,
            @RequestParam(name = "timestamp",
                    required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {

        this.logger.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        if (this.wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "非法请求";
    }


    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam(name = "encrypt_type",
                               required = false) String encType,
                       @RequestParam(name = "msg_signature",
                               required = false) String msgSignature) throws Exception {
        this.logger.info(
                "\n接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!this.wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = null;
        TextBuilder builder = new TextBuilder();
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            if(msgMap.contains(inMessage.getMsgId())){
                WxMpXmlOutMessage outMessage = builder.build("", inMessage, null);
                logger.info("============== duplicate message about message(普通消息推送) ============");
                return outMessage.toXml();
            }else {
                msgMap.put(inMessage.getMsgId(),System.currentTimeMillis());
            }
            if(eventMap.contains(inMessage.getCreateTime())){
                String event = eventMap.get(inMessage.getCreateTime());
                if(StringUtils.isNotBlank(event) && event.equals(inMessage.getFromUser())){
                    WxMpXmlOutMessage outMessage = builder.build("", inMessage, null);
                    logger.info("============== duplicate message for event (关注取消)============");
                    return outMessage.toXml();
                }else {
                    eventMap.put(inMessage.getCreateTime(),inMessage.getFromUser());
                }
            }else {
                eventMap.put(inMessage.getCreateTime(),inMessage.getFromUser());
            }

            Gson gson = new Gson();
            String s = gson.toJson(inMessage);
            logger.info("json string is {}", s);
            ConcurrentHashMap<String, String> tokenMap = TokenSchedule.getMap();
            String token ="";
            String username = TokenProperties.username;
            String pass = TokenProperties.password;
            if(tokenMap.contains(username)){
               token = tokenMap.get(username);
                if(StringUtils.isBlank(token)){
                    token = tokenHandler.getTokenFromServer(username, pass);
                    tokenMap.put(username,token);
                }
            }

            HttpResponse response = null;
            Map<String, String> map = new HashMap<>();
            Map<String, String> head = new HashMap<>();
            head.put("Content-Type", "application/json");
            head.put("token",token);
            try {
                response = HttpUtils.doPost("http://hostname", "/imapi/thirdparty/api/wechat/inMessage", "POST", head, map, s);
            } catch (Exception e) {
                return out;
            }
            if (response != null) {
                HttpEntity entity = response.getEntity();
                String s1 = EntityUtils.toString(entity, HTTP.UTF_8);
                String data = JSONObject.parseObject(s1).getString("data");
                if (data != null) {
                    String content = JSONObject.parseObject(data).getString("content");
                    WxMpXmlOutMessage outMessage = builder.build(content, inMessage, null);
                    logger.info("============== receive info from server ============");
                    out = outMessage.toXml();
                }
                return out;
            }
        }

        return out;
    }


    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.router.route(message);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }


}
