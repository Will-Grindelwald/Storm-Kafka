var app = require('http').createServer(handler),
    io = require('socket.io').listen(app), //require('socket.io').listen(80);
    fs = require('fs'),
    url = require('url');

app.listen(99);


/**
 * init redis client
 */
var redis = require('redis');
var redisClient = redis.createClient("//192.168.125.171:6379");

redisClient.subscribe('visData');




function handler(request, response) {
    console.log('handler');

    var pathname = url.parse(request.url).pathname;
    var ext = pathname.match(/(\.[^.]+|)$/)[0]; //取得后缀名
    switch (ext) {
        case ".css":
        case ".js":
            fs.readFile("." + request.url, 'utf-8', function(err, data) { //读取内容
                if (err) throw err;
                response.writeHead(200, {
                    "Content-Type": {
                        ".css": "text/css",
                        ".js": "application/javascript",
                    }[ext]
                });
                response.write(data);
                response.end();
            });
            break;
        default:
            fs.readFile(__dirname + '/test5.html',
                function(err, data) {
                    if (err) {
                        response.writeHead(500);
                        return response.end('Error loading index.html');
                    }

                    response.writeHead(200);
                    response.end(data);
                });

    }



}
var sum1 = 0;
var sum2 = 0;
var count = 0;
var datetime = ((new Date()).getTime());
// Manage connections
io.sockets.on('connection', function(socket) {
    console.log('handle connection');
    var num = 0;


    /**
     * Handle "disconnect" events.
     */
    var handleDisconnect = function() {
        console.log('handle disconnect');
    };

    /**
     * pub redis channel
     */
    redisClient.on('message', function(channel, message) {
        var json = JSON.parse(message);
        console.log(json.number1 + "  " + json.number2);
        /*
         *采用本地时间，间隔1s,忽略storm输出乱序
         */
        sum1 += json.number1;
        sum2 += json.number2;
        count++;
        if (((new Date()).getTime()) - datetime > 1000) {
            socket.emit('server', {
                time: datetime,
                y1: sum1 / count,
                y2: sum2 / count

            });
            sum1 = 0;
            sum2 = 0;
            count = 0;
            datetime = ((new Date()).getTime());
        }

        /*
         * 根据数据携带的时间戳显示，但是因为storm的乱序输出，尤其是在每秒处理数据量大的情况下可能会出现错误
         *
         */
        // console. log('reciv message:' + message);
        //  var json = JSON.parse(message);
        //  console.log(json.time-datetime);
        // if((json.time-datetime)<1000){
        //         sum+=json.number1;
        //         count++;
        //  }
        //     else{
        //          var value=sum/count;
        //          count=0;
        //         sum=0;
        //         datetime=json.time;
        //          socket.emit('server', {
        //     time: json.time,
        //     y1: value
        //     //y2:json.number2
        // });





    });
    socket.on('disconnect', handleDisconnect);

});
