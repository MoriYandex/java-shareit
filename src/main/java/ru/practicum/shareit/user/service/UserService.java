package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto, Integer userId);

    UserDto get(Integer userId);

    void delete(Integer userId);

    List<UserDto> getAll();
}
