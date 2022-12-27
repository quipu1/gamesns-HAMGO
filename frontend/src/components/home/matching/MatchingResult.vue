<template>
  <div>
    <Header />
    <div class="result-container" style="padding-top: 45px;">
    <div class="durl"><a :href="discordUrl" :target="_blank">{{ discordUrl }}</a></div>

    <div class="result-title">이번 매칭은 어떠셨나요? :)</div>

    <div v-for="(user,idx) in matchedUser" :key="idx">
    <div class="result-line" v-if="user.uid != uid" >
      <div class="re-name">
      <Nickname :userInfo="user"/>
      </div>
      <div class="re-manner">
      <Manner :userInfo="user"/>
      </div>
      <div class="re-mselect">
      <MannerSelect :userInfo="user"/>
      </div>
    </div>
    </div>
    <div class="homebtn" @click="goHome"><button>나가기</button></div>
    </div>
    <Footer />
  </div>
</template>
<script>
import Header from '@/components/layout/header/Header.vue';
import Footer from '@/components/layout/footer/Footer.vue';
import MannerSelect from '@/components/user/myPage/MannerSelect.vue';
import Manner from '@/components/user/myPage/Manner.vue';
import Nickname from '@/components/user/myPage/Nickname.vue';

export default {
  components: {
    Header,
    Footer,
    Manner,
    MannerSelect,
    Nickname
  },
  data() {
    return {
      uid: 0,
      discordUrl: '',
      matchedUser: [],
    };
  },
  created() {
    this.uid = this.$store.state.uid;
    this.discordUrl = this.$route.params.discordUrl;
    this.matchedUser = this.$route.params.matchedUser;
  },
  methods: {
    goHome: function() {
      this.$router.push('/main');
    },
  }
};
</script>
<style>
@import '../../../components/css/home/matching/result.css';
@import '../../../components/css/home/matching/result.scss';
</style>
