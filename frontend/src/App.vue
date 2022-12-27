<template>
  <div id="app">
    <router-view :stompClient="stompClient" :recvList="recvList"></router-view>
  </div>
</template>
<script>
import './components/css/style.scss';
import Stomp from 'webstomp-client';
import SockJS from 'sockjs-client';

export default {
  name: 'app',
  data() {
    return {
      first: true,
      recvList: null,
    };
  },
  created() {
    this.connect();
  },
  methods: {
    connect() {
      const serverURL = 'http://localhost:8080/alarm';
      let socket = new SockJS(serverURL);
      this.stompClient = Stomp.over(socket);
      this.stompClient.debug = () => {};

      this.stompClient.connect(
        {},
        (frame) => {
          // 소켓 연결 성공
          this.connected = true;

          // 최초 한번 recvList 를 받기 위한 send
          this.stompClient.send(
            '/receive',
            JSON.stringify({
              memberName: '',
              followingName: '',
            }),
            {}
          );

          // subscribe 로 alarm List 가져오기
          this.stompClient.subscribe('/send', (res) => {
            this.recvList = JSON.parse(res.body);
          });
        },
        (error) => {
          // 소켓 연결 실패
          console.log('소켓 연결 실패', error);
        }
      );
    },
  },
};
</script>
