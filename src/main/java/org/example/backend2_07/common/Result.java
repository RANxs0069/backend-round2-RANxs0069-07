package org.example.backend2_07.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//把返回结果包装成实体类，统一返回内容规范
@Data
@NoArgsConstructor//让lombok提供无参构造方法
@AllArgsConstructor//让lombok提供全参构造方法
public class Result<T> {//使用泛型
    private String msg;
    private T data;
    //带数据返回
    public static <E> Result<E> generateResult(String msg,E data) {
        return new Result<>( msg, data);
    }
    //不带数据返回
    public static  Result<?> generateResult(String msg) {return new Result<>( msg, null);}

}
