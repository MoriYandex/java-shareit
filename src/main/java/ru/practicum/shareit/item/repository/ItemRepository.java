package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwner(User owner);

    List<Item> findAllByAvailableAndDescriptionContainingIgnoreCase(boolean available, String description);

    @Query("select i " +
            " from Item i" +
            " where i.available = ?1" +
            " and (upper(i.name) like upper(concat('%', ?2, '%'))" +
            " or upper(i.description) like upper(concat('%', ?2, '%')))")
    List<Item> searchByText(boolean available, String text);
}
