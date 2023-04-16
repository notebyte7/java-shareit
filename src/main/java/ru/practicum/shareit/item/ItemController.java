package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithLastAndNextBookingAndComments;

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
    public ItemWithLastAndNextBookingAndComments getItemDtoById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.getItemDtoById(userId, itemId);
    }

    @GetMapping
    @ResponseBody
    public Collection<ItemWithLastAndNextBookingAndComments> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public Collection<ItemDto> searchItemsByText(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @RequestParam String text) {
        return itemService.searchItemsByTest(text);
    }
}
