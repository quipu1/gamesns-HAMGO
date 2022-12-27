<template>
	<div class="manner-select-box">
		<select v-model="score" class="mselect-box">
      <option v-for="(op, index) in options" :key="index" :value="op.value">{{ op.name }}</option>
    </select>
    <button class="mbtn" @click="addManner" v-if="showbtn"> 완료 </button>
	</div>
</template>

<script>
import http from '@/util/http-common';

export default {
  name: 'MannerSelect',
  props:['userInfo'],
	data: () => {
		return {
      uid: 0,
			score: 0,
      showbtn: 1,
			options: [
				{ 
          value: -5,
          name: '너무 싫어요!'
        },
        { 
          value: 2,
          name: '싫어요!'
        },
        { 
          value: 3,
          name: '그냥 그래요'
        },
        { 
          value: 4,
          name: '좋아요'
        },
        { 
          value: 5,
          name: '너무 좋아요!'
        },
			]
		}
	},
  created() {
      if (this.userInfo == null) {
        this.uid = this.$store.state.uid;
      } else {
        this.uid = this.userInfo.uid;
      }
    },
  methods: {
    addManner: function() {

      let data = {
        uid: this.uid,
        score: this.score
      };

      http
      .put('/member/manner', data)
      .then(({data}) => {
        this.showbtn = 0
      })
      .catch((err) => {
        console.log('addmanner 실패')

      })
    }
  }
}
</script>

<style>
@import "../../../components/css/user/mannerselect.css";
</style>