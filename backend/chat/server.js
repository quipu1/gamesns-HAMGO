// const { copyFileSync } = require("fs");
// const { type } = require("os");
// const { join } = require("path");

// const fs = require('fs')
// const options = {
//     cert: fs.readFileSync('/etc/letsencrypt/live/i5c203.p.ssafy.io/fullchain.pem'),
//     key: fs.readFileSync('/etc/letsencrypt/live/i5c203.p.ssafy.io/privkey.pem')
// };

var app = require("express")();
var server = require("http").createServer(app);
var io = require("socket.io")(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
      }
});

// user 들의 socket.id
var users = {};
// user 들의 room list
// { '조용일' : [(내 socketid, 상대방1 id), (내 )   }
// '조용일' : ['pms', '조성표'], 'pms' : ['조용일'], '조성표 : '['조용일']
var rooms = {};
// 방마다 들어와 있는 유저 수
var join_num = {};


//setting cors
app.all("/*", function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  next();
});
app.get("/", function (req, res) {
  res.sendFile("Hellow Chating App Server");
}); 

//connection event handler 
io.on('connection' , function(socket) { 
    
    // users update
    socket.on('updateUser', function(data) {
        if(rooms[data.nickname] == undefined){
            rooms[data.nickname] = [];
        } 
        users[data.nickname] = data.id;
    })

    socket.on('callRooms', function(data) {
        io.to(users[data]).emit('getRooms', rooms[data]);
    })

    // Message 보내기
    socket.on('sendMsg', function(data) {
        
        /*
        data = {
            id : socket.id,
            myNickname : this.nickname,
            yourNickname : suggest.nickname,
            msg : this.msg,
            type : 1        // 0 : 입장, 1 : 퇴장, 2 : 그냥 메세지 전송
        }
        */

        if(rooms[data.yourNickname] == undefined) rooms[data.yourNickname] = [];
        if(rooms[data.myNickname] == undefined) rooms[data.myNickname] = [];
        if(join_num[data.yourNickname] == undefined) join_num[data.yourNickname] = [];
        if(join_num[data.myNickname] == undefined) join_num[data.myNickname] = [];

        if(data.type == 2) {
            // 둘 다 채팅방에 들어온 것이므로 그냥 메세지 전송 

            let msg = {
                sender : data.myNickname,
                msg : data.msg,
                time : data.time,
            }
            io.to(users[data.yourNickname]).emit('recvMsg', msg);
        } else if(data.type == 0) {
            // 입장
            /* 생각해야 될 것
            
            1. 내가 먼저 채팅방에 들어갈 때
            2. 내가 나중에 채팅방에 들어갈 때
            3. 누군가와 대화 중일 때 다른 사람이 나와의 채팅방을 만들었을 때 
            
            */

            if(join_num[data.yourNickname][0] != data.myNickname){
                // 3번의 경우(내가 채팅하려는 사람이 다른 사람과의 채팅방에 있을 때)

                join_num[data.myNickname][0] = data.yourNickname;
                join_num[data.myNickname][1] = 1;

            }

            let msg = {
                sender : data.myNickname,
                msg : data.myNickname + "님이 입장하셨습니다." + "\n",
                type : 0
            }
            // 입장하는 순간 상대방에게 메세지 보내기
            io.to(users[data.yourNickname]).emit('recvMsg', msg);

            var flag = 0;
            for(var i = 0 ; i < rooms[data.myNickname].length ; i++){
                if(rooms[data.myNickname][i] == data.yourNickname){
                    flag = 1;
                    break;
                }
            }
            
            var flag2 = 0;
            for(var i = 0 ; i < rooms[data.yourNickname].length ; i++){
                if(rooms[data.yourNickname][i] == data.myNickname){
                    flag2 = 1;
                    break;
                }
            }
            
            // 내가 처음에 입장한 사람일 경우에 입장 : flag = 0 && flag = 0
            if(flag == 0 && flag2 == 0) {
                // rooms 에 데이터 추가
                rooms[data.myNickname].push(data.yourNickname);
                rooms[data.yourNickname].push(data.myNickname);

                // join_num 에 데이터 추가
                join_num[data.myNickname] = [data.yourNickname,1];

                // 데이터가 추가된 rooms 보내기 
                io.to(users[data.myNickname]).emit('getRooms', rooms[data.myNickname]);
                io.to(users[data.yourNickname]).emit('getRooms', rooms[data.yourNickname]);

            } else {
                // 방에 한명이라도 있는 경우에 입장
                if(join_num[data.yourNickname][0] == data.myNickname && join_num[data.yourNickname][1] == 1) {
                    join_num[data.yourNickname][1] = 2;
                    join_num[data.myNickname][0] = data.yourNickname;
                    join_num[data.myNickname][1] = 2;

                    // 바뀐 join_num 수를 보내줘야 한다.
                    io.to(users[data.myNickname]).emit('getJoinNum', 2);
                    io.to(users[data.yourNickname]).emit('getJoinNum', 2);
                }
            }

        } else if(data.type == 1) {
            // 퇴장
            
            // 생각해야 될 것
            /*
            
            1. 다른 두 사용자가 채팅 중에 
            어떤 사용자가 그 두 명 중 한 명과의 채팅방을 만들고 입장하고 바로 퇴장한다면
            기존의 두 사용자가 사용 중이던 채팅방은 유지가 되야한다.
            
            */

           if(join_num[data.yourNickname][0] != data.myNickname) {
                // 1번의 경우
                // 나 혼자 입장하고 퇴장했을 때(다른 사람은 채팅방에 들어오지 않음) 
                
                join_num[data.myNickname][0] = '';
                join_num[data.myNickname][1] = 0;
                
                // 퇴장하는 순간 join_num 을 다시 보내준다.
                io.to(users[data.myNickname]).emit('getJoinNum', 0);

                // rooms 에서 방을 지워야 한다.
                for(let i = 0; i < rooms[data.yourNickname].length; i++) {
                    if(rooms[data.yourNickname][i] == data.myNickname)  {
                        rooms[data.yourNickname].splice(i, 1);
                        i--;
                    }
                }

                for(let i = 0; i < rooms[data.myNickname].length; i++) {
                    if(rooms[data.myNickname][i] == data.yourNickname)  {
                        rooms[data.myNickname].splice(i, 1);
                        i--;
                    }
                }

                // 바뀐 rooms 데이터를 보내줘야한다.
                io.to(users[data.myNickname]).emit('getRooms', rooms[data.myNickname]);
                io.to(users[data.yourNickname]).emit('getRooms', rooms[data.yourNickname]);
            } else {
                // 퇴장하는 순간 join_num 을 다시 보내준다.
                io.to(users[data.myNickname]).emit('getJoinNum', 1);
                io.to(users[data.yourNickname]).emit('getJoinNum', 1);
    
                let msg = {
                    sender : data.myNickname,
                    msg : data.myNickname + "님이 퇴장하셨습니다." + "\n",
                    type : 1
                }
                // 상대방에게 퇴장 메세지 보내기
                io.to(users[data.yourNickname]).emit('recvMsg', msg);
    
                // join_num 을 최신화
                join_num[data.myNickname][0] = '';
                join_num[data.myNickname][1]--;
    
                if(join_num[data.myNickname][1] == 1) {
                    // join_num[data.myNickname][1] == 1 이라면 내가 먼저 나온 경우
                    // join_num 의 yourNickname 의 인원 수도 1 줄여줌
                    join_num[data.yourNickname][1]--;
                } else if(join_num[data.myNickname][1] == 0) {
                    // join_num[data.myNickname][1] == 0 이라면 내가 마지막에 나온 경우
    
                    // rooms 에서 방을 지워야 한다.
                    for(let i = 0; i < rooms[data.yourNickname].length; i++) {
                        if(rooms[data.yourNickname][i] == data.myNickname)  {
                            rooms[data.yourNickname].splice(i, 1);
                            i--;
                        }
                    }
    
                    for(let i = 0; i < rooms[data.myNickname].length; i++) {
                        if(rooms[data.myNickname][i] == data.yourNickname)  {
                            rooms[data.myNickname].splice(i, 1);
                            i--;
                        }
                    }
    
                    // 바뀐 rooms 데이터를 보내줘야한다.
                    io.to(users[data.myNickname]).emit('getRooms', rooms[data.myNickname]);
                    io.to(users[data.yourNickname]).emit('getRooms', rooms[data.yourNickname]);
    
                }

            }


        }    
    })

}) 
        
server.listen(3001, function() { 
    console.log('socket io server listening on port 3001') 
})

/*
front
받는 코드는 on, 보내는 코드는 emit
서버측에서 이벤트를 보낼 때는 io.sockets.emit("이벤트 명", data)
서버측에서 이벤트를 받을 때는 socket.on("이벤트 명", function(data){ })
클라이언트측에서 이벤트를 보낼때는 socket.emit("이벤트 명", data)
클라이언트측에서 이벤트를 받을떄는 socket.on("이벤트 명", function(data){ })

// 1. front에서 보낸다
socket.emit('sendMsg', data)
data = {
    id : socket.id,
    myNickname : this.nickname,
    yourNickname : suggest.nickname,
    msg : this.msg
}

// 2. back 에서 받는다.
socket.on('sendMsg', function(data) {
    
    // 2-1. users 의 socketid 를 최신화한다.
    users[data.myNickname] = data.id;

    // 3. back 에서 특정인에게 보낸다.
    io.to(users[data.yourNickname]).emit('recvMsg', data.msg);
})

// 4. front 에서 받는다.
socket.on('recmsg', function(data) {
    // 받은 데이터를 textarea 에 더해준다.
    this.textarea += data;
})


*/