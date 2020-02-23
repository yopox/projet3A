const express = require('express');
const router = express.Router();
const fs = require('fs');
const sqlite3 = require('sqlite3').verbose();
const db = new sqlite3.Database('./coucou.db');
let users = [];

db.serialize(function () {
    console.log("createTable user");
    db.run("CREATE TABLE IF NOT EXISTS users " +
        "(id INT PRIMARY KEY NOT NULL," +
        "lastname VARCHAR(100)," +
        "firstname VARCHAR(100)," +
        "username VARCHAR(100) UNIQUE NOT NULL," +
        "email VARCHAR(255)," +
        "badgeid VARCHAR(64)" +
        ")"
    );
    // let stmt = db.prepare('INSERT INTO users VALUES (?,?,?,?,?,?)');
    //
    // stmt.run(0, "de Boutray", "Thibault", "deboutray_thi", "thibault.deboutray@supelec.fr", "123456789");
    // stmt.run(1, "Vignier", "Louis", "vignier_lou", "louis.vignier@supelec.fr", "101112131");
    //
    //
    // stmt.finalize();

    db.each('SELECT * FROM users', function (err, row) {
        console.log(row.id + ': ' + row.username);
        let arrayRow = {
            id: row.id,
            username: row.username,
            firstname: row.firstname,
            lastname: row.lastname,
            email: row.email,
            badgeid: row.badgeid
        };
        users.push(arrayRow);
    })
});

db.close();


/* GET users listing. */
router.get('/', function (req, res, next) {
    res.render(__dirname + '/../templates/users.ejs', {users: users});
});

module.exports = router;
