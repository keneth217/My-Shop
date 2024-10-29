package com.laptop.Laptop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ImageProcessingException extends Throwable {

    public ImageProcessingException(String message, IOException e){
        super();
    }
}
