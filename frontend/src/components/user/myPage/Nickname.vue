<template>
  <div>
    {{ nickname }}
  </div>
</template>

<script>
import http from '@/util/http-common';

export default {
    name:'Nickname',
    props:["userInfo"],
    data: () => {
      return {
        uid: 0,
        nickname: "",
      };
    },
    created() {

      if (this.userInfo == null) {
        this.uid = this.$store.state.uid;
      } else {
        this.uid = this.userInfo.uid;
      }

      this.getNickname();
    },
    methods: {
      getNickname: function() {
        let data = {
          uid: this.uid
        }

        http
          .get(`/info`, { params: data })
          .then(({data}) => {
            this.nickname = data.object.nickname
          })
          .catch((err) => {
            console.log('getNickname 에러')
          })
      },
    }
}
</script>

<style>

</style>