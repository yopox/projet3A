const express = require('express');
const router = express.Router();
const ip = require('ip');
let pythonProcess = 0;

function getDatabase(number,offset) {
    const db = require('better-sqlite3')('./users.db');
    let users = [];
    const rows = db.prepare('SELECT * FROM users WHERE promotion LIKE ? ORDER BY lastname ASC LIMIT ? OFFSET ?').all("%{BASEELEVES}ENE%",number,offset);
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

/* GET users listing. */
router.get('/', function (req, res, next) {
    const uri = ip.address();
    res.locals.users = getDatabase(100,0);
    res.locals.promotions = getPromotions();
    res.render(__dirname + '/../templates/class.ejs', {URI: uri});
});

module.exports = router;
