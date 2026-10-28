package org.example.backend2_07.service;

import org.example.backend2_07.service.dto.UserServiceResult;

public interface UserService {
    public UserServiceResult<?> register(String username, String password);
    public UserServiceResult<?> login(String username, String password);
    public UserServiceResult<?> findById(Long id);
    public UserServiceResult<?> findByName(String username);
    public UserServiceResult<?> findAll();
}
