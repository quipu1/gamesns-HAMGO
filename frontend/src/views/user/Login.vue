<template>
<div style="height: 100vh; background-color: #FFB937;">
	<div class="wrapper fadeInDown">
		<div id="formContent">
    <!-- Tabs Titles -->

    <!-- Icon -->
    <div class="fadeIn first" style="margin-top: 50px; margin-bottom: 50px;">
        <img src="@/assets/images/logo2.png" alt="" id="icon" />
    </div>

    <!-- Login Form -->
    <form class="apibox">
		<h6 class="easy fadeIn second">간편 로그인</h6>
		<div class="login-btn fadeIn third">
			<img  src="@/assets/images/kakao.png"  style="width: 70%;" @click="login"/>
        </div>
    </form>

</div>
		<div>
			<link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
			<link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet">
		</div>
	</div>
</div>
</template>

<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<script>
import UserApi from '../../api/UserApi';

export default {
  name: 'App',
  components: {},
  methods: {
    login() {
      const CLIENT_ID = process.env.VUE_APP_KAKAO_ID;
      const REDIRECT_URI = process.env.VUE_APP_KAKAO_URI;

      window.location.replace(
        'https://kauth.kakao.com/oauth/authorize?client_id=' +
          CLIENT_ID +
          '&redirect_uri=' +
          REDIRECT_URI +
          '&response_type=code'
      );
    },
    onSuccess: function(e) {
      let data = {
        access_token: e.access_token,
      };

      UserApi.requestkakaoLogin(
        data,
        (res) => {
          this.$router.push('/feed/main');
        },
        (error) => {
          this.$router.push('/user/join');
        }
      );
    },
    onFailure: function(e) {
      console.log(e);
      console.log('failure');
    },
    logout(e) {
      let data = {
        access_token: e.access_token,
      };
      UserApi.logout(
        data,
        (res) => {},
        (error) => {}
      );
    },
  },
};
</script>

<style >
  @import "../../components/css/user/login.css";
</style>
