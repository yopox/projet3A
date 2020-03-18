const express = require('express');
const router = express.Router();
const ip = require('ip');
let pythonProcess = 0;
const {body, validationResult} = require('express-validator');
const uri = ip.address();
const db = require('better-sqlite3')('./users.db');

function getDatabase(number,offset) {
    const db = require('better-sqlite3')('./users.db');
    let users = [];
    const rows = db.prepare('SELECT * FROM users ORDER BY lastname ASC LIMIT ? OFFSET ?').all(number,offset);
    for (row of rows) {
        let arrayRow = {
            id: row.id,
            username: row.username,
            firstname: row.firstname,
            lastname: row.lastname,
            email: row.email,
            badgeid: row.badge_id,
            promotion: row.promotion
        };
        users.push(arrayRow);
    }
    db.close();
    return users;
}

/* GET users listing no parameters. */
router.get('/', function (req, res, next) {
    res.locals.users = getDatabase(25,0);
    res.render(__dirname + '/../templates/users.ejs', {URI: uri});
});

/* GET users listing number parameters. */
router.get('/:number(10|25|50|100)', function (req, res, next) {
    console.log(req.params['number']);
    res.locals.users = getDatabase(req.params['number'],0);
    res.render(__dirname + '/../templates/users.ejs', {URI: uri});
});

/* GET users listing number parameters. */
router.get('/:number(10|25|50|100)/:offset([0-9]+)', function (req, res, next) {
    console.log(req.params['number']);
    console.log(req.params['offset']);
    res.locals.users = getDatabase(parseInt(req.params['number']),parseInt(req.params['offset']));
    res.render(__dirname + '/../templates/users.ejs', {URI: uri});
});

///(((10)|(25)|(50)|(100))(\-([0-9])+)?)?


router.post('/add-user/', function(req, res) {
    addUser(req.body);
    res.redirect('/users');
});

router.post('/edit-user/', function(req, res) {
    editUser(req.body);
    res.redirect('/users');
});

function addUser( userinfo ) {
    const db = require('better-sqlite3')('./users.db');
    const lastID = db.prepare('SELECT id FROM users ORDER BY id DESC LIMIT 1').all();
    userinfo["id"] = parseInt(lastID[0].id) + 1;
    const addRequest = db.prepare('INSERT INTO users (id, username, firstname, lastname, email, badge_id) VALUES (@id,@username, @firstname, @lastname, @email, @badgeid)');
    addRequest.run(userinfo);
}

function editUser( userinfo ) {
    const db = require('better-sqlite3')('./users.db');
    const lastID = db.prepare('SELECT id FROM users WHERE username = ?').all(userinfo.username);
    const editRequest = db.prepare('UPDATE users SET username = @username , firstname = @firstname , lastname = @lastname, email = @email, badge_id = @badgeid WHERE id = ' + lastID[0].id);
    editRequest.run(userinfo);
    console.log(userinfo["username"]);
}

module.exports = router;
