package ru.practicum.shareit.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongCommandException extends RuntimeException {
    public WrongCommandException(String message) {
        super(message);
    }
}
