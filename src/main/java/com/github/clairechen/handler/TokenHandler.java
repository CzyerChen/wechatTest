package com.github.clairechen.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.clairechen.config.TokenProperties;
import com.github.clairechen.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Claire.Chen
 */
@Service
@Slf4j
public class TokenHandler {

    public String getTokenFromServer(String username,String password) {
        String host = TokenProperties.bathUrl;
        String path = "/thirdparty/api/authenticate";

        Map<String, String> paramHeader = new HashMap();
        paramHeader.put("Accept", "application/json");
        Map<String, String> paramBody = new HashMap();

        paramBody.put("username", username);
        paramBody.put("password", password);

        JSONObject o = new JSONObject();
        o.put("username", username);
        o.put("password", password);
        try {
            HttpResponse response = HttpUtils.postJson(host, path, paramHeader, paramBody, o.toJSONString(), null);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject parse = (JSONObject) JSON.parse(result);
                System.out.println("第一步收到的结果是 \n" + JSON.toJSONString(parse, true));

                String token = parse.getJSONObject("data").getString("token");
                System.out.println("获得的token是  " + token);
                return token;
            }
        }catch (Exception e){
            log.error("get token from server meet exception{}",e);
            return  null;
        }
        return null;
}

}
