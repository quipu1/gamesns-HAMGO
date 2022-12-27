<!-- 테스트 -->
<!--
    가입하기는 기본적인 폼만 제공됩니다
    기능명세에 따라 개발을 진행하세요.
    Sub PJT I에서는 UX, 디자인 등을 포함하여 백엔드를 제외하여 개발합니다.
 -->
<template>
  <div class="join-body-container">
    <div class="join-container join-res">
      <div class="login-box">
        <div class="logo-box">
          <img src="@/assets/images/logo2.png" alt="" style="width: 90%; height: auto;" />
        </div>
        <h5 style="margin-bottom: 40px; font-family: 'Nanum Gothic', sans-serif;">
          회원가입
        </h5>

        <div>
          <input
            v-model="nickname"
            autocapitalize="off"
            v-bind:class="{
              error: error.nickname,
              complete: !error.nickname && nickname.length !== 0,
            }"
            id="nickname"
            placeholder="닉네임을 입력하세요."
            type="text"
            class="nickname-input"
          />
          <input
            type="submit"
            class="nickname-input"
            id="nickname-submit"
            value="중복확인"
            @click="dupCheck"
          />
          <label for="nickname"></label>
          <div iv class="error-text" v-if="error.nickname">
            {{ error.nickname }}
          </div>
        </div>
        <button
          class="join-btn"
          @click="signUp"
          :disabled="!isSubmit"
          :class="{ disabled: !isSubmit }"
        >
          START
        </button>
      </div>
    </div>
    <div>
      <link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet" />
    </div>
  </div>
</template>

<script>
import UserApi from '../../api/UserApi';
import { mapActions, mapGetters } from 'vuex';
import { login } from '../../common/UserLogin';

export default {
  data: () => {
    return {
      nickname: '',
      isDup: false,
      error: {
        nickname: false,
      },
      isSubmit: false,
    };
  },
  watch: {
    nickname: function(v) {
      this.checkForm();
    },
  },
  methods: {
    ...mapActions(['setNickname']),
    checkForm() {
      // nickname 중복 확인 필요
      if (this.nickname.length == 0) this.error.nickname = '닉네임은 한 글자 이상이어야 합니다.';
      else this.error.nickname = false;

      let isSubmit = true;
      Object.values(this.error).map((v) => {
        if (v) isSubmit = false;
      });
      this.isSubmit = isSubmit && this.isDup;
    },
    signUp() {
      if (this.isSubmit && this.isDup) {
        let data = {
          uid: this.uid,
          nickname: this.nickname,
        };

        this.isSubmit = false;

        UserApi.requestSignUp(
          data,
          (res) => {
            if (res.status) {
              this.isSubmit = true;
              // feed/main으로 가야함

              this.setNickname(this.nickname);

              let status = login(this.uid);

              if (status) {
                this.$router.push({ name: 'Loading', params: { isJoin: true } });
              } else {
                alert('오류가 발생했습니다. 다시 시도해주세요.');
                this.$router.push('/');
              }
            } else {
              alert('문제가 생겼습니다. 다시 시도해주세요.');
              this.$router.push('/');
            }
          },
          (error) => {
            if (error) this.$router.push('/error');
            this.isSubmit = true;
          }
        );
      } else {
        Object.values(this.error).map((v) => {
          if (v) alert(v);
        });
      }
    },
    dupCheck() {
      if (!this.error.nickname) {
        UserApi.requestDupCheck(
          this.nickname,
          (res) => {
            if (res.data.data == 'fail') {
              alert('사용 중인 닉네임입니다.');
            } else {
              alert('사용가능한 닉네임입니다.');
              this.isDup = true;
              this.checkForm();
            }
          },
          () => {}
        );
      }
    },
  },
  computed: {
    ...mapGetters(['uid']),
  },
};
</script>

<style>
@import '../../components/css/user/join.css';
</style>
