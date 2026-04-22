CREATE DATABASE IF NOT EXISTS payment_db;
USE payment_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    balance DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS merchants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    balance DECIMAL(10,2)
);

INSERT INTO users (name, balance) VALUES
('Anruth', 5000),
('Rahul', 3000);

INSERT INTO merchants (name, balance) VALUES
('Amazon', 2000),
('Flipkart', 1500);