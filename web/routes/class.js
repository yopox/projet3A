const express = require('express');
const router = express.Router();
const ip = require('ip');
let pythonProcess = 0;

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

function getDatabaseFilter(promotions,diplomas,number,offset) {
    const db = require('better-sqlite3')('./users.db');
    let users = [];

    // Building the filter string
    let request = 'SELECT * FROM users ';
    // Let's make sure there are promotions and diplomas !
    if (promotions.length > 0 || diplomas.length > 0) request += 'WHERE ';

    // This is the parameter list
    let params = [];
    // Add the "promotion LIKE ? OR promotion LIKE ?" for each diploma
    // Also add the parameter for each
    if (promotions.length > 0) {
        request += 'promotion LIKE ? ';
        params.push("%" + promotions[0] + "%");
        for (let i = 0; i < promotions.length -1 ; i++) {
            request += 'OR promotion LIKE ? ';
            params.push("%" + promotions[i + 1] + "%");
        }
    }

    // Add the "AND supann_diplome LIKE ? OR supann_diplome LIKE ?" for each diploma
    // Also add the parameter for each
    if (diplomas.length > 0) {
        if (promotions.length > 0) request += 'AND ';
        request +='supann_diplome LIKE ? ';
        params.push("%" + diplomas[0] + "%");

        for (let i = 0; i < diplomas.length -1 ; i++) {
            request += 'OR supann_diplome LIKE ? ';
            params.push("%" + diplomas[i + 1] + "%");
        }
    }
    request += 'ORDER BY lastname ASC LIMIT ? OFFSET ?';
    console.log(request);
    // Let's add number and offset as SQL parameters
    params.push(number);
    params.push(offset);
    console.log(params);
    // this is our final query
    const rows = db.prepare(request).all(params);
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
        // If it is a list, we return each and every element once
        if (row.promotion[0] === "[") {
            let arrayRow = row.promotion.slice(2, -2).split("', '");
            for (let element of arrayRow) {
                if (!promotions.includes(element)) {
                    promotions.push(element);
                }
            }
        }
        // If it isn't a list, only return one element
        else {
            let element = row.promotion;
            if (!promotions.includes(element)) {
                promotions.push(element);
            }
        }

    }
    db.close();
    return promotions.sort();
}

function getSupAnn() {
    const db = require('better-sqlite3')('./users.db');
    let diplomes = [];
    const rows = db.prepare('SELECT DISTINCT supann_diplome FROM users ORDER BY supann_diplome ASC').all();
    for (row of rows) {
        // If it is a list, we return each and every element once
        if (row.supann_diplome[0] === "[") {
            let arrayRow = row.supann_diplome.slice(2, -2).split("', '");
            for (let element of arrayRow) {
                if (!diplomes.includes(element)) {
                    diplomes.push(element);
                }
            }
        }
        // If it isn't a list, only return one element
        else {
            let element = row.supann_diplome;
            if (!diplomes.includes(element)) {
                diplomes.push(element);
            }
        }
    }
    db.close();
    return diplomes.sort();
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
    // Grab the info from the selector of the HTML
    let promotions = req.body.selectPromotion;
    let diplomas = req.body.selectDiplome;

    // If they are string, let's create lists
    if (typeof promotions === "string") {
        promotions = [promotions];
    }
    //If they are empty, let's create empty lists
    else if (typeof promotions === "undefined") {
        promotions = [];
    }
    // If they are string, let's create lists
    if (typeof diplomas === "string") {
        diplomas = [diplomas];
    }
    //If they are empty, let's create empty lists
    else if (typeof diplomas === "undefined") {
        diplomas = [];
    }

    // And finally let's return our user filtering them first
    const uri = ip.address();
    res.locals.users = getDatabaseFilter(promotions,diplomas,100,0);
    res.locals.promotions = getPromotions();
    res.locals.diplomes = getSupAnn();
    res.render(__dirname + '/../templates/class.ejs', {URI: uri});
});


function addUser( userinfo ) {
    const db = require('better-sqlite3')('./users.db');
    const lastID = db.prepare('SELECT id FROM users ORDER BY id DESC LIMIT 1').all();
    userinfo["id"] = parseInt(lastID[0].id) + 1;
    const addRequest = db.prepare('INSERT INTO users (id, username, firstname, lastname, email, badge_id) VALUES (@id,@username, @firstname, @lastname, @email, @badgeid)');
    addRequest.run(userinfo);
}

module.exports = router;
