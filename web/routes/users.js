const express = require('express');
const router = express.Router();
const fs = require('fs');
const sqlite3 = require('sqlite3').verbose();
const db = new sqlite3.Database('./coucou.db');
let users = [];
const ip = require('ip');
let pythonProcess = 0;


db.serialize(function () {
    console.log("createTable user");
    db.run("CREATE TABLE IF NOT EXISTS users " +
        "(id INT PRIMARY KEY NOT NULL," +
        "lastname VARCHAR(100)," +
        "firstname VARCHAR(100)," +
        "username VARCHAR(100) UNIQUE NOT NULL," +
        "email VARCHAR(255)," +
        "badge_id VARCHAR(64) UNIQUE," +
        "skowa_id VARCHAR(64) UNIQUE" +
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
        console.log("Extracting row #" + row.id + ': ' + row.username);
        let arrayRow = {
            id: row.id,
            username: row.username,
            firstname: row.firstname,
            lastname: row.lastname,
            email: row.email,
            badgeid: row.badge_id
        };
        users.push(arrayRow);
    })
});

db.close();



/* GET users listing. */
router.get('/', function (req, res, next) {
    const uri = ip.address();
    res.locals.users = users;
    res.render(__dirname + '/../templates/users.ejs', {URI: uri});
});

router.post('/start-server', function (req, res) {
    let spawn = require("child_process").spawn;
    if (pythonProcess !== 0) {
        console.log("stoping already existing server");
        pythonProcess.kill();
    }
    console.log("starting server !");
    pythonProcess = spawn('python3', ["./fake_NFC_badge.py"]);
    //pythonProcess = spawn('python3',["./poll_NFC_badge.py"] );
});

router.post('/stop-server', function (req, res) {
    console.log("stoping server !");
    pythonProcess.kill();
    pythonProcess = 0;

});

module.exports = router;
