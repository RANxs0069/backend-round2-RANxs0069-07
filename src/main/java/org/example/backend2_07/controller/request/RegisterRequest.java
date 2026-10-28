package org.example.backend2_07.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

    @NotNull(message = "用户名和密码不能为空")
    @NotBlank(message = "用户名和密码不能为空")
    private String username;
    @NotNull(message = "用户名和密码不能为空")
    @NotBlank(message = "用户名和密码不能为空")
    private String password;
}
