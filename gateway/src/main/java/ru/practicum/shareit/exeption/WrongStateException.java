package ru.practicum.shareit.exeption;

import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class WrongStateException extends RuntimeException {
    public WrongStateException(String message) {
        super(message);
    }
}
