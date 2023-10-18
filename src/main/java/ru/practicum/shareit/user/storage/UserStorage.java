package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User update(User user);

    User get(Integer id);

    void delete(Integer id);

    List<User> getAll();

    User getByEmail(String email);
}
