<template>
  <div class="inchatting-container">
    <Header />
    <div class="msg_area" style="padding-top: 45px;">
      <div class="mesgs">
        <div class="msg_history">
          <div
            class="incoming_msg"
            v-for="(item, index) in msgHistory"
            :key="index"
          >
            <div
              v-if="item.type != undefined"
              class="chat-box-single-line"
              style="text-align: center;"
            >
              {{ item.msg }}
            </div>
            <div v-else>
              <div v-if="item.nickname == nickname" class="outgoing_msg">
                <div class="sent_msg">
                  <p>{{ item.msg }}</p>
                  <span class="time_date">{{item.time}}</span>
                </div>
              </div>
              <div
                v-else-if="item.nickname == yourNickname"
                class="incoming_msg"
              >
                <div class="incoming_msg_img">
                  <img class="profile_img" :src="`http://localhost:8080/account/file/` + yourid" />
                </div>
                <div class="received_msg">
                  <div class="received_withd_msg">
                    <p>{{ item.msg }}</p>
                    <span class="time_date">{{item.time}}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="type_msg" style="background-color:white">
        <div class="input_msg_write">
          <div v-if="!chatPossible" style="text-align:center; ">
            <p style="margin:0.5rem 0 0.5rem 0; ">상대방의 입장을 기다리고 있습니다.</p>
          </div>
          <div v-else>
            <input
              type="text"
              class="write_msg"
              v-model="message"
              @keyup.enter="sendMessage"
              placeholder="Type a message"
              style="padding-left:10px; vertical-align: middle;"
            />
            <button
              class="msg_send_btn"
              style="margin-right:10px; padding-right:2px"
              type="button"
              @click="sendMessage"
            >
              <i class="fas fa-paper-plane"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script>
import Header from "@/components/layout/header/Header.vue";
import Footer from "@/components/layout/footer/Footer.vue";
import UserApi from "../../../api/UserApi";

export default {
  name: "InChatting",
  components: {
    Header,
    Footer,
  },
  data() {
    return {
      id: "",
      uid: "",
      yourid: "",
      nickname: "",
      yourNickname: "",
      textarea: "",
      message: "",
      chatPossible: false,
      msgHistory: [],
    };
  },
  created() {
    this.id = this.$socketio.id;
    this.uid = this.$store.state.uid;
    this.nickname = this.$store.state.nickname;
    this.yourNickname = this.$route.params.yourNickname;

    UserApi.requestGetUser(
      this.yourNickname,
      (res) => {
        this.yourid = res.data.object.uid;
      },
      () => {}
    );
    // msg 받기
    this.$socketio.on("recvMsg", (data) => {
      // 받는 유저의 닉네임이 같을 때만
      if (data.sender == this.yourNickname) {
        // this.textarea += data.msg;

        // 메세지 보내기
        this.msgHistory.push({
          nickname: data.sender,
          msg: data.msg,
          time: data.time,
          type: data.type,
        });
      }
    });
  },
  mounted() {
    this.$socketio.on("getJoinNum", (data) => {
      if (data == 2) {
        this.chatPossible = true;
      } else {
        this.chatPossible = false;
      }
    });
    
  },
  updated(){
    let divdiv = document.getElementsByClassName("msg_history");
    divdiv[0].scrollTop = divdiv[0].scrollHeight;
  },
  methods: {
    getDate(){
      let today = new Date();   

      let hours = today.getHours(); // 시
      
      let tmp = "";
      if(hours > 12) {
        tmp = "PM";
        hours -= 12;
      } else {
        tmp = "AM";
      }
      let minutes = today.getMinutes();  // 분
      let month = today.getMonth() + 1;  // 월
      let date = today.getDate();  // 날짜  

      let time = hours+":"+minutes+" " + tmp + " | " + month + "/" + date;

      return time;
    },
    sendMessage() {

      var data = {
        id: this.id,
        myNickname: this.nickname,
        yourNickname: this.yourNickname,
        msg: this.message,
        time: this.getDate(),
        type: 2,
      };
      this.$socketio.emit("sendMsg", data);

      // 메세지 보내기
      this.msgHistory.push({
        nickname: this.nickname,
        msg: data.msg,
        time : data.time
      });

      

      this.message = "";
    },
  },
  beforeDestroy() {
    this.$socketio.emit("sendMsg", {
      myNickname: this.nickname,
      yourNickname: this.yourNickname,
      type: 1,
    });
  },
};
</script>

<style>
@import "../../css/home/chatting/inchatting.css";
@import "../../css/home/chatting/inchatting.scss";
</style>
