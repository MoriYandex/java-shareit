package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    List<Booking> findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(User booker, LocalDateTime end);

    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime end);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus state);

    List<Booking> findAllByItemOrderByStartDesc(Item item);

    List<Booking> findAllByItemAndBookerAndStatusAndEndIsBefore(Item item, User author, BookingStatus approved, LocalDateTime now);

    @Query("select b" +
            " from Booking b" +
            " where b.item.owner = ?1" +
            " order by b.start desc")
    List<Booking> findAllByItemOwner(User owner);

    @Query("select b" +
            " from Booking b" +
            " where b.item.owner = ?1" +
            " and b.start < ?2" +
            " and b.end > ?2" +
            " order by b.start desc")
    List<Booking> findAllByItemOwnerCurrent(User owner, LocalDateTime now);

    @Query("select b" +
            " from Booking b" +
            " where b.item.owner = ?1" +
            " and b.end < ?2" +
            " order by b.start desc")
    List<Booking> findAllByItemOwnerPast(User owner, LocalDateTime now);

    @Query("select b" +
            " from Booking b" +
            " where b.item.owner = ?1" +
            " and b.start > ?2" +
            " order by b.start desc")
    List<Booking> findAllByItemOwnerFuture(User owner, LocalDateTime now);

    @Query("select b" +
            " from Booking b" +
            " where b.item.owner = ?1" +
            " and b.status = ?2" +
            " order by b.start desc")
    List<Booking> findAllByItemOwnerByStatus(User owner, BookingStatus status);
}
