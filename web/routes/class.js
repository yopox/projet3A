const express = require('express');
const router = express.Router();
const ip = require('ip');
let pythonProcess = 0;

function getDatabase() {
    const db = require('better-sqlite3')('./coucou.db');
    let users = [];
    const rows = db.prepare('SELECT * FROM users').all();
    for (row of rows) {
        let arrayRow = {
            id: row.id,
            username: row.username,
            firstname: row.firstname,
            lastname: row.lastname,
            email: row.email,
            badgeid: row.badge_id
        };
        users.push(arrayRow);
    }
    db.close();
    return users;
}

/* GET users listing. */
router.get('/', function (req, res, next) {
    const uri = ip.address();
    res.locals.users = getDatabase();
    res.render(__dirname + '/../templates/class.ejs', {URI: uri});
});

router.post('/start-server', function (req, res) {
    let spawn = require("child_process").spawn;
    if (pythonProcess !== 0) {
        console.log("stoping already existing server");
        pythonProcess.kill();
    }
    console.log("starting server !");
    pythonProcess = spawn('python3', ["./fake_NFC_badge.py"]);
    pythonProcess.stdout.on('data', function (data) {
        console.log(data);
    });
    //pythonProcess = spawn('python3',["./poll_NFC_badge.py"] );
});

router.post('/stop-server', function (req, res) {
    console.log("stoping server !");
    pythonProcess.kill();
    pythonProcess = 0;
});

module.exports = router;
