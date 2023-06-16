package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByRequestId(int id);

    List<Item> findByOwnerId(Integer id);

    @Query(" select i " +
            "from Item i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            "   or lower(i.description) like lower(concat('%', ?1, '%')))" +
            " and (i.available is true )")
    List<Item> search(String text);

    @Query(" select i " +
            "from Item i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            "   or lower(i.description) like lower(concat('%', ?1, '%')))" +
            " and (i.available is true )")
    List<Item> search(Pageable pageable, String text);

    @Query("select i from Item i " +
            "where i.owner.id = ?1 ")
    List<Item> searchByOwner(Pageable pageable, int ownerId);

    @Query("select i from Item i " +
            "where i.owner.id = ?1 ")
    List<Item> searchByOwner(int ownerId);

    @Query("select i.id from Item i " +
            "where i.owner.id = ?1 ")
    List<Integer> searchItemIdByOwner(int ownerId, LocalDateTime now);

}
