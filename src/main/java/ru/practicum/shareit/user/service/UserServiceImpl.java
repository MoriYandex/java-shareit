package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final BookingService bookingService;
    private final RequestService requestService;
    private final ItemService itemService;

    @Override
    public User add(User user) {
        log.info("Добавление пользователя");
        validateUser(user);
        return userStorage.add(user);
    }

    @Override
    public User update(User user, Integer userId) {
        log.info("Редактирование пользователя с идентификатором {}", user.getId());
        User toUpdate = getById(userId);
        if (!Strings.isBlank(user.getEmail())) {
            user.setId(userId);
            validateUser(user);
            toUpdate.setEmail(user.getEmail());
        }
        if (!Strings.isBlank(user.getName())) {
            toUpdate.setName(user.getName());
        }
        //При работе с памятью вызывать метод хранилища необязательно, но оставляю с расчётом на работу с БД
        return userStorage.update(toUpdate);
    }

    @Override
    public User get(Integer userId) {
        log.info("Поиск пользователя с идентификатором {}", userId);
        return getById(userId);
    }

    @Override
    public void delete(Integer userId) {
        log.info("Удаление пользователя с идентификатором {}", userId);
        getById(userId);
        if (!bookingService.getAllByUserId(userId).isEmpty())
            throw new ValidationException(String.format("Пользователь с идентификатором %d имеет бронирования, удаление невозможно!", userId));
        if (!requestService.getAllByUserId(userId).isEmpty())
            throw new ValidationException(String.format("Пользователь с идентификатором %d имеет запросы на создание вещи, удаление невозможно!", userId));
        if (!itemService.getAllByUserId(userId).isEmpty())
            throw new ValidationException(String.format("Пользователь с идентификатором %d имеет вещи, удаление невозможно!", userId));
        userStorage.delete(userId);
    }

    @Override
    public List<User> getAll() {
        log.info("Получение всех пользователей");
        return userStorage.getAll();
    }

    private void validateUser(User user) {
        if (Strings.isBlank(user.getEmail())) {
            log.error("Электронный адрес не может быть пустым!");
            throw new ValidationException("Электронный адрес не может быть пустым!");
        }
        User alterUser = userStorage.getByEmail(user.getEmail());
        if (alterUser != null && !Objects.equals(alterUser.getId(), user.getId())) {
            log.error("Пользователь с электронным адресом {} уже существует!", user.getEmail());
            throw new ConflictException(String.format("Пользователь с электронным адресом %s уже существует!", user.getEmail()));
        }
    }

    private User getById(Integer userId) {
        User user = userStorage.get(userId);
        if (user == null) {
            log.error("Пользователь с идентификатором {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с идентификатором %d не найден", userId));
        }
        return user;
    }
}
