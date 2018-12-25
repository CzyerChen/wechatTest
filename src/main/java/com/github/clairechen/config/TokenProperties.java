package com.github.clairechen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Claire.Chen
 */
@Configuration
public class TokenProperties {
    public static String username;

    public static String password;

    public static String bathUrl;

    @Value("${token.username}")
    public void setUsername(String username) {
        TokenProperties.username = username;
    }
    @Value("${token.password}")
    public void setPassword(String password) {
        TokenProperties.password = password;
    }
    @Value("${token.base.url}")
    public void setBathUrl(String bathUrl) {
        TokenProperties.bathUrl = bathUrl;
    }
}
