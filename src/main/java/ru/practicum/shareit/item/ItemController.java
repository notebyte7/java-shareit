package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
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
    public ItemDto getItemDtoById(@PathVariable int itemId) {
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    @ResponseBody
    public Collection<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public Collection<ItemDto> searchItemsByText(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @RequestParam String text) {
        return itemService.searchItemsByTest(text);
    }
}
