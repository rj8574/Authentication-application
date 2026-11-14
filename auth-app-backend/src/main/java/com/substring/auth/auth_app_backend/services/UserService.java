package com.substring.auth.auth_app_backend.services;

import com.substring.auth.auth_app_backend.dtos.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto,String UserId);

    UserDto getUserByEmail(String email);

    void deleteUser(String UserId);

    UserDto getUserById(String UserId);

    Iterable<UserDto> getAllUsers();




}
