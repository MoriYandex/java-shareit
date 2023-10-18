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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final BookingService bookingService;
    private final RequestService requestService;
    private final ItemService itemService;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto userDto) {
        log.info("Добавление пользователя");
        validateEmail(null, userDto.getEmail());
        return userMapper.toDto(userStorage.add(userMapper.fromDto(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Integer userId) {
        log.info("Редактирование пользователя с идентификатором {}", userDto.getId());
        User toUpdate = getById(userId);
        if (!Strings.isBlank(userDto.getEmail())) {
            validateEmail(userId, userDto.getEmail());
            toUpdate.setEmail(userDto.getEmail());
        }
        if (!Strings.isBlank(userDto.getName())) {
            toUpdate.setName(userDto.getName());
        }
        //При работе с памятью вызывать метод хранилища необязательно, но оставляю с расчётом на работу с БД
        return userMapper.toDto(userStorage.update(toUpdate));
    }

    @Override
    public UserDto get(Integer userId) {
        log.info("Поиск пользователя с идентификатором {}", userId);
        return userMapper.toDto(getById(userId));
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
    public List<UserDto> getAll() {
        log.info("Получение всех пользователей");
        return userStorage.getAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    private void validateEmail(Integer userId, String email) {
        User alterUser = userStorage.getByEmail(email);
        if (alterUser != null && (userId == null || !Objects.equals(alterUser.getId(), userId))) {
            log.error("Пользователь с электронным адресом {} уже существует!", email);
            throw new ConflictException(String.format("Пользователь с электронным адресом %s уже существует!", email));
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
