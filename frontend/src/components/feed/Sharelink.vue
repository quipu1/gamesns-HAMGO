<template>
  <div>
    <a class="kakao" @click="openkakaoshare">
      <i class="fas fa-share-alt fa-lg" style="width:18px;height:18px"></i>
    </a>
    <head>
      <script
        type="text/JavaScript"
        src="https://developers.kakao.com/sdk/js/kakao.min.js"
      ></script>
    </head>
  </div>
</template>

<script>
import http from '@/util/http-common.js';
export default {
  name: 'Sharelink',
  props: ['boardItem'],
  data: () => {
    return {
      reply_num: 0,
      likelist: [],
      likelist_num: 0,
      contentPreview: '',
    };
  },
  methods: {
    openkakaoshare: function() {
      try {
        if (window.Kakao) {
          window.Kakao.init('95193d8232dc43a8e06b95a27d2bd1ce');
        }
      } catch (e) {
        console.log();
      }
      window.Kakao.Link.sendDefault({
        objectType: 'feed',
        content: {
          title: this.contentPreview,
          description: this.boardItem.nickname,
          // '~~~님의 글'
          imageUrl: 'https://ifh.cc/g/7jY7L7.png',
          link: {
            mobileWebUrl: 'https://i5c203.p.ssafy.io',
            webUrl: 'https://i5c203.p.ssafy.io',
          },
        },
        social: {
          likeCount: this.likelist_num,
          // 좋아요 수
          commentCount: this.reply_num,
          //  댓글 수
        },
        buttons: [
          {
            title: '웹으로 보기',
            link: {
              mobileWebUrl: 'https://i5c203.p.ssafy.io',
              webUrl: 'https://i5c203.p.ssafy.io',
            },
          },
        ],
      });
    },
  },
  created() {
    let data = {
      bid: this.boardItem.bid,
    };
    http
      .get('/reply/cnt', { params: data })
      .then(({ data }) => {
        this.reply_num = data;
      })
      .catch((err) => {
        console.log('reply num 에러입니다');
      });

    let data2;
    data2 = {
      bid: this.boardItem.bid,
    };

    http
      .get(`/board/like`, { params: data2 })
      .then(({ data }) => {
        if (data == null) {
          this.likelist = [];
          this.likelist_num = 0;
        } else {
          this.likelist = data;
          this.likelist_num = this.likelist.length;
        }
      })
      .catch(() => {
        console.log('좋아요 리스트 에러');
      });
    let subContent = '';
    const extractPattern = /<(\/div|a|p|span|li|ui|ol|br)([^>]*)>/gi;
    let extractContent = this.boardItem.contents.replace(extractPattern, '');

    if (extractContent.length < 20) {
      subContent = extractContent;
    } else {
      subContent = extractContent.substr(0, 20) + '...';
    }
    this.contentPreview = subContent.substr(5);
  },
};
</script>

<style>
.kakao {
  color: #ff8f00;
}
</style>
