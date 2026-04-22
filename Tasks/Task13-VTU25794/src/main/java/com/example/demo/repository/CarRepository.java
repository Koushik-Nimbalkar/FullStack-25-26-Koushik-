package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.CarEntity;

public interface CarRepository extends JpaRepository<CarEntity, Long> {

}