package com.example.kalah;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Nonnull;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class KalahException extends RuntimeException {

    public KalahException(@Nonnull final String message) {
        super(message);
    }

    public KalahException(@Nonnull final String message,
                          @Nonnull final Throwable cause) {
        super(message, cause);
    }
}
