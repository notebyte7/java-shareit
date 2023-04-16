package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(int userId);

    List<Booking> findBookingByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime time, LocalDateTime time2);

    List<Booking> findBookingByBookerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime time);

    List<Booking> findBookingByBookerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStatusOrderByStartDesc(int userId, Status status);

    List<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(int userId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 and b.end > ?2 and b.start < ?2" +
            "order by b.start desc ")
    List<Booking> findBookingsByOwnItemByUserAndCurrent(int userId, LocalDateTime time);

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

    Collection<Booking> findBookingsByItem_Id(int itemId);

    Collection<Booking> findBookingsByItem_Owner_Id(int userId);

}
