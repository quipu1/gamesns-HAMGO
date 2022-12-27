<template>
  <div class="likeBtn" @click="boardLiked" style="width: 3rem; display: flex;">
    <div v-if="likes == 0">
      <p class="m-0" style="color:#ff8f00"><i class="far fa-heart fa-lg"></i></p>
    </div>
    <div v-if="likes > 0">
      <p class="m-0" style="color:#ff8f00"><i class="fas fa-heart fa-lg"></i></p>
    </div>
    <p style="margin-right:0px;">{{ likelist.length }}</p>

    <div>
      <link
        href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
        rel="stylesheet"
      />
    </div>
  </div>
</template>

<script>
import http from '@/util/http-common';
import {mapGetters,mapMutations} from 'vuex';

export default {
  name: 'Like',
  props: ['boardItem'],
  data: () => {
    return {
      likelist: [],
      likes: 0,
      uid: 0,
    };
  },
  created() {
    this.uid = this.$store.state.uid;
    this.getLikeList();
  },
  computed:{
    ...mapGetters(["boardState"]),
  },
  watch:{
    boardState(val){
      if(val.bid == this.boardItem.bid){
        this.setLiked();
      }
    },
  },
  methods: {
    ...mapMutations(["SET_BOARDSTATE"]),

    getLikeList() {
      let data;
      data = {
        bid: this.boardItem.bid,
      };

      http
        .get(`/board/like`, { params: data })
        .then(({ data }) => {
          if (data == null) {
            this.likelist = [];
          } else {

            this.likelist = data;
            this.likes = 0;
            for(let d of data) {
              if(d.uid == this.uid) {
                this.likes = 1;
                break;
              }
            }
          }
        })
        .catch(() => {
          console.log('좋아요 리스트 에러');
        });
    },
    setLiked() {
      let data;
      data = {
        bid: this.boardItem.bid,
        uid: this.uid,
      };

      http
        .get(`/board/liked`, { params: data })
        .then(() => {
          this.getLikeList();
        })
        .catch(() => {
          console.log('좋아요 로드 에러');
        });
    },
    boardLiked: function(e) {
      let data;
      data = {
        bid: this.boardItem.bid,
        uid: this.uid,
      };

      http
        .post(`/board/AddOrDeleteLike`, data)
        .then(() => {

          this.getLikeList();
        })
        .catch(() => {
          console.log('좋아요 에러');
        });

      this.SET_BOARDSTATE({
        bid : this.boardItem.bid,
        likes : this.likes
      });
      // EventBus.$emit('modalEvent', {
      //   bid : this.boardItem.bid,
      //   likes : this.likes
      // });
    },
  },
  // updated(){
  //   EventBus.$on('modalEvent',(bData) => {
  //     console.log(bData);
  //     if(bData.bid == this.boardItem.bid){
  //       this.likes = bData.likes;
  //     }
  //   })
  // }
};
</script>

<style>
.likeBtn {
  cursor: pointer;
}
</style>