package com.laptop.Laptop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CartAlreadySoldException extends  RuntimeException {

    public CartAlreadySoldException(String message){
        super(message);
    }
}
