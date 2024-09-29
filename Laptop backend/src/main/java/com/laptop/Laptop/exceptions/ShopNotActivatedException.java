package com.laptop.Laptop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ShopNotActivatedException extends RuntimeException {
    public ShopNotActivatedException(String message) {
        super(message);
    }
}
