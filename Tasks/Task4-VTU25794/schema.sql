CREATE DATABASE IF NOT EXISTS order_management;
USE order_management;

CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100),
    price DECIMAL(10,2)
);

CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    product_id INT,
    quantity INT,
    order_date DATE,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

INSERT INTO Customers (name,email) VALUES
('Anruth','anruth@gmail.com'),
('Rahul','rahul@gmail.com');

INSERT INTO Products (product_name,price) VALUES
('Laptop',60000),
('Phone',20000),
('Headphones',2000);

INSERT INTO Orders (customer_id,product_id,quantity,order_date) VALUES
(1,1,1,'2025-03-01'),
(1,2,2,'2025-03-05'),
(2,3,3,'2025-03-10');