package com.laptop.Laptop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @GetMapping
    public ResponseEntity<String> user(){
        return  ResponseEntity.ok("welcome user");
    }
}
