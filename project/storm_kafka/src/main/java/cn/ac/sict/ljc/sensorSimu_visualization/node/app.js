
var express = require('express')
  , app = express()
  , server = require('http').Server(app)
  , io = require('socket.io')(server)
  , redis = require("redis");

// 静态文件
app.use(express.static('lib'));

// http server
server.listen(80, function(){
  console.log('Server running at http://127.0.0.1:80/');
});

// 路由
app.get('/', function (req, res) {
  res.sendFile(__dirname + '/index.html');
});

// redis io
var redisClient = redis.createClient({host: "192.168.125.171", port: 6378});
redisClient.auth("yourpassword");
redisClient.subscribe("ljc_sensor_temper");
redisClient.subscribe("ljc_sensor_pressure");

// var pub = redis.createClient({host: "192.168.125.171", port: 6378});
// pub.auth("yourpassword");
// pub.publish("msg", "I am sending a message.");

redisClient.on("error", function (err) {
  console.log("Error " + err);
});

redisClient.on("message", function (channel, message) {
  message = JSON.parse(message);
  console.log("channel: " + channel + "  message: " + message);
  console.log({ time: parseInt(message.time / 1000 / 1000), value: parseFloat(message.value) });
  io.emit(channel, { time: parseInt(message.time / 1000 / 1000), value: parseFloat(message.value) }); // 向所有链接发信息
});

// socket io
// io.on('connection', function (socket) {
//   socket.emit('news', { hello: 'world' }); // 向此 socket 发信息
//   socket.on('my other event', function (data) {
//     console.log(data);
//   });
// });
