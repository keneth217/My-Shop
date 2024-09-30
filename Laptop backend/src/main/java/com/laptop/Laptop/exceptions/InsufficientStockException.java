package com.laptop.Laptop.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InsufficientStockException extends  RuntimeException {
    public InsufficientStockException(String message){
        super(message);
    }
}
