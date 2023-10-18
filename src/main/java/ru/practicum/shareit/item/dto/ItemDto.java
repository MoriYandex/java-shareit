package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(message = "Наименование вещи не может быть пустым!")
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым!")
    private String description;
    @NotNull(message = "Состояние доступности вещи не может быть пустым!")
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
}
