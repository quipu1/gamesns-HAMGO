<template>
  <div>
      <div class="mbadgecon" style="margin-left: 0px;
    margin-right: 0px;">

        <div class="mbadges"
        style="padding-left: 0px; padding-right: 0px;"
         v-for="(badge, idx) in badgelist" :key="idx">
            <div>
              <img :src="'http://localhost:8080/badge/file/' + badge.no">
              <p>{{badge.name}}</p>
            </div>
        </div>
      </div>
  </div>
</template>

<script>
import http from '@/util/http-common';

export default {
    name:'MatchingBadge',
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

      this.getMatchingBadge();

    },
    methods: {
      getMatchingBadge: function() {

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
@import "../../components/css/user/matchbadge.css";
@import "../../components/css/user/matchbadge.scss";
</style>