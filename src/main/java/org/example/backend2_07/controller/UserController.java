package org.example.backend2_07.controller;

import jakarta.validation.Valid;
import org.example.backend2_07.common.Result;
import org.example.backend2_07.controller.request.LoginRequest;
import org.example.backend2_07.controller.request.RegisterRequest;
import org.example.backend2_07.service.UserService;
import org.example.backend2_07.service.dto.UserServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Result<?>> login(@RequestBody @Valid LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        UserServiceResult<?> usr = userService.login(username, password);
        return transform(usr);
    }

    @PostMapping("/register")
    public ResponseEntity<Result<?>> register(@RequestBody @Valid RegisterRequest registerRequest
    ){
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        UserServiceResult<?> usr = userService.register(username, password);
        return transform(usr);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<?>> getUser(@PathVariable Long id, @RequestAttribute Long userId) {//@RequestAttribute专门从Attribute中获取信息，也就是预处理器放进去的token中的信息
        if (userId.equals(id)) {
            UserServiceResult<?> usr = userService.findById(id);
            return transform(usr);
        }
        return ResponseEntity.status(403).body(Result.generateResult("只能访问本用户数据"));
    }

    @GetMapping("/admin/id/{id}")
    public ResponseEntity<Result<?>> adminGetUserById(@PathVariable Long id) {
        UserServiceResult<?> usr = userService.findById(id);
        return transform(usr);
    }

    @GetMapping("/admin/name/{username}")
    public ResponseEntity<Result<?>> adminGetUserByUsername(@PathVariable String username) {
        UserServiceResult<?> usr = userService.findByName(username);
        return transform(usr);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<Result<?>> getAllUsers() {
        UserServiceResult<?> usr = userService.findAll();
        return transform(usr);
    }

    private ResponseEntity<Result<?>> transform(UserServiceResult<?> usr){
        Result<?> result = Result.generateResult(usr.getMsg(), usr.getData());
        return ResponseEntity.status(usr.getCode()).body(result);
    }
}
