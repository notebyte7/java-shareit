package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                  @RequestParam(defaultValue = "0", required = false) Integer from,
                                                  @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemClient.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemDtoById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long id) {
        return itemClient.getItemDtoById(userId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestParam("text") String text,
                                                    @RequestParam(defaultValue = "0", required = false) Integer from,
                                                    @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemClient.searchItemsByText(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                             @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(ownerId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long id,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, id, commentDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                             @PathVariable Long id,
                                             @RequestBody String json) {
        return itemClient.updateItem(ownerId, id, json);
    }

}