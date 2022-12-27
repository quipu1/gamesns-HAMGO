<template>
  <div>
    {{ score }}
  </div>
</template>

<script>
import http from '@/util/http-common';

export default {
    name:'Manner',
    props:["userInfo"],
    data: () => {
      return {
        uid: 0,
        score: 0,
      };
    },
    created() {

      if (this.userInfo == null) {
        this.uid = this.$store.state.uid;
      } else {
        this.uid = this.userInfo.uid;
      }

      this.getManner();
    },
    methods: {
      getManner: function() {
        
        let data;
        data = {
          uid: this.uid
        }

        http
          .get(`/member/manner`, { params: data })
          .then(({data}) => {
            this.score = data.object
          })
          .catch(() => {
            console.log('getmanner 에러')
          })
      },
      // addManner: function() {

      //   let data;
      //   data = {
      //     uid: this.uid
      //   }



      // }
    }
}
</script>

<style>

</style>