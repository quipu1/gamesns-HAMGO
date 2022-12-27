<template>
  <div>
    <Header/>
    <div class="matching-container" style="padding-top: 45px;">
      <!-- <div class="game-kind-btns">
      </div> -->
      
      <div class="discord-link-total-div">
        <div class="discord-link-div">
          <a class="discord-link" style="color:white;margin:0px;text-decoration:none" href="https://discord.gg/BbcYwNcFv9" target="_blank">함고 서버링크</a>
        </div>
        <p style="margin:0px;" id="discord-plus-comment">함고 채널에 접속하셔야 매칭이 가능합니다.</p>
      </div>
      
        <div class="discord-id-box">
          <input
          class="discord-id-input"
          v-model="discordId"
          type="text"
          placeholder="Example#1234"
          :disabled="validated == 1"
          />
          <p id="discord-comment">디스코드 아이디를 해시태그까지 적어주세요</p>
        </div>
      <button class="matchingBefore-btn" @click="discordCheckClick" id = 'discordCheck' :disabled="gamerCheck == 0">{{text}}</button>
      <button class="matchingBefore-btn" id = 'matchNext' :disabled="validated == 0">다음</button>
      <div class="error-text">{{error}}</div>
    </div>
    <Footer/>
  </div>
</template>
<script>
import Header from '@/components/layout/header/Header.vue'
import Footer from '@/components/layout/footer/Footer.vue'
import UserApi from '@/api/UserApi'
var clicks = 0; 

export default {
  name:'MatchingBefore',
  components: {
    Header,
    Footer
  },
  mounted(){
    const $matchStart =  document.querySelector('#matchNext');
    $matchStart.addEventListener('click',() => this.next());

    const $discordCheck =  document.querySelector('#discordCheck');
    $discordCheck.addEventListener('click',() => this.check());
  },
  data(){
    return {
      text:"접속 확인",
      discordId : "",
      validated : 0,
      gamerCheck: 0,
      error : "",
      regex: /#[0-9]{4,4}$/,
    }
  },
  watch: {
    discordId() {
      this.onUpdate();
    },
  },
  methods: {
    check(){
      let data = {
        userTag : this.discordId
      }

      UserApi
      .requestDiscord(data,
      ((res) => {
        if(res){
          this.validated = 1;
          const discordComment = document.querySelector("#discord-comment")
          discordComment.style.color = "white";
          const discordCheckBtn = document.querySelector("#discordCheck")
          discordCheckBtn.style.backgroundColor = "#FFAF0A"
          const discordNextBtn = document.querySelector("#matchNext")
          discordNextBtn.style.backgroundColor = "#37474f"
          this.error = "";
          this.text = "확인 완료";
        } else{
          this.validated = 0;
          this.error = "접속 중이지 않은 아이디입니다.\n 함고 채널에 들어와 주세요!";
        }
      }),
      (() => {}))
    },
    next(){
      let data = {
        discordId : this.discordId,
      }
      this.$router.push({ name: 'Matching', params: {data}});
    },
    discordCheckClick(){
        if (clicks >= 10) { 
      alert("잠시만 기다려주십시오");
      clicks = 0; 
      } 
      clicks += 1; 
    },
    onUpdate(){
      let gamerTag = this.regex.exec(this.discordId);
      const discordCheckBtn = document.querySelector("#discordCheck")
      if(gamerTag){
        this.gamerCheck = 1;
        this.error = ""
        discordCheckBtn.style.backgroundColor = "#37474f"
      } else {
        this.gamerCheck = 0;
        this.error = "올바른 아이디를 적어주세요."
        discordCheckBtn.style.backgroundColor = "#8197a1"
      }
    },
  },
}
</script>
<style>
  @import "../../css/home/matching/matchingBefore.css";
  @import "../../css/home/matching/matchingBefore.scss";
</style>