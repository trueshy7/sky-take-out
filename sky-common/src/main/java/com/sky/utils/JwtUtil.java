package com.sky.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);
        System.out.println("令牌过期时间: " + exp);
        System.out.println("当前时间: " + new Date());
        System.out.println("是否已过期: " + new Date().after(exp));
        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 保存当前线程的类加载器
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // 切换为应用类加载器
            Thread.currentThread().setContextClassLoader(
                    JwtUtil.class.getClassLoader()
            );

            log.info("解析JWT令牌:{}",token);


            // 解析令牌
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
            return claims;

        } catch (MalformedJwtException e) {
            log.error("JWT格式错误: {}", e.getMessage());
            throw new RuntimeException("令牌格式无效", e);
        } catch (ExpiredJwtException e) {
            log.error("JWT已过期: {}", e.getMessage());
            throw new RuntimeException("令牌已过期", e);
        } catch (SignatureException e) {
            log.error("JWT签名验证失败: {}", e.getMessage());
            throw new RuntimeException("令牌签名无效", e);
        } catch (Exception e) {
            log.error("JWT解析未知错误: {}", e.getMessage());
            throw new RuntimeException("令牌解析失败", e);
        }finally {
            // 恢复原来的类加载器
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }
}