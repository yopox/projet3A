const express = require('express')
const app = express()
const port = 3000
var nfc = require("explorenfc");
nfc.init("/usr/bin/explorenfc-basic");

app.get('/', function(req, res) {
	var valTab = nfc.read(function(nfcEvent){
		if(nfcEvent){
			console.log("id", nfcEvent.id);
			console.log("value", nfcEvent.value);
			res.send('Badge id %s detected',nfcEvent.id);
			res.send('Value is %s',nfcEvent.value);
		}else{
			console.log("no NFC Event");
			res.send('No Badge Detected');
		}
	});
});

var server = app.listen(port, function() {
	var host = server.address().address;
	console.log('Example app listening at http://%s:%s',host,port);
});
