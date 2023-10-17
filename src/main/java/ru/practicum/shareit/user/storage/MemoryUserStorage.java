package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
@Slf4j
public class MemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer userSequence = 0;

    @Override
    public User add(User user) {
        log.info("Добавление пользователя");
        validateUser(user);
        user.setId(++userSequence);
        userMap.put(userSequence, user);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Редактирование пользователя с идентификатором {}", user.getId());
        if (!userMap.containsKey(user.getId())) {
            log.error("Пользователь с идентификатором {} не найден", user.getId());
            throw new NotFoundException(String.format("Пользователь с идентификатором %d не найден", user.getId()));
        }
        User oldUser = userMap.get(user.getId());
        User newUser = new User(oldUser);
        if (!Strings.isBlank(user.getName()))
            newUser.setName(user.getName());
        if (!Strings.isBlank(user.getEmail()))
            newUser.setEmail(user.getEmail());
        validateUser(newUser);
        userMap.put(user.getId(), newUser);
        return newUser;
    }


    @Override
    public User get(Integer id) {
        log.info("Поиск пользователя с идентификатором {}", id);
        if (!userMap.containsKey(id)) {
            log.error("Пользователь с идентификатором {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с идентификатором %d не найден", id));
        }
        return userMap.get(id);
    }

    @Override
    public void delete(Integer id) {
        log.info("Удаление пользователя с идентификатором {}", id);
        if (!userMap.containsKey(id)) {
            log.error("Пользователь с идентификатором {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с идентификатором %d не найден", id));
        }
        userMap.remove(id);
    }

    @Override
    public List<User> getAll() {
        log.info("Получение всех пользователей");
        return new ArrayList<>(userMap.values());
    }


    private void validateUser(User user) {
        if (userMap.values().stream().filter(x -> Objects.equals(x.getEmail(), user.getEmail()) && !Objects.equals(x.getId(), user.getId())).findFirst().orElse(null) != null) {
            log.error("Пользователь с электронным адресом {} уже существует!", user.getEmail());
            throw new ConflictException(String.format("Пользователь с электронным адресом %s уже существует!", user.getEmail()));
        }
        if (Strings.isBlank(user.getEmail())) {
            log.error("Электронный адрес не может быть пустым!");
            throw new ValidationException("Электронный адрес не может быть пустым!");
        }
    }
}
