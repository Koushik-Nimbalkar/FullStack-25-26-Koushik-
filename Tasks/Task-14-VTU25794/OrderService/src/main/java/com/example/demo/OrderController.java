package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/{id}")
    public String getOrder(@PathVariable int id) {

        String user = restTemplate.getForObject(
            "http://localhost:8081/users/" + id,
            String.class
        );

        return "Order Id: " + id + " placed by " + user;
    }
}