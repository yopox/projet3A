var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
const bodyParser  = require('body-parser');

let spawn = require("child_process").spawn;
console.log("starting temporary server !");
//pythonProcess = spawn('python3', ["./fake_NFC_badge.py"]);
pythonProcess = spawn('python3',["./poll_NFC_badge.py"] );
pythonProcess.stdout.on('data', function (data) {
    //console.log(data.toString());
});

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var classRouter = require('./routes/class');
var presenceRouter = require('./routes/presence');



var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/class', classRouter);
app.use('/presence', presenceRouter);



// catch 404 and forward to error handler
app.use(function (req, res, next) {
    next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render(__dirname + '/templates/error.ejs');
});

module.exports = app;
