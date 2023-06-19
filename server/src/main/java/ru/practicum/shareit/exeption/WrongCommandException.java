package ru.practicum.shareit.exeption;

public class WrongCommandException extends RuntimeException {
    public WrongCommandException(String message) {
        super(message);
    }
}
