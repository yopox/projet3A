const express = require('express');
const router = express.Router();
const ip = require('ip');
let pythonProcess = 0;

function getDatabase() {
    const db = require('better-sqlite3')('./users.db');
    let classes = [];
    const rows = db.prepare('SELECT * FROM classes').all();
    for (row of rows) {
        let arrayRow = {
            classid: row.classid,
            entry: row.studentsOnEntry ? row.studentsOnEntry.slice(2, -2).split("', '") : [],
            exit: row.studentsOnExit ? row.studentsOnExit.slice(2, -2).split("', '") : []
        };
        classes.push(arrayRow);
    }
    db.close();
    return classes;
}

/* GET users listing. */
router.get('/', function (req, res, next) {

    const uri = ip.address();
    res.locals.classes = getDatabase();
    res.render(__dirname + '/../templates/presence.ejs', {URI: uri});
});

module.exports = router;
