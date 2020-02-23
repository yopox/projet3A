const express = require('express');
const router = express.Router();
const WebSocket = require('ws');
const ip = require('ip');

/* GET home page. */
router.get('/', function (req, res, next) {
    // Get server IP to generate the Web Socket URI
    const uri = ip.address();
    // Run template
    res.render(__dirname + '/../templates/index.ejs', {URI: uri});

});

module.exports = router;
