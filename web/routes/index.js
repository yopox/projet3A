const fs = require('fs');
const express = require('express');
const router = express.Router();
const WebSocket = require('ws');
const ip = require('ip');
// const wss = new WebSocket.Server({ port: 3001 });



/* GET home page. */
router.get('/', function(req, res, next) {
  // Get server IP to generate the Web Socket URI
  const uri = ip.address();
  // Run template
  fs.readFile(__dirname + '/index.html', 'utf-8', (err, content) => {
    const rendered = content.toString().replace('#URI#', uri);
    res.send(rendered);
  });
});

module.exports = router;
