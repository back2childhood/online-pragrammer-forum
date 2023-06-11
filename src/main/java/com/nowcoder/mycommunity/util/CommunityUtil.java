package com.nowcoder.mycommunity.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    // generate a random string
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5 encryption
    // MD5 can do encryption, but can not decrypt
    // the same string is encrypted with the same result.
    // so in order to improve the security of our password,
    // this function could automatically add a random string after the password
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "tom");
        map.put("age", "25");
        System.out.println(getJSONString(0, "ok", map));
    }
}
