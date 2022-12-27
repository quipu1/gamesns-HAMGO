<template>
  <div>
    <Header />
    <div id="main-content" class="start-container" style="padding-top: 45px;">
      <!--
      <div class="row">
        <textarea id="chat-content" v-model="textarea" rows="20" style="width:100%; height:50%;" readonly></textarea>
      </div>
      -->
      <div v-for="(user, idx) in matchedUser" :key="idx">
        <div class="match-line">
          <div class="match-fimg">
            <img :src="'http://localhost:8080/account/file/' + user.uid" />
          </div>
          <!-- 매너점수 -->
          <div class="matchmanner">
            <Manner :userInfo="user"/>
          </div>
          <div class="match-badge">
          <MatchingBadge :userInfo="user"/>
          </div>
          <!-- 체크 엑스 -->
          <div class="match-check">
          <div v-if="user.checked == true">
            <p style="color: #4169E1;"><i class="fas fa-check"></i></p>
          </div>
          <div v-else>
            <p><i class="fas fa-times"></i></p>
          </div>
          </div>
        </div>
      </div>

      <div class="mloading-container" v-if="matchedUser.length == 0">
        <div class="mloading"></div>
        <div id="mloading-text">matching</div>
      </div>

      <button class="matching-start-btn game-btn" id="btnJoin" style="">함고?</button>
      <div class="matching-accept-btn game-btn" v-if="matchedUser.length > 0" id="btnSend"><button style="color: white;">매칭수락</button></div>
      <!--<button class="matching-start-btn game-btn" id="btnSend"> 보내기 </button> <button   style="text-align: center;"></button>-->
    </div>
    <Footer />
  </div>
</template>

<script>
import Header from '@/components/layout/header/Header.vue';
import Footer from '@/components/layout/footer/Footer.vue';
import Stomp from 'webstomp-client';
import SockJS from 'sockjs-client';
import MatchingBadge from '@/components/user/MatchingBadge.vue';
import Manner from '@/components/user/myPage/Manner.vue';
import http from '@/util/http-common';

