package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "requests")
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "description")
    String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requestor_id")
    User requestor;
    LocalDateTime created;
    @OneToMany(mappedBy = "request", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonBackReference
    List<Item> items;

    public ItemRequest(int id, String description, User requestor, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
