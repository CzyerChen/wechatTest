package com.github.clairechen.utils;

import com.alibaba.fastjson.JSONObject;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;


public class JsonUtils {
  public static String toJson(Object obj) {
    return WxMpGsonBuilder.create().toJson(obj);
  }

  public static <T> T getObject(String pojo, Class<T> tclass) {
    try {
      return JSONObject.parseObject(pojo, tclass);
    }catch (Exception e){
    }
    return null;
  }
}
