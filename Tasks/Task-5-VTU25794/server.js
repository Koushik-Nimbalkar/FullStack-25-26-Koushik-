const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());
app.use(express.static(__dirname));

const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'anruth',
    database: 'payment_db'
});

db.connect(err => {
    if (err) throw err;
    console.log("MySQL Connected...");
});

app.post('/pay', (req, res) => {
    const { userId, merchantId, amount } = req.body;

    db.beginTransaction(err => {
        if (err) return res.send(err);

        db.query(
            "UPDATE users SET balance = balance - ? WHERE id = ? AND balance >= ?",
            [amount, userId, amount],
            (err, result) => {

                if (err || result.affectedRows === 0) {
                    return db.rollback(() => {
                        res.send("Payment Failed - Insufficient Balance");
                    });
                }

                db.query(
                    "UPDATE merchants SET balance = balance + ? WHERE id = ?",
                    [amount, merchantId],
                    (err) => {

                        if (err) {
                            return db.rollback(() => {
                                res.send("Payment Failed");
                            });
                        }

                        db.commit(err => {
                            if (err) {
                                return db.rollback(() => {
                                    res.send("Commit Failed");
                                });
                            }

                            res.send("Payment Successful (COMMIT)");
                        });
                    }
                );
            }
        );
    });
});

app.listen(3000, () => {
    console.log("Server running on http://localhost:3000");
});