package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="car")
public class CarEntity {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@NotBlank(message="Car name cannot be empty")
@Size(min = 2, message="Car name must contain at least 2 characters")
private String name;

@NotNull(message="Cost cannot be null")
@Min(value = 300000, message="Cost must be minimum 3 lakhs")
private Double cost;

@NotBlank(message="Email cannot be empty")
@Email(message="Invalid email format")
private String email;

@NotBlank(message="Mobile number cannot be empty")
@Pattern(regexp = "^[6-9]\\d{9}$", message="Mobile number must be 10 digits and start with 6-9")
private String mobile;

@NotBlank(message="Address cannot be empty")
@Size(min = 5, message="Address must be at least 5 characters")
private String address;

public CarEntity() {}

public CarEntity(Long id, String name, Double cost, String email, String mobile, String address) {
    this.id = id;
    this.name = name;
    this.cost = cost;
    this.email = email;
    this.mobile = mobile;
    this.address = address;
}

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getName() { return name; }
public void setName(String name) { this.name = name; }

public Double getCost() { return cost; }
public void setCost(Double cost) { this.cost = cost; }

public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }

public String getMobile() { return mobile; }
public void setMobile(String mobile) { this.mobile = mobile; }

public String getAddress() { return address; }
public void setAddress(String address) { this.address = address; }


}
