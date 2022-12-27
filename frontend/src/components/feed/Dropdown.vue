<template>
	<div class="dropdown">
    <button class="dropbtn" @click="showDrop">
			<img style="width: 16px;" src="@/assets/images/ellipsis.png" alt="" />
		</button>
    <div class="dropdown-content" v-if="show">
			<!-- <a style="color: #1E90FF; border-bottom: 1px solid grey">수정</a> -->
			<a @click="deleteFeed" class="dropdown-delete-btn" style="color: #CD5C5C;">삭제</a>
    </div>
  </div>
</template>

<script>
import http from '@/util/http-common';

export default {
	name: 'Dropdown',
	props: ['boardItem'],
	created() {
		// this.uid = this.$store.state.uid;\
	},
	data: () => {
		return {
			show: false,
			bid: 0,
		}
	},
	methods: {
		showDrop() {
			this.show = !this.show;
		},
		deleteFeed() {

			this.bid = this.boardItem.bid

			let data = {
				bid: this.bid
			}

			http
				.delete(`/board/${this.bid}`, data)
        .then(({ data }) => {
					document.location.reload();
        })
        .catch(() => {
          console.log('feed delete 에러');
        });
		}
	}

}
</script>

<style>
	@import '../css/feed/dropdown.css';
</style>