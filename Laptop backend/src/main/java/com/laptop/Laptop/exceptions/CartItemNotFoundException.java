package com.laptop.Laptop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CartItemNotFoundException extends  RuntimeException {

    public CartItemNotFoundException(String message){
        super(message);
    }
}
