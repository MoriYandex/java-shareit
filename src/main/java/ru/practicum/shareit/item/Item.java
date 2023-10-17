package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

    public Item(Item oldItem) {
        this.id = oldItem.getId();
        this.name = oldItem.getName();
        this.description = oldItem.getDescription();
        this.available = oldItem.getAvailable();
        this.owner = oldItem.getOwner();
        this.request = oldItem.getRequest();
    }
}
