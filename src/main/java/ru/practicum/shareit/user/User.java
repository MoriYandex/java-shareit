package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    private String name;
    @Email
    private String email;

    public User(User oldUser) {
        this.id = oldUser.getId();
        this.name = oldUser.getName();
        this.email = oldUser.getEmail();
    }
}
