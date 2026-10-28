package org.example.backend2_07.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.backend2_07.vo.UserVO;
import org.example.backend2_07.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO backend2_07.users (username, role, password) VALUES (#{username}, #{role}, #{password})")
    void insert(User newUser) ;
    @Select("SELECT * FROM users WHERE username = #{username}")
    UserVO findByUsername(@Param("username") String username) ;
    @Select("SELECT password FROM users WHERE username = #{username}")
    String findUserPassword(@Param("username") String username) ;
    @Select("SELECT * FROM users WHERE id = #{userId}")
    UserVO findById(@Param("userId") Long userId) ;
    @Select("SELECT * FROM users ORDER BY id")
    List<UserVO> findAll() ;

}
