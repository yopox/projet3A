const express = require('express');
const router = express.Router();
const ip = require('ip');
let pythonProcess = 0;

function getDatabase(number,offset) {
    const db = require('better-sqlite3')('./users.db');
    let users = [];
    const rows = db.prepare('SELECT * FROM users WHERE promotion LIKE ? ORDER BY lastname ASC LIMIT ? OFFSET ?').all("%{BASEELEVES}SIS%",number,offset);
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

function getPromotions() {
    const db = require('better-sqlite3')('./users.db');
    let promotions = [];
    const rows = db.prepare('SELECT DISTINCT promotion FROM users ORDER BY promotion ASC').all();
    for (row of rows) {
        let arrayRow = {
            promotion: row.promotion
        };
        promotions.push(arrayRow);
    }
    db.close();
    return promotions;
}

function getSupAnn() {
    const db = require('better-sqlite3')('./users.db');
    let diplomes = [];
    const rows = db.prepare('SELECT DISTINCT supann_diplome FROM users ORDER BY supann_diplome ASC').all();
    for (row of rows) {
        let arrayRow = {
            diplome: row.supann_diplome
        };
        diplomes.push(arrayRow);
    }
    db.close();
    return diplomes;
}

/* GET users listing. */
router.get('/', function (req, res, next) {
    const uri = ip.address();
    res.locals.users = getDatabase(100,0);
    res.locals.promotions = getPromotions();
    res.locals.diplomes = getSupAnn();

    res.render(__dirname + '/../templates/class.ejs', {URI: uri});
});

router.post('/filter', function(req, res) {
    console.log(JSON.parse(req.body.selectPromotion.replace()));
    console.log(req.body.selectDiplome);
    res.redirect('/class');
});


function addUser( userinfo ) {
    const db = require('better-sqlite3')('./users.db');
    const lastID = db.prepare('SELECT id FROM users ORDER BY id DESC LIMIT 1').all();
    userinfo["id"] = parseInt(lastID[0].id) + 1;
    const addRequest = db.prepare('INSERT INTO users (id, username, firstname, lastname, email, badge_id) VALUES (@id,@username, @firstname, @lastname, @email, @badgeid)');
    addRequest.run(userinfo);
}

module.exports = router;
