package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
public class MemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer userSequence = 0;

    @Override
    public User add(User user) {
        user.setId(++userSequence);
        userMap.put(userSequence, user);
        return user;
    }

    @Override
    public User update(User user) {
        userMap.put(user.getId(), user);
        return user;
    }


    @Override
    public User get(Integer id) {
        return userMap.get(id);
    }

    @Override
    public void delete(Integer id) {
        userMap.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getByEmail(String email) {
        return userMap.values().stream().filter(x -> Objects.equals(x.getEmail(), email)).findFirst().orElse(null);
    }
}
