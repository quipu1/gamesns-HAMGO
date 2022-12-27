<template>
  <div>
    <Header/>
    <div class="matching-container" style="padding-top: 45px;">
      <!-- <div class="game-kind-btns">
      </div> -->
      <div class="select-boxes">
        <div class="game-select-box">
          <select v-model="selectedGame" class="select-box">
            <option v-for="(game,index) in games" :key="index" >
              {{ game }}
            </option>
          </select>
          <p>원하는 게임을 선택하세요</p>
        </div>
        <div class="number-select-box">
          <select v-model="selectedPeople" class="select-box">
            <option v-for="(people,index) in people" :key="index">
              {{ people.num }}
            </option>
          </select>
          <p>원하는 인원을 선택하세요</p>
        </div>
      </div>
      <button id = 'matchStart' style="" :disabled="validated == 0">함고?</button>
    </div>
    <div>
      <link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet">
    </div>
    <Footer/>
  </div>
</template>

<script>
import Header from '@/components/layout/header/Header.vue'
import Footer from '@/components/layout/footer/Footer.vue'
import UserApi from '@/api/UserApi'

export default {
  name:'Matching',
  components: {
    Header,
    Footer
  },
  data(){
    return{
      selectedGame : "",
      selectedPeople: "",
      discordId: this.$route.params.data.discordId,
      games:[],
      people:["게임을 선택해주세요."],
      validated: 0,
    }
  },
  created(){
    UserApi
      .requestGames("",
      ((res) => {
        this.games = res.object;
      }),
      (() => {}))
  },
  mounted(){
    const $matchStart =  document.querySelector('#matchStart');
    $matchStart.addEventListener('click',() => this.clickEvent());
  },
  watch:{
    selectedGame() {
      UserApi
        .requestPeople({
          game: this.selectedGame
        },
        ((res) => {
          this.people = res.object}),
        (() => {}))
    },
    selectedPeople() {
      if(this.selectedPeople != ""){
        const discordCheckBtn = document.querySelector("#matchStart")
        discordCheckBtn.style.backgroundColor = "#FFAF0A"
        this.validated = 1;
      } else{
        const discordCheckBtn = document.querySelector("#matchStart")
        discordCheckBtn.style.backgroundColor = "#8197a1"
        this.validated = 0;
      }
    },
  },
  methods: {
    clickEvent(){
      let data = {
        discordId : this.discordId,
        selectedGame : this.selectedGame ,
        selectedPeople : this.selectedPeople,
      }
      this.$router.push({ name: 'MatchingStart', params: {data}});
    },
  },
}
</script>

<style>
  @import "../../css/home/matching/matching.css";
</style>