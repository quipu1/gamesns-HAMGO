<template>
    <div id="menu-nav">
      <div id="navigation-bar">
        <ul>
          <li @click="goToMain"><i class="fa fa-home"></i></li>
          <li @click="goToSearch"><i class="fa fa-search"></i></li>
          <li @click="goToMatching"><i class="fas fa-gamepad"></i></li>
          <li @click="goToChatting"><i class="fas fa-comments"></i></li>
          <li @click="goToWriting"><i class="fas fa-edit"></i></li>
        </ul>
      </div>
    </div>
</template>

<script>
export default {
  name: 'Footer',
  methods: {
    goToMain: function() {
      this.$router.push('/main').catch(error => {
          if(error.name === "NavigationDuplicated" ){
            location.reload();
          }
      });
    },
    goToSearch: function() {
      this.$router.push('/search').catch(error => {
          if(error.name === "NavigationDuplicated" ){
              location.reload();
          }
      });
    },
    goToMatching: function() {
      this.$router.push('/matchingBefore').catch(error => {
          if(error.name === "NavigationDuplicated" ){
            location.reload();
          }
      });
    },
    goToChatting: function() {
      this.$router.push('/chatting').catch(error => {
          if(error.name === "NavigationDuplicated" ){
            location.reload();
          }
      });
    },
    goToWriting: function() {
      this.$router.push('/writing').catch(error => {
          if(error.name === "NavigationDuplicated" ){
            location.reload();
          }
      });
    },
  },
};
</script>

<style>
@import '../../css/layout/footer.css';
@import '../../css/layout/footer.scss';
</style>
