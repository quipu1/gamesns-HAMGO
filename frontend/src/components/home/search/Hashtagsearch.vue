<template>
  <div>
    <Header/>
      <div class="hashtag-guide-wording-div" style="padding-top: 45px;">
        <h4 style="margin:0px; font-family: 'Nanum Gothic', sans-serif;">
          <mark class="highlight"><b>#{{$route.params.hashtag}}</b></mark>
        </h4>
      </div>

      <div class="mainfeed" style="margin-top:0px">
        <div class="" @scroll.passive="handleScroll">
          <ModalFeed v-if="isModalViewed" @close-modal="modalClose()" :boardItem="temp" />
          <div v-for="hashtag_board in searched_hashtag_boards" :key="hashtag_board.bid">
            <FeedItem @showModal="modalShow" :boardItem="hashtag_board" />
          </div>
        </div>
      </div>

    <div>
      <link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet" />
    </div>
    <Footer/>
  </div>
</template>

<script>
import Header from '@/components/layout/header/Header.vue'
import Footer from '@/components/layout/footer/Footer.vue'
import FeedItem from '@/components/feed/FeedItem.vue';
import ModalFeed from '@/components/feed/ModalFeed.vue';
import UserApi from "../../../api/UserApi";

export default {
  name: 'Hashtagsearch',
  components: {
    Header,
    Footer,
    FeedItem,
    ModalFeed
  },
  data: () => {
    return {
      isModalViewed: false,
      temp: null,
      searched_hashtag_boards:[],
    }
  },
  created() {
    this.getHashItem('#' + this.$route.params.hashtag);
  },
  watch:{
    '$route' (to) {
      var new_hashtag = to.params.hashtag;
      this.getHashItem('#' + new_hashtag);
    }
  },
  methods: {
    getHashItem(item){
      UserApi.requestHashtagBoard(
        {
          hashtag: item,
        },
        (res) => {
          this.searched_hashtag_boards = res;
          window.scrollTo(0,0);
        },
        () => {}
      );
    },
    modalShow(item) {
      this.isModalViewed = !this.isModalViewed;
      this.temp = item;
      document.body.style.overflow = 'hidden';
    },
    modalClose() {
      this.isModalViewed = !this.isModalViewed;
      this.temp = null;
      document.body.style.overflow = 'scroll';
    },
  },
  beforeDestroy() {
    this.modalClose();
  },
}
</script>

<style>
  .hashtag-guide-wording-div {
    display: flex;
    justify-content: center;
    padding: 15px 0px;
    border-bottom: 1px solid #f4f4f4;
    font-family: 'Nanum Gothic', sans-serif;
  }

  .hashtag-feeds-container{
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .eachfeed {
    width: 70vw;
    height: 70vw;
    border: 1px solid black;
  }

  /* mark {
    display: inline-block;
    line-height: 1em;
    padding-bottom: 0.5em;
} */

.highlight {
  background: linear-gradient(180deg,rgba(255,255,255,0) 50%, #FFD0AE 50%);
}
</style>
