const express = require('express');
const router = express.Router();
const ip = require('ip');
let pythonProcess = 0;
const {body, validationResult} = require('express-validator');
const uri = ip.address();


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
    res.redirect('/users');
});
// router.post('/add-user/', [
//     body("add-user-form-username", "Empty Username").trim().isLength({min: 1}),
//     body("add-user-form-firstname", "Empty First Name").trim().isLength({min: 1}),
//     body("add-user-form-lastname", "Empty Last Name").trim().isLength({min: 1}),
//     body("add-user-form-email", "Email Error").trim().isEmail(),
//     body("add-user-form-badge-id", "Empty Badge ID").trim().isLength({min: 1})
// ], (req, res) => {
//     const errors = validationResult(req);
//     if (!errors.isEmpty()) {
//         return res.status(422).json({errors: errors.array()});
//     }
//     let user = ({
//         username: req.body.add-user-form-username,
//         firstname: req.body.add-user-form-firstname,
//         lastname: req.body.add-user-form-lastname,
//         email: req.body.add-user-form-email,
//         badgeid: req.body.add-user-form-badge_id
//     });
//     console.log(user);
//     res.render(__dirname + '/../templates/users.ejs', {URI: uri});
// });
//
// function createUser( userinfo ) {
// }

module.exports = router;
