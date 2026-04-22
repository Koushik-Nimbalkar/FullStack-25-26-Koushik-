package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class FactorialService {
public long calculateFactorial(int n) {
	long fact=1;
	for(int i=1;i<=n;i++) {
		fact*=i;
	}
	return fact;
}
}
