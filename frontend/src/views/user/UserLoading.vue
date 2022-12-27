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
        <div class="user-loading-container">
          <div class="user-loading"></div>
          <div id="user-loading-text">loading</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<script>
import UserApi from '../../api/UserApi';
import { mapActions, mapGetters } from 'vuex';
import { login } from '../../common/UserLogin';

export default {
  name: 'App',
  data: () => {
    return {
      code: '',
      nickname: '',
    };
  },
  mounted() {
    if (!this.$route.params.isJoin) {
      this.kakaoLogin();
    } else {
      this.isUser();
    }
  },
  methods: {
    ...mapActions(['setUid', 'setNickname']),
    kakaoLogin() {
      this.code = this.$route.query.code;

      UserApi.requestkakaoLogin(
        this.code,
        (res) => {
          // this.uid = res;

          this.setUid(res);

          this.isUser();
        },
        (error) => {
          alert('잘못된 접근입니다!');
          this.$router.push('/');
        }
      );
    },

    isUser() {
      UserApi.requestExistUser(
        this.uid,
        (res) => {

          if (res.status) {
            this.nickname = res.object;

            this.setNickname(this.nickname);

            let status = login(this.uid);

            if (status) {
              setTimeout(() => {
                this.$router.push('/main');
              }, 1500);
            } else {
              alert('오류가 발생했습니다. 다시 시도해주세요.');
              this.$router.push('/');
            }
          } else {
            this.$router.push('/user/join');
          }
        },
        (error) => {
          alert('잘못된 접근입니다!!');
          this.$router.push('/');
        }
      );
    },
  },
  computed: {
    ...mapGetters(['uid']),
  },
};
</script>

<style>
@import '../../components/css/user/login.css';
@import '../../components/css/user/loading.css';
</style>
