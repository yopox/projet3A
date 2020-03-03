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
    res.render(__dirname + '/../templates/users.ejs', {URI: uri});
});

module.exports = router;
