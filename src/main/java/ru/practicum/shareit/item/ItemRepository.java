package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwner_Id(Integer id);

    @Query(" select i " +
            "from Item i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            "   or lower(i.description) like lower(concat('%', ?1, '%')))" +
            " and (i.available is true )")
    List<Item> search(String text);

    @Query("select i from Item i " +
            "where i.owner.id = ?1 ")
    List<Item> searchByOwner(int ownerId, LocalDateTime now);

    @Query("select i.id from Item i " +
            "where i.owner.id = ?1 ")
    List<Integer> searchItem_IdByOwner(int ownerId, LocalDateTime now);
}
