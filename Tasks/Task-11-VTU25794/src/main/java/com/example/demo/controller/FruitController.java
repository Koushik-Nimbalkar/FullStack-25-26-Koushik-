package com.example.demo.controller;
import com.example.demo.entity.Fruit;
import com.example.demo.repository.FruitRepository;

import java.util.List;

import org.springframework.web.bind.annotation.*;
@RestController 
@RequestMapping("/fruits")
public class FruitController {
	private final FruitRepository repo;
	public FruitController(FruitRepository repo) {
		this.repo = repo;
	}
	@PostMapping
	public Fruit addFruit(@RequestBody Fruit fruit) {
		return repo.save(fruit);
	}
	@GetMapping
	public List<Fruit> getAllFruits(){
		return repo.findAll();
	}
	@GetMapping("/{id}")
	public Fruit getFruit(@PathVariable Long id) {
		return repo.findById(id).orElse(null);
	}
	@PutMapping("/{id}")
	public Fruit updateFruit(@PathVariable Long id,@RequestBody Fruit fruit) {
		Fruit existing=repo.findById(id).orElse(null);
		if(existing==null) return null;
		existing.setName(fruit.getName());
		existing.setQuantity(fruit.getQuantity());
		return repo.save(existing);
	}
	@DeleteMapping("/{id}")
	public String deleteFruit(@PathVariable Long id) {
		repo.deleteById(id);
		return "Deleted fruit id = "+id;
	}
	@GetMapping("/count")
	public long countFruits() {
		return repo.count();
	}
}