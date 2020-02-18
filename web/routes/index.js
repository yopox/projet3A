const fs = require('fs');
const express = require('express');
const router = express.Router();
const WebSocket = require('ws');
const ip = require('ip');
// const wss = new WebSocket.Server({ port: 3001 });



/* GET home page. */
router.get('/', function(req, res, next) {
  // res.render('index', { title: 'NFC'});
  const uri = ip.address();
  fs.readFile(__dirname + '/index.html', 'utf-8', (err, content) => {
    const rendered = content.toString().replace('#URI#', uri);
    res.send(rendered)
  });
  // setTimeout(() => {
  //   wss.on('connection', function connection(ws) {
  //     ws.send('coucou');
  //   });
  // }, 1000)
});

module.exports = router;
