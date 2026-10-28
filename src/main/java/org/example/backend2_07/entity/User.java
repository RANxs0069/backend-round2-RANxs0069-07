package org.example.backend2_07.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
//实体类，存放用户信息
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;
    private LocalDateTime createdAt;
}
