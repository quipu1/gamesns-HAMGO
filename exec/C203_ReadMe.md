# 1. 작업문서

#### 

### 1) prerequisites

##### version

- JVM : openjdk 1.8.0_192

- Tomcat : apache-tomcat-9.0.43

- Spring Tool Suite : 3.9.14.RELEASE

- VS code : 1.59



### 2) environment variables

- **kakao API**

  경로 :  \frontend\ .env.local

  ```
  VUE_APP_KAKAO_ID=d9244e59520303e14f40a6a210aae21d
  VUE_APP_KAKAO_URI=https://i5c203.p.ssafy.io/user/loading
  ```

  VUE_APP_KAKAO_ID : 카카오에서 제공한 REST api key

  VUE_APP_KAKAO_URI : 인증 완료 후 redirect 될 uri

- **Discord** 

  경로 : (backend\src\main\resources\application.properties)

  ```
  jda.discord.token=ODcyMjk5MzEzODEwNzY3OTMz.YQn2Cg.laaG7eejNueUdmlrwGpB9hisyi0
  jda.discord.guild.id=865502741388722216
  jda.discord.guild.category.id=865502741388722220
  ```

  jda.discord.token : 디스코드 봇과 통신하기 위한 토큰

  jda.discord.guild.id : 디스코드 서버 고유아이디 

  jda.discord.guild.category.id : 디스코드 서버 내 카테고리 아이디



### 3) 배포 시 특이사항 기재

- **nginx 설정** 

​	경로 : /etc/nginx/sites-enabled/default

```
		location /chat {
                proxy_pass http://localhost:3001;
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection "upgrade";
                proxy_http_version 1.1;
                proxy_set_header Host $host;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header X-Forwarded-Proto $scheme;
                proxy_set_header X-Forwarded-Host $host;
                proxy_set_header X-Forwarded-Port $server_port;
        }

        location /api {
                proxy_pass http://localhost:8080;
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection "upgrade";
                proxy_http_version 1.1;
                proxy_set_header Host $host;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header X-Forwarded-Proto $scheme;
                proxy_set_header X-Forwarded-Host $host;
                proxy_set_header X-Forwarded-Port $server_port;
        }
```

Socket IO와 Backend 서버와의 통신을 위한 Nginx 설정 필수



- **채팅서버 설정**

  경로 : /backend/chat/server.js

```
const fs = require('fs')
const options = {
    cert: fs.readFileSync('/etc/letsencrypt/live/i5c203.p.ssafy.io/fullchain.pem'),
    key: fs.readFileSync('/etc/letsencrypt/live/i5c203.p.ssafy.io/privkey.pem')
};

var app = require("express")();
var server = require("https").createServer(options, app);
var io = require("socket.io")(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
      }
});
```

certificate file, private key 경로 지정

모든 경로에 cors 허용

- 채팅 서버 실행 시 
   1. chat 폴더로 이동
   2. su nodeuser 로 사용자 변경
   3. node server.js 로 채팅 서버 실행


### 4) db 접속정보

```
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://i5c203.p.ssafy.io:3306/hamgo?useUniCode=yes&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
spring.datasource.username=root
spring.datasource.password=hamgo!234
```

username : root

password : hamgo!234

DB 명 : hamgo



# 2. 외부 서비스 정보

1) 카카오 API

​	kakao developers > '내 어플리케이션' > 카카오 로그인 > Redirect URI 설정



```
https://localhost:8081/user/loading
https://i5c203.p.ssafy.io/user/loading
```



kakao developers 에 hamgo 등록(도메인 등록)

'내 어플리케이션' > 카카오 로그인 에 인증을 완료한 다음 넘어갈 URI 를 Redirect URI 에 설정



2) JDA (Java Discord API)

​	디스코드 서버 생성, 채널 생성, 멤버 권한 부여 등의 작업을 수행할 수 있는 API





# 3. 데이터베이스 덤프 파일 최신본

데이터베이스 : hamgo 

 [광주2반_3팀_SQL.sql](..\..\Downloads\광주2반_3팀_SQL.sql) 



# 4.시연 시나리오

1. 첫 화면
   1 - 1. 로그인

   - 카카오로 로그인 버튼을 누른다.
   - 카카오톡 계정으로 로그인을 한다. (2단계 인증)

   1 - 2. 로딩 

   * 로딩화면을 기다린다.

   1 - 3. 회원가입 

   * 첫 로그인시 닉네임을 통해 회원가입을 한다.
   * 닉네임 입력 후 중복확인을 한다.
   * 중복이 아닐 경우 start 버튼을 누른다.
   * 메인 페이지로 이동한다.

