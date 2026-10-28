package org.example.backend2_07.service.impl;

import org.example.backend2_07.security.JwtUtil;
import org.example.backend2_07.vo.UserVO;

import org.example.backend2_07.entity.User;
import org.example.backend2_07.mapper.UserMapper;
import org.example.backend2_07.service.UserService;
import org.example.backend2_07.service.dto.UserServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {

    @Value("${web.userService.register.passwordMin}")
    private int passwordMinLength;
    @Value("${web.userService.register.passwordMax}")
    private int passwordMaxLength;
    @Value("${web.userService.register.usernameMin}")
    private int usernameMinLength;
    @Value("${web.userService.register.usernameMax}")
    private int usernameMaxLength;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserServiceResult<List<UserVO>> findAll() {
        return transform(userMapper.findAll());
    }

    @Override
    public UserServiceResult<UserVO> findByUsername(String username) {
        UserVO user = userMapper.findByUsername(username);
        if (user == null) {
            return UserServiceResult.fail(404, "用户不存在");
        }
        return UserServiceResult.success("请求成功", user);
    }

    @Override
    public UserServiceResult<UserVO> findById(Long id) {
        UserVO user = userMapper.findById(id);
        if (user == null) {
            return UserServiceResult.fail(404, "用户不存在");
        }
        return UserServiceResult.success("请求成功", user);
    }

    @Override
    public UserServiceResult<?> register (String username, String password) {
        UserVO user= userMapper.findByUsername(username);
        if (user != null){
            return UserServiceResult.fail(400, "用户名已存在");
        }else if(username.length() < usernameMinLength || username.length() > usernameMaxLength){
        return UserServiceResult.fail(400, "用户名必须为" + usernameMinLength + "-" + usernameMaxLength + "位");
        }else if(password.length() < passwordMinLength || password.length() > passwordMaxLength) {
            return UserServiceResult.fail(400, "密码必须为" + passwordMinLength + "-" + passwordMaxLength + "位");
        }
        User newUser = new User();
        newUser.setUsername(username);
        // 注册入口一律普通用户，管理员由 schema.sql 的 seed 账号提供
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole("USER");
        //装到数据库中
        userMapper.insert(newUser);
        //签发token
        Long userId = userMapper.findByUsername(username).getId();
        String role = userMapper.findByUsername(username).getRole();
        return UserServiceResult.success( "注册成功", jwtUtil.generateToken(userId, username, role));
    }

    @Override
    public UserServiceResult<?> login(String username, String password) {
        UserVO user = userMapper.findByUsername(username);
        String storedPassword = userMapper.findUserPassword(username);
        if (user == null || storedPassword == null
                || !passwordEncoder.matches(password, storedPassword)) {
            return UserServiceResult.fail(400, "用户名或密码错误");
        }
        return UserServiceResult.success("登录成功",
                jwtUtil.generateToken(user.getId(), username, user.getRole()));
    }

    private <E> UserServiceResult<E> transform(E data){
        return UserServiceResult.success("请求成功", data);
    }

}
