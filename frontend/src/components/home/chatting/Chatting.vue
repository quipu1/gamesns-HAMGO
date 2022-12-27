<template>
  <div>
    <Header />
    
    <div class="chatting-container" style="padding-top: 45px;">
          <div class="headind_srch">
            <div class="recent_heading">
              <h4>채팅</h4>
            </div>
            <div class="srch_bar">
              <div class="stylish-input-group">
                <input type="text" class="search-bar" v-model="search" placeholder="검색" >
                <span class="input-group-addon">
                <button type="button"> <i class="fa fa-search" aria-hidden="true"></i> </button>
                </span> </div>
              <div
                v-for="(suggest, index) in searched"
                :key="index"
                class="suggestion_box"
                @click="addRoom(suggest)"
              >
                <!--<img />-->
                {{ suggest.nickname }}
              </div>
            </div>
          </div>
          <div class="inbox_chat">
            <div class="chat_list active_chat" v-for="(item, index) in rooms" :key="index" @click="chatLink(item.nickname)">
              <div class="chat_people">
                <div class="chat_img"> <img class="profile_img" :src="`http://localhost:8080/account/file/` + item.id"> </div>
                <div class="chat_ib">
                  <h5 class="chat-nickname">{{item.nickname}}</h5>
                </div>
              </div>
            </div>
            
          </div>
        </div>

      <div>
        <link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet" />
      </div>
    <Footer />
  </div>
</template>

<script>
import Header from "@/components/layout/header/Header.vue";
import Footer from "@/components/layout/footer/Footer.vue";
import UserApi from '../../../api/UserApi';

export default {
  name: "Chatting",
  components: {
    Header,
    Footer,
  },
  data() {
    return {
      id: "",
      nickname: "",
      rooms: [],
      search: "",
      searched: [],
    };
  },
  created() {
    this.id = this.$socketio.id;
    this.nickname = this.$store.state.nickname;

    // this.getRooms();
    // this.$socketio.emit('getRooms', this.$store.state.nickname);
  },
  mounted() {
    this.$socketio.emit("callRooms", this.nickname);

    this.$socketio.on("getRooms", (data) => {
      this.rooms = data;

      let rooms = [];

      for(let d of data){
        UserApi
          .requestGetUser(d,
          (res) => {
            rooms.push({
              id: res.data.object.uid,
              nickname: res.data.object.nickname,
            })
          },
          (() => {}))

      }
      this.rooms = rooms;

    });
  },

  watch: {
    search(val) {
      if (val == "") this.searched = [];
      else {
        UserApi
          .requestSearch({
            nickname: val
          },
          ((res) => {
            this.searched = res;
          }),
          (() => {}))
      }
    },
  },
  methods: {
    chatLink(yourNickname) {
      this.$socketio.emit("sendMsg", {
        id: this.id,
        myNickname: this.nickname,
        yourNickname: yourNickname,
        // 0 : join, 1 : leave
        type: 0,
      });

      this.$router.push({
        name: "InChatting",
        params: { yourNickname: yourNickname },
      });
    },
    addRoom(suggest) {
      // 기존의 rooms 배열 안에 검색 후 선택한 유저가 있는지 확인할 변수
      var flag = true;
      for (const item of this.rooms) {
        // rooms 안에 체크할 유저가 있다면 flag = false
        if (suggest.nickname == item.nickname) {
          flag = false;
          break;
        }
      }

      // 새로 추가해야 할 유저
      if (flag) {
        this.rooms.push({
          id: suggest.uid,
          nickname: suggest.nickname
        });
      }
    },
  },
};
</script>

<style>
  @import "../../css/home/chatting/chatting.css";
  @import "../../css/home/chatting/chatting.scss";

</style>
