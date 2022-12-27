<template>
  <div class="alarm-container" style="background-color: #fafafa;">
    <Header />
    <div class="alarm-res" style="padding-top: 45px;">
    <div class="form">
      <div>
        <h5 v-if="List == undefined" class="title">알림 - 0개</h5>
        <div v-else>
          <h5 class="title">알림 - {{ List.length }}개</h5>
          <li class="list" v-for="(user, index) in List" :key="index">
          <div class="small-user-img-div">
              <img
                :src="`http://localhost:8080/account/file/` + user.uid"
                class="small-user-img"
              />
              <!-- 임의의 이미지가 들어가는거라, user의 프로필사진이 나오게 해야 함. -->
                <span class="small-user-comment">
                  <div style="margin:0">
                    {{ user.nickname }} 님이 팔로우를 신청하셨습니다.
                  </div>
                </span>
            </div>
            <button class="agree-btn" @click="go(user, 1)">수락</button>
            <button class="deny-btn" @click="go(user, 0)">거절</button>
          </li>
        </div>
      </div>
    </div>
    <div>
      <link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet" />
    </div>
    </div>
    <Footer />
  </div>
</template>

<script>
import Header from '@/components/layout/header/Header.vue';
import Footer from '@/components/layout/footer/Footer.vue';
import UserApi from '../../api/UserApi';

export default {
  name: 'App',
  props: ['stompClient', 'recvList'],
  components: {
    Header,
    Footer,
  },
  data() {
    return {
      List: [],
      nickname: '',
      userName: '',
      followingName: '',
    };
  },
  created() {
    this.nickname = this.$store.state.nickname;
    try {
      this.List = this.recvList[this.nickname];
    } catch(err) {
      // console.log(err)
    }
    // if(this.recvList[this.nickname] != null) this.List = this.recvList[this.nickname];
  },
  watch: {
    recvList: function() {
      this.List = this.recvList[this.nickname];
    },
  },
  methods: {
    go(u, n) {
      // WebSocket 의 알림 리스트에서 해당 항목 삭제
      let data = {
        uid: u.uid,
        memberName: u.nickname,
        followingName: this.nickname,
      };
      
      this.stompClient.send('/receive', JSON.stringify(data), {});

      if (n == 1) {
        // Follow 관련 테이블에서 해당 항목 추가
        UserApi.requestFollowUpdate(
          {
            fromNickname: u.nickname,
            toNickname: this.nickname,
            type: 0,
          },
          () => {},
          () => {}
        );
      }
    },
  },
};
</script>

<style>
@import '../../components/css/alarm/alarm.css';
@import '../../components/css/alarm/alarm.scss';
</style>
