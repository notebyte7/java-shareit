package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(int requestorId);

    Page<ItemRequest> findAll(Pageable pageable);

    ItemRequest findItemRequestById(int id);
}
