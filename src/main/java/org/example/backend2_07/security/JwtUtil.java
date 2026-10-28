package org.example.backend2_07.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
//这是工具类，负责token的生成，校验，解析等具体动作
@Component
public class JwtUtil {

    @Value("${jwt.secret}")//从配置读取密钥和失效时间
    private String secret;

    @Value("${jwt.expire-ms}")
    private long expire;

    // 把字符串密钥转成 JJWT 需要的 SecretKey 对象
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 签发 Token
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)//主体
                .claim("userId", userId)//自定义载荷字段id
                .claim("role", role)//带身份
                .issuedAt(now)//签发时间
                .expiration(new Date(now.getTime() + expire))//过期
                .signWith(getSignKey(), Jwts.SIG.HS256) //用密钥创建签名
                .compact();//组装成字符串
    }

    // 解析 Token
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //检验是否过期
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //检验是否临期
    public boolean shouldRefresh(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            // 剩余有效期小于 30 分钟就刷新
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return remaining < 30 * 60 * 1000L;//这是个判断，返回布尔类型
        } catch (Exception e) {
            return false;
        }
    }
}