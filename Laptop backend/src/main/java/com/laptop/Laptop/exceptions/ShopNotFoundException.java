package com.laptop.Laptop.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ShopNotFoundException extends RuntimeException{
    public ShopNotFoundException(String message){
        super(message);
    }
}
