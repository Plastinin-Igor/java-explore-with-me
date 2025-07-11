package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserControllerAdmin {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                  @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил запрос GET: /admin/users с параметрами: ids: {}; from: {}, size: {}.", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Поступил запрос POST: /admin/users на добавление пользователя: {}.", newUserRequest);
        UserDto userDto = userService.addUser(newUserRequest);
        log.info("Пользователь {} успешно зарегистрирован в системе.", userDto);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Поступил запрос DELETE: /admin/users/{}", userId);
        userService.deleteUser(userId);
        log.info("Пользователь с id: {} успешно удален из системы.", userId);
    }

}
