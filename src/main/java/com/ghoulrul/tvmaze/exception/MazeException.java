package com.ghoulrul.tvmaze.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MazeException extends RuntimeException {
    private final HttpStatus status;
    public MazeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
