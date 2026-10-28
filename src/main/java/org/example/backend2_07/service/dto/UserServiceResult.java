package org.example.backend2_07.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceResult<T> {
    int code;
    String msg;
    T data;

    public static UserServiceResult<?> success(String msg){
        return new UserServiceResult<>(200,msg, null);
    }
    public static <E> UserServiceResult<E> success( String msg,E data){
        return new UserServiceResult<>(200, msg, data);
    }
    public static <E> UserServiceResult<E> fail(int code, String msg){
        return new UserServiceResult<>(code, msg,null);
    }
}
