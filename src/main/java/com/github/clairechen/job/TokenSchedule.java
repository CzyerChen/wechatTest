package com.github.clairechen.job;

import com.github.clairechen.config.TokenProperties;
import com.github.clairechen.handler.TokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Claire.Chen
 */
@Component
@Slf4j
public class TokenSchedule {
    private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    @Autowired
    private TokenHandler tokenHandler;

    @Scheduled(cron = "0 0,15,30,45 * * * ?")
    public void getTokenFor3rdParty() throws Exception {
        boolean flag = false;
        String username = TokenProperties.username;
        if (map.contains(username)) {
            String result = map.get(username);
            if (StringUtils.isBlank(result)) {
                flag = true;
            }
        } else {
            flag = true;
        }
        String pass = TokenProperties.password;
        if (flag) {
            String tokenFromServer = tokenHandler.getTokenFromServer(username, pass);
            if (StringUtils.isNotBlank(tokenFromServer)) {
                map.put(username, tokenFromServer);
            } else {
              log.error("fail to get token from server");
            }
        }
    }

    public TokenHandler getTokenHandler() {
        return tokenHandler;
    }

    public void setTokenHandler(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    public static ConcurrentHashMap<String, String> getMap() {
        return map;
    }

    public static void setMap(ConcurrentHashMap<String, String> map) {
        TokenSchedule.map = map;
    }
}
