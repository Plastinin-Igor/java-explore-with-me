package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    User getUserById(Long userId);

}
