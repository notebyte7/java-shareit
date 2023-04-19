package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseBody
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseBody
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto,
                              @PathVariable int itemId) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    @ResponseBody
    public ItemOutputDto getItemDtoById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.getItemDtoById(userId, itemId);
    }

    @GetMapping
    @ResponseBody
    public Collection<ItemOutputDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public Collection<ItemDto> searchItemsByText(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @RequestParam String text) {
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseBody
    public CommentOutputDto createComment(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                          @PathVariable Integer itemId, @RequestBody @Valid CommentDto comment) {
        return itemService.createComment(userId, itemId, comment);
    }
}
