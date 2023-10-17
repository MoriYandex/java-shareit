package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    User add(User user);

    User update(User user, Integer userId);

    User get(Integer userId);

    void delete(Integer userId);

    List<User> getAll();
}
