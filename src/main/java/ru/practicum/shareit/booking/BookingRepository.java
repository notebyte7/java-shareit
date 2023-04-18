package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(int userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime startTime, LocalDateTime endTime);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int userId, Status status);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(int userId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 and b.end > ?2 and b.start < ?2" +
            "order by b.start desc ")
    List<Booking> findCurrentByOwner(int userId, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 and b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsByOwnItemByUserAndPast(int userId, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsByOwnItemByUserAndFuture(int userId, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 and b.status = ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsByOwnItemByUserAndStatus(int userId, Status status);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "order by b.start desc ")
    List<Booking> findByOwner(int userId);

    Collection<Booking> findBookingsByItem_Id(int itemId);

}
