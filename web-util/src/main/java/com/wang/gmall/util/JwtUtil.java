package com.wang.gmall.util;

import io.jsonwebtoken.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

public class JwtUtil {

    /**
     *
     * @param key 服务器公共key
     * @param param 用户信息
     * @param salt 盐值
     * @return
     */
    public static String encode(String key, Map<String,Object> param, String salt){
        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256,key);

        jwtBuilder = jwtBuilder.setClaims(param);

        return jwtBuilder.compact();

    }


    public  static Map<String,Object>  decode(String token ,String key,String salt){
        Claims claims=null;
        if (salt!=null){
            key+=salt;
        }
        try {
            claims= Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch ( JwtException e) {
           return null;
        }
        return  claims;
    }
}