export default {
  name: 'MatchingStart',
  components: {
    Header,
    Footer,
    MatchingBadge,
    Manner,
  },
  data() {
    return {
      socket: null,
      stompClient: null,
      sessionId: null,
      chatRoomId: null,
      joinInterval: null,
      //message : "",
      matchingInfo: null,
      matchedUser: [],
      discordUrl: '',
    };
  },
  created() {
    this.matchingInfo = this.$route.params.data;
    if (this.matchingInfo == null) {
      alert('비정상적인 접근입니다.');
      this.$router.push('/matchingBefore');
    }
  },
  mounted() {
    const $joinEvent = document.querySelector('#btnJoin');
    $joinEvent.addEventListener('click', (e) => this.clickEvent(e));
    this.join();
  },
  updated() {
    const $sendEvent = document.querySelector('#btnSend');
    if ($sendEvent) $sendEvent.addEventListener('click', (e) => this.sendMessage(e));
  },
  methods: {
    clickEvent(e) {
      var type = e.target.innerText;
      if (type == '함고?') {
        this.join();
      } else if (type == '매칭중지') {
        //this.cancel();
        this.$router.push('/matchingBefore');
      }
    },
    join() {
      document.querySelector('#btnJoin').innerText = '매칭중지';
      //this.updateText('waiting anonymous user', false);
      this.joinInterval = setInterval(() => {
        //this.updateText('.', true);
      }, 1000);

      http
        .get('/matching/join', {
          params: {
            gameName: this.matchingInfo.selectedGame,
            peopleLimit: this.matchingInfo.selectedPeople,
            discordId: this.matchingInfo.discordId,
            uid: this.$store.state.uid,
          },
        })
        .then((matchingResponse) => {
          console.log('Success to receive join result.');

          clearInterval(this.joinInterval);
          if (matchingResponse.data.responseResult == 'SUCCESS') {
            this.sessionId = matchingResponse.data.sessionId;
            this.chatRoomId = matchingResponse.data.chatRoomId;
            this.discordUrl = matchingResponse.data.discordUrl;

            let temp = matchingResponse.data.matchedUser;

            temp.forEach((element) => {
              element.checked = false;
              this.matchedUser.push(element);
              
            });

            //this.updateText('>> Connect anonymous user :)', false);
            this.connectAndSubscribe();
          } else if (matchingResponse.data.responseResult == 'CANCEL') {
            //this.updateText('>> Success to cancel', false);
            document.querySelector('#btnJoin').innerText = '함고?';
            this.$router.push('/matchingBefore');
          } else if (matchingResponse.data.responseResult == 'TIMEOUT') {
            //this.updateText('>> Can`t find user :(', false);
            document.querySelector('#btnJoin').innerText = '함고?';
            alert("매칭 시간초과입니다.")
            this.$router.push('/matchingBefore');
          }
        })
        .catch((jqxhr) => {
          clearInterval(this.joinInterval);

          // if (jqxhr.status == 503) {
          //   //this.updateText('>>> Failed to connect some user :( Plz try again', true);
          // } else {
          //   //this.updateText(jqxhr, true);
          // }
        });
    },
    cancel() {
      //매칭 멈추기
      this.matchedUser = [];
      http
        .get('/matching/cancel', {
          params: {
            gameName: this.matchingInfo.selectedGame,
            peopleLimit: this.matchingInfo.selectedPeople,
            discordId: this.matchingInfo.discordId,
            uid: this.$store.state.uid,
          },
        })
        .then(() => {
          //this.updateText('', false);
          document.querySelector('#btnJoin').innerText = '함고?';
        })
        .catch((jqxhr) => {
          console.log('Error occur. please refresh');
        });
      clearInterval(this.joinInterval);
    },
    connectAndSubscribe() {
      if (this.stompClient == null || !this.stompClient.connected) {
        const serverURL = 'http://localhost:8080/matching';
        var socket = new SockJS(serverURL);
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({ matchingRoomId: this.chatRoomId }, (frame) => {
          console.log('Connected: ' + frame);
          this.subscribeMessage();
        });
      } else {
        this.subscribeMessage();
      }
    },
    disconnect() {
      if (this.stompClient !== null) {
        this.stompClient.disconnect();
        this.stompClient = null;
      }
    },
    sendMessage() {
      console.log('Check.. >> ', this.stompClient);
      console.log('send message.. >> ');
      //var $chatTarget = document.querySelector('#chat-message-input');
      //var message = $chatTarget.val();
      //$chatTarget.val('');

      var matchingMessage = {
        messageType: 'CHAT',
        senderSessionId: this.sessionId,
        message: this.$store.state.uid,
      };

      this.stompClient.send(
        '/chat.message/' + this.chatRoomId,
        JSON.stringify(matchingMessage),
        {}
      );
    },
    subscribeMessage() {
      this.stompClient.subscribe('/topic/chat/' + this.chatRoomId, (resultObj) => {
        console.log('>> success to receive message\n', resultObj.body);
        var result = JSON.parse(resultObj.body);

        if (result.messageType == 'CHAT') {
          // if (result.senderSessionId === this.sessionId) {//여기다가 로직
          //   message += '[Me] : ';
          // } else {
          //   message += '[Anonymous] : ';
          // }
          // message += result.message + '\n';
        } else if (result.messageType == 'DISCONNECTED') {
          alert('매칭이 거절되었습니다.');
          this.$router.push('/matchingBefore');
          //message = '>> Disconnected user :(';
          this.disconnect();
        }
        this.checkUserAccept(result.message);
      });
    },
    checkUserAccept(message) {
      this.matchedUser.forEach((element) => {
        if (element.uid == message) element.checked = true;
      });

      let flag = true;
      this.matchedUser.forEach((element) => {
        if (!element.checked) {
          flag = false;
          return false;
        }
      });

      if (flag) {
        this.$router.push({
          name: 'MatchingResult',
          params: { matchedUser: this.matchedUser, discordUrl: this.discordUrl },
        });
      }
    },
    // updateText(message,append){
    //   if (append) {
    //     this.textarea += message;
    //   } else {
    //     this.textarea = message;
    //   }
    // },
    // updateTemplate(type){
    //   var source;
    //   if (type == 'wait') {
    //     source = document.querySelector('#wait-chat-template').innerHTML;
    //   } else if (type == 'chat') {
    //     source = document.querySelector('#send-chat-template').innerHTML;
    //   } else {
    //     console.log('invalid type : ' + type);
    //     return;
    //   }
    //   //var template = Handlebars.compile(source);
    //   //var $target = document.querySelector('#chat-action-div');
    //   //$target.empty();
    //   //$target.append(template({}));
    // },
  },
  beforeDestroy() {
    //this.cancel();
    clearInterval(this.joinInterval);
    this.disconnect();
  },
};
</script>
<style>
@import '../../../components/css/home/matching/matchingStart.css';
@import '../../../components/css/home/matching/start.scss';
@import '../../../components/css/user/login.css';
@import '../../../components/css/user/loading.css';
</style>
