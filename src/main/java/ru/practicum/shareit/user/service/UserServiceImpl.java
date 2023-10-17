package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final BookingService bookingService;
    private final RequestService requestService;
    private final ItemService itemService;

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public User update(User user, Integer userId) {
        user.setId(userId);
        return userStorage.update(user);
    }

    @Override
    public User get(Integer userId) {
        return userStorage.get(userId);
    }

    @Override
    public void delete(Integer userId) {
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
        return userStorage.getAll();
    }
}