2. 피드

   2 - 1. 글작성자

   * 피드 상단의 `닉네임`을 클릭한다.
   * 글작성자의 마이페이지로 이동한다.

   2 - 2. 글 삭제

   * 해당 피드의 글작성자일 경우 우상단의 `...` 버튼이 보인다.

   * `...` 버튼을 누른다.
   * `삭제` 버튼을 누른다.

   2 - 3. 자세히 보기

   * `자세히 보기` 클릭 시, 피드의 세부사항이 담긴 모달 피드로 이동한다.

   2 - 4. 좋아요

   2 - 5. 공유

   * 피드 우하단의 `공유 버튼`을 누른다.
   * 카카오톡을 통해 해당 피드를 공유한다.

   2 - 6. 해시태그

   * 해시태그 클릭 시,

3. 모달 피드

   * 피드의 `자세히보기` 또는 `댓글 아이콘`을 누를 시 모달창이 보인다.

   3 - 1. 댓글

4. 글 작성

   * 우하단 푸터의 `글작성 아이콘`을 클릭한다.
   * 글작성 페이지로 이동한다.

   4 - 1. 사진

   * `파일 업로드` 버튼을 클릭해 사진을 선택한다.
   * 선택된 사진은 버튼 아래의 미리보기로 보여진다.
   * 미리보기의 x버튼을 눌러 사진을 재선택한다.

   4 - 2. 해시태그

   * `#` 작성 후, 자유롭게 키워드를 작성한다.
   * 하나의 키워드를 작성 후 띄어쓰기를 통해 해시태그를 구분한다. 
   * 단, 하나의 키워드는 띄어쓰지않는다. (예시 - 가능: #함고 #하암고 / 불가능: #함고 함고 함고)

5. 마이 페이지

   5 - 1. 프로필 편집

   * `프로필 편집 버튼`을 누른다.
   * 사진, 닉네임을 수정한다.

   5 - 2. 팔로우 팔로잉 리스트

   * 팔로워와 팔로잉수가 나타난다.
   * `팔로워`, `팔로잉`을 누를 시, 각 팔로우 리스트를 보여준다.
   * 리스트에서 닉네임을 누를 시, 해당 유저의 마이페이지로 이동한다.
   * 우측의 버튼을 통해 팔로워 또는 팔로잉을 해제한다.

   5 - 3. 매너점수

   * 회원가입 시, 기본 200점의 매너점수를 갖는다.
   * 매칭 후의 매너평가를 통해 바뀐 점수가 나타난다.

   5 - 4. 내 피드

   * 하단의 `상자 아이콘`을 누를 시, 내가 쓴 피드를 보여준다.

   5 - 5. 뱃지

   * `얼굴 아이콘`을 누를 시, 내가 얻은 뱃지를 보여준다.
   * 매너점수, 작성한 글, 댓글 수 등을 기준으로 뱃지를 획득한다.

6. 알림

   * 팔로우 요청에 대한 알림을 받는다.
   * 팔로우 요청을 수락 또는 거절한다.

7. 검색(검색 자동완성, 최근 검색)
   7 - 1. 유저 검색

   * 닉네임을 통해 유저를 검색한다.
   * 검색을 통해 나온 닉네임을 클릭 시, 해당 유저의 마이페이지로 이동한다.

   7 - 2. 해시태그 검색

   * `#키워드` 검색 시, 해당 해시태그를 입력한 피드들이 보여진다.

   7 - 3. 최근 검색

   * 최근 검색한 목록이 보여진다.

8. 매칭

   8 - 1. 디스코드 서버 접속 확인 페이지

   * `함고 서버링크 버튼`을 통해 디스코드 서버에 접속한다.
   * 해시태그를 포함한 디스코드 아이디를 입력 후 `접속 확인 버튼`을 통해 접속 상태를 확인한다.
   * 접속이 확인 되면 다음 페이지로 이동한다.

   8 - 2. 게임, 인원 선택 페이지

   * 매칭을 원하는 게임과 매칭할 인원을 선택한다.

   8 - 3. 매칭 로딩 페이지

   * 매칭이 되기 전까지 로딩 페이지를 보여준다.
   * `매칭 중지`를 누를 시, 매칭 페이지의 첫 화면을 보여준다.

   8 - 4. 매칭 완료 후 수락 대기

   * 매칭된 유저들의 프로필 사진, 매너점수, 뱃지를 보여준다.
   * 제공된 정보를 통해 매칭을 수락할지 결정한다.
   * `매칭 수락`을 누르면 매칭 유저 목록 우측의 `x`가 `✔`로 바뀐다.
   * 모든 유저의 수락 후 최종 매칭이 된다.

   8 - 5. 완료 페이지 및 팀원 평가 페이지

   * 최종 매칭 결정 후 함고에서 매칭된 유저들만을 위한 `비밀 음성 채널`이 생성된다.
   * 제공된 `디스코드 링크`를 통해 자신의  `비밀 음성 채널`에 들어간다.
   * 게임 후 함께 한 유저에 대한 매너점수를 평가한다.

9. 채팅

   9 - 1. 채팅방 목록

   * 현재 나에게 채팅을 한 유저들의 채팅방 목록이 보여진다.

   9 - 2. 채팅방

   * 우상단의 검색을 통해 유저를 찾아 채팅방을 생성한다.
   * 채팅방에 들어가 해당 유저와 채팅한다.
