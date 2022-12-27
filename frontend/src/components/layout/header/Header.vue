<template>
    <div class="main-container">
        <ul class="header" style="right: 0px;">
          <div class="logo">
            <img @click="goToMain" src="@/assets/images/logolight.png" alt="" style="width:120px;"/>
          </div>
          <div class="alarm-btn">
            <div @click="goAlarm"><i class="fas fa-bell"></i></div>
          </div>
          <div class="mypage-btn">
            <div @click="goToMypage"><i class="fas fa-user-circle"></i></div>
          </div>
          <!-- <div class="logout-btn">
            <div><i class="fas fa-sign-out-alt"></i></div>
          </div> -->
        </ul>
    </div>
</template>

<script>
export default {
  name: 'Header',
  methods: {
    goToMypage: function() {
      this.$router.push('/mypage').catch(error => {
          if(error.name === "NavigationDuplicated" ){
              location.reload();
          }
      });
    },
    goToMain: function() {
      this.$router.push('/main').catch(error => {
          if(error.name === "NavigationDuplicated" ){
              location.reload();
          }
      });
    },
    goAlarm: function() {
      this.$router.push('/alarm').catch(error => {
          if(error.name === "NavigationDuplicated" ){
              location.reload();
          }
      });
    },
  },
};
</script>

<style>
@import '../../css/layout/header.css';
@import '../../css/layout/header.scss';
</style>
