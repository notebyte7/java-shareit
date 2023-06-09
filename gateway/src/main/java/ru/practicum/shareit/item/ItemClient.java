package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exeption.WrongCommandException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItemsByOwner(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        checkFromSize(from, size);
        return get("?from={from}&size={size}", userId, parameters);

    }

    public ResponseEntity<Object> getItemDtoById(long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> searchItemsByText(long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        checkFromSize(from, size);
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);

    }

    public ResponseEntity<Object> createItem(long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> createComment(long userId, Long id, CommentDto commentDto) {
        return post("/" + id + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> updateItem(long userId, Long id, String json) {
        return patch("/" + id, userId, json);
    }

    protected void checkFromSize(Integer from, Integer size) {
        if (!(from >= 0 && size > 0)) {
            throw new WrongCommandException("Неправильный запрос from и size");
        }
    }
}
