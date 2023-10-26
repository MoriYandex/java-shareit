package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> getAllByItemOrderByCreatedDesc(Item item);

    @Query("select c" +
            " from Comment c" +
            " where c.item.owner = ?1")
    List<Comment> getAllByOwner(User owner);
}
