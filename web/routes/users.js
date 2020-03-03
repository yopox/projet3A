const express = require('express');
const router = express.Router();
const ip = require('ip');
let pythonProcess = 0;
const {body, validationResult} = require('express-validator');
const uri = ip.address();
const db = require('better-sqlite3')('./coucou.db');

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
    res.locals.users = getDatabase();
    res.render(__dirname + '/../templates/users.ejs', {URI: uri});
});

router.post('/add-user/', function(req, res) {
    console.log(req.body);
    addUser(req.body);
    res.redirect('/users');
});

function addUser( userinfo ) {
    const db = require('better-sqlite3')('./coucou.db');
    const lastID = db.prepare('SELECT id FROM users ORDER BY id DESC LIMIT 1').all();
    console.log(lastID);
    const newID = parseInt(lastID[0].id) + 1;
    console.log(newID);
    userinfo["id"] = newID;
    console.log(userinfo);
    const addRequest = db.prepare('INSERT INTO users (id, username, firstname, lastname, email, badge_id) VALUES (@id,@username, @firstname, @lastname, @email, @badgeid)');
    addRequest.run(userinfo);
    console.log(userinfo["username"]);
}

module.exports = router;
