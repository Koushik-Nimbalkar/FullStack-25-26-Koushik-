package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	@GetMapping("sai")
	public String msg() {
		return "saiiiiiiiiiiiiii";
	}

}
