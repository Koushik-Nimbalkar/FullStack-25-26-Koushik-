package com.example.demo.controller;

import com.example.demo.entity.CarEntity;
import com.example.demo.repository.CarRepository;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarRepository repo;

    public CarController(CarRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public CarEntity addCar(@Valid @RequestBody CarEntity car) {
        return repo.save(car);
    }

    @GetMapping
    public List<CarEntity> getAllCars(){
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public CarEntity getCar(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public CarEntity updateCar(@PathVariable Long id,
                               @Valid @RequestBody CarEntity car) {

        CarEntity existing = repo.findById(id).orElse(null);

        if(existing == null) return null;

        existing.setName(car.getName());
        existing.setCost(car.getCost());

        return repo.save(existing);
    }

    @DeleteMapping("/{id}")
    public String deleteCar(@PathVariable Long id) {

        repo.deleteById(id);

        return "Deleted car id = " + id;
    }

    @GetMapping("/count")
    public long countCars() {
        return repo.count();
    }
}