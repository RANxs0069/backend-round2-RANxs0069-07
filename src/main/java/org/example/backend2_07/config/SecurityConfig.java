package org.example.backend2_07.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
//配置类，负责管理编码器
@Configuration
public class SecurityConfig {

    // 新密码使用哪种算法，由配置决定；库里存 {算法}散列 前缀
    @Value("${app.password.algorithm:bcrypt}")//有个默认值以防没有相关配置
    private String idForEncode;

    @Bean
    public PasswordEncoder passwordEncoder() {
        //创建一个哈希表，存放多个不同的编码器对象，由DelegatingPasswordEncoder统一管理，根据请求调用所需对象
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        // 下面两个只用于演示
        encoders.put("sha256", new StandardPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        //生成新密码时使用的算法+所有支持的编码器对象列表
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
}
