package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    RestTemplate restTemplate;

        @GetMapping("/{id}")
        public String getUser(@PathVariable int id) {
            return "user ID:" + id + " Name:Sai";
        }
    }