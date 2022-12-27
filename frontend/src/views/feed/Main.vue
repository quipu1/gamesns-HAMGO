<template>
  <div style="height: 100vh;">
    <Header />
    <div class="mainfeed" style="background-color: #fafafa;">
      <div class="" @scroll.passive="handleScroll">
        <ModalFeed v-if="isModalViewed" @close-modal="modalClose()" @replyLengthUpdate="updateReplyLength" :boardItem="temp" />
        <div v-for="boardItem in boardItems" :key="boardItem.bid">
          <div v-if="newReplyLenData.bid == boardItem.bid"><FeedItem @showModal="modalShow" :newReplyLenData="newReplyLenData.len" :boardItem="boardItem" /></div>
          <div v-else><FeedItem @showModal="modalShow"  :boardItem="boardItem" /></div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script>
import '../../components/css/feed/feed-item.scss';
import '../../components/css/feed/mainfeed.scss';
import FeedItem from '../../components/feed/FeedItem.vue';
import Header from '@/components/layout/header/Header.vue';
import Footer from '@/components/layout/footer/Footer.vue';
import ModalFeed from '../../components/feed/ModalFeed.vue';
import UserApi from '../../api/UserApi';
var timer;
export default {
  components: {
    FeedItem,
    Header,
    Footer,
    ModalFeed,
  },
  data() {
    return {
      isModalViewed: false,
      temp: null,
      uid: 0,
      sockid: '',
      nickname: '',
      boardItems: [],
      timer: null,
      newReplyLenData:'',
    };
  },
  created() {
    this.sockid = this.$socketio.id;
    this.uid = this.$store.state.uid;
    this.nickname = this.$store.state.nickname;
    this.getBoardItems();
    // socket io 의 user 업데이트
    this.updateSock();
  },
  mounted() {
    // this.sockid = this.$socketio.id;
    window.addEventListener('scroll', this.handleScroll);
    // this.updateSock();
  },
  methods: {
    updateSock() {
      var updateData = {
        id: this.sockid,
        nickname: this.nickname,
      };

      this.$socketio.emit('updateUser', updateData);
    },
    handleScroll() {
      let scrollLocation = document.documentElement.scrollTop; // 현재 스크롤바 위치
      let windowHeight = window.innerHeight; // 스크린 창
      let fullHeight = document.body.scrollHeight; //  margin 값은 포함 x

      if (
        Math.abs(parseInt(fullHeight) - parseInt(scrollLocation + windowHeight)) < 3 &&
        parseInt(scrollLocation) != 0
      ) {
        if (timer == null) {
          this.getBoardItems(); //다음 뉴스피드 10개를 가져오는 함수
          timer = setTimeout(function() {
            timer = null;
          }, 300);
        }
      }
    },
    getBoardItems() {
      let data;
      if (this.boardItems.length == 0) {
        data = {
          uid: this.uid,
        };
      } else {
        data = {
          uid: this.uid,
          bid: String(this.boardItems[this.boardItems.length - 1].bid),
        };
      }
      UserApi.requestFeedList(
        data,
        (list) => {
          this.boardItems = this.boardItems.concat(list);
        },
        () => {
          alert('메인피드 가져오기 오류!');
        }
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
    updateReplyLength(item){
      this.newReplyLenData = item;
    }
  },
  beforeDestroy() {
    this.boardItems = [];
    window.removeEventListener('scroll', this.handleScroll);
    this.modalClose();
  },
};
</script>
