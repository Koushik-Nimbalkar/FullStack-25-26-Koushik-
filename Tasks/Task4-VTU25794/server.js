const express = require("express");
const db = require("./db");
const app = express();

app.use(express.static("."));
app.use(express.json());

app.get("/orders", (req, res) => {
    const sql = `
        SELECT c.name, p.product_name, o.quantity, p.price,
               (o.quantity * p.price) AS total, o.order_date
        FROM Orders o
        JOIN Customers c ON o.customer_id = c.customer_id
        JOIN Products p ON o.product_id = p.product_id
        ORDER BY o.order_date DESC
    `;
    db.query(sql, (err, result) => {
        if (err) throw err;
        res.json(result);
    });
});

app.get("/highest-order", (req, res) => {
    const sql = `
        SELECT *, (quantity * price) AS total
        FROM (
            SELECT o.quantity, p.price, c.name
            FROM Orders o
            JOIN Customers c ON o.customer_id = c.customer_id
            JOIN Products p ON o.product_id = p.product_id
        ) AS sub
        ORDER BY total DESC
        LIMIT 1
    `;
    db.query(sql, (err, result) => {
        if (err) throw err;
        res.json(result[0]);
    });
});

app.get("/top-customer", (req, res) => {
    const sql = `
        SELECT c.name, COUNT(o.order_id) AS total_orders
        FROM Customers c
        JOIN Orders o ON c.customer_id = o.customer_id
        GROUP BY c.customer_id
        ORDER BY total_orders DESC
        LIMIT 1
    `;
    db.query(sql, (err, result) => {
        if (err) throw err;
        res.json(result[0]);
    });
});

app.listen(3000, () => {
    console.log("Server running on port 3000");
    console.log("Open: http://localhost:3000");
});