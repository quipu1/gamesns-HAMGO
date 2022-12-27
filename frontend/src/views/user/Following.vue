<template>
  <div class="following-container">
    <Header/>
    <div class="form" style="padding-top: 45px;">
      <h4 class="title">Following - {{ following.length }}</h4>
      <!-- 나중에 닉네임 같은 걸로 내용 바꾸기~ -->
      <li class="list" v-for="(follow, idx) in following" :key="idx">
        <div class="small-user-img-div">
          <img :src="'http://localhost:8080/account/file/' + follow.uid" class="small-user-img">
          <span class="small-user-comment" @click="goUserPage(follow)">{{follow.nickname}}</span>
        </div>
        <!-- 사용자 아이디와 같으면 삭제 버튼을 생성 : 사용자 아이디는 어떤 화면에서든 가져올 수 있다. -->
        <button class="deny-btn"  v-if="isMe" @click="deleteFollow(follow.nickname)">삭제</button>
      </li>
    </div>
    <div>
      <link href="https://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet">
    </div>
    <Footer/>
  </div>
</template>

<script>
import Header from '@/components/layout/header/Header.vue'
import Footer from '@/components/layout/footer/Footer.vue'
import UserApi from '@/api/UserApi.js'

export default {
    name:'Following',
    components: {
      Header,
      Footer,
    },
    data () { 
      return {
        uid: '',
        nickname:'',
        following: [],
        isMe: false,
      }
    },
    created() {
      this.following = this.$route.params.following;
      this.uid = this.$store.state.uid;
      this.nickname = this.$store.state.nickname;
      if(this.uid == this.$route.params.id) this.isMe = true;
    },
    methods:{
      goUserPage(u){
        this.$router.push({name:"UserPage", params:{u}});
      },
      deleteFollow(u){
        let data = {
          fromNickname: this.nickname,
          toNickname: u,
          type: 0
        }
        UserApi
          .requestFollowUpdate(
            data,
            (() => {
              alert("삭제되었습니다.");
              
              UserApi
                .requestFollowing( { from : this.uid },
                (res) => {
                  this.following = res;
                },
                () => {})
            }),
            (() => {})
          )
      }
    }
      
}
</script>

<style>
  @import "../../components/css/user/follow.css";
  @import "../../components/css/user/follow.scss";
</style>