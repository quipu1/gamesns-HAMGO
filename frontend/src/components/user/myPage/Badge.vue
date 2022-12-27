<template>
  <div>
      <div class="badgetitle">
        <p>✨ 배찌빼지 ✨</p>
      </div>
      <div class="badgecon row" style="margin-left: 0px;
    margin-right: 0px;">

        <div class="badges col-6 col-xs-6 col-sm-4 col-md-4 clear-xs-2 clear-md-2"
        style="padding-left: 0px; padding-right: 0px;"
         v-for="(badge, idx) in badgelist" :key="idx">
              <img :src="'http://localhost:8080/badge/file/' + badge.no">
              <p>{{badge.name}}</p>
        </div>
      </div>
  </div>
</template>

<script>
import http from '@/util/http-common';

export default {
    name:'Badge',
    props:["userInfo"],
    data: () => {
      return {
        uid: 0,
        badgelist: [],
      }
    },
    created() {
      if (this.userInfo == null) {
        this.uid = this.$store.state.uid;
      } else {
        this.uid = this.userInfo.uid;
      }

      this.getBadge();

    },
    methods: {
      getBadge: function() {

        let data = {
          uid: this.uid
        }

        http
          .get(`/member/badge`, { params: data })
          .then(({data}) => {
            this.badgelist = data.object
          })
          .catch((err) => {
            console.log('겟배지 에러')
          })
      }
    }
}
</script>

<style>
@import "../../../components/css/user/badge.css";
@import "../../../components/css/user/badge.scss";
</style>