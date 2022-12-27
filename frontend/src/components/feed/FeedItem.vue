<template>
  <div class="fbody">
    <div class="cardbox bg-white">
      <!-- 프로필 -->
      <div class="cardbox-heading" style="padding-right: 0px">
        <!-- 내부 사진 크기 조절 필요 -->
        <div class="fimg">
          <img :src="'http://localhost:8080/account/file/' + boardItem.uid" />
        </div>
        <div class="media-body">
          <p class="fname mb-0" @click="userSearch">{{ boardItem.nickname }}</p>
          <p class="m-0 time">
            {{ boardItem.createDate | moment('from', 'now') }}
          </p>
        </div>
        <!--/ media -->
        <!-- dropdown menu -->
       <Dropdown v-if="mine" :boardItem="boardItem"/>
      </div>
      <!--/ cardbox-heading -->

      <!-- 이미지나 내용 -->
      <div class="cardbox-item">
        <div class="image-slider" style="padding: 0;" v-if="img_src.length > 0">
          <div class="slider" style="padding: 0;" v-if="img_src.length > 1">
            <button class="prev" @click="prev"><i class="black fas fa-chevron-left"></i></button>
            <button class="next" @click="next"><i class="black fas fa-chevron-right"></i></button>
          </div>
          <!-- 이미지 -->
          <div
            class="ffimg"
            style="padding: 0px;"
            v-for="number in [currentNumber]"
            v-bind:key="number"
            transition="fade"
          >
            <img
              class="img-fluid"
              alt=""
              :src="img_src[Math.abs(currentNumber) % img_src.length]"
            />
          </div>
        </div>
        <!-- 내용 -->
        <!-- <editor v-model="boardItem.contents" :isOK="false"/> -->
        <div style="padding: 0px;">
          <editor-content v-if="boardItem.contents" :editor="editor" />
          <div class="showContentDiv">
            <a @click="showContent" class="grayText">자세히보기</a>
          </div>
          <div v-if="hashtag_list != 0" class="hashtag-div" style="padding: 20px 10px 0px 10px;">
            <span v-for="(hashtag, idx) in hashtag_list" :key="idx">
              <router-link :to="{ name: 'Hashtagsearch', params: { hashtag: hashtag.slice(1) } }"
                ><span style="margin-right:3px">{{ hashtag }}</span></router-link
              >
              <!-- hashtag = #싸피 -->
            </span>
            <!-- 해시태그가 없다면 안나오는 로직 써야함 v-if length()? -->
          </div>
        </div>
        <!-- <div>{{boardItem.contents}}</div> -->
      </div>
      <!--/ cardbox-item -->

      <!-- 좋아요 등 -->
      <div class="cardbox-base">
        <div class="likebox">
          <Like :boardItem="boardItem" />
          <div class="replyShowContentBtn" @click="showContent">
            <Reply :boardItem="boardItem" :now_reply_num="newReplyLenData" />
          </div>
        </div>
        <div class="sharebox">
          <Sharelink :boardItem="boardItem" />
        </div>
      </div>
      <!--/ cardbox-base -->

      <div>
        <link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet" />
        <link
          href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
          rel="stylesheet"
        />
      </div>
    </div>
  </div>
</template>

<script>
import defaultImage from '../../assets/images/img-placeholder.png';
import defaultProfile from '../../assets/images/profile_default.png';
import { Editor, EditorContent } from '@tiptap/vue-2';
import StarterKit from '@tiptap/starter-kit';
import Paragraph from '@tiptap/extension-paragraph';
import { mergeAttributes } from '@tiptap/core';
import Sharelink from './Sharelink';
import Like from './Like';
import Dropdown from './Dropdown';
import Reply from './Reply';
import UserApi from "../../api/UserApi";
import '@/components/css/feed/feed-item.scss';

export default {
  props: ['boardItem','newReplyLenData'],
  components: {
    EditorContent,
    Sharelink,
    Like,
    Reply,
    Dropdown,
  },
  data: () => {
    return {
      nickname: "",
      contentPreview: '',
      editor: null,
      defaultImage,
      defaultProfile,
      img_src: [],
      currentNumber: 0,
      hashtag_list: [],
      temp_hashtaglist: [],
      mine: 0,
    };
  },
  created() {

    // 피드 수정, 삭제 버튼
    // 글 쓴 사람이면 보이게
    this.uid = this.$store.state.uid;
    if (this.uid == this.boardItem.uid) {
      this.mine = true
    }

    this.nickname = this.$store.state.nickname;

    this.boardItem.imgFiles.forEach((element) => {
      this.img_src.push('http://localhost:8080/board/file/' + element.file_name);
    });

    // 통으로 받아온 해시태그 스페이스바 기준으로 잘라서 temp_hashtaglist에 넣어주기
    if (this.boardItem.hashtags != undefined) {
      this.temp_hashtaglist = this.boardItem.hashtags.split(' ');
    }

    // temp_hashtaglist에서 하나씩 꺼내서 검사 해준 후에, 유효한 해시태그들만 hashtag_list에 넣어주기
    for (var i = 0; i < this.temp_hashtaglist.length; i++) {
      if (this.temp_hashtaglist[i].indexOf('#') == 0) {
        if (this.temp_hashtaglist[i].length >= 2) {
          this.hashtag_list.push(this.temp_hashtaglist[i]);
        }
      }
    }
    // 만들어졌을 때, get요청을 보내서 hashtags를 통으로 들고오고
    // 스페이스바로 분리하여 하단에 파란색으로 보여준다.

    // 그리고 각기 해시태그를 누르면
    // http://localhost:8081/#싸피 이런식으로
    // 해당 해시태그가 나온 글들이 모두 나와야함.

    let subContent = '';

    // const extractDivPattern = /(<([^>]+)>)/gi;
    const extractPattern = /<(\/div|a|p|span|li|ui|ol)([^>]*)>/gi;
    let extractContent = this.boardItem.contents.replace(extractPattern, '');

    if (extractContent.length < 95) {
      subContent = extractContent;
    } else {
      let idx = extractContent.indexOf('<br>');

      if (idx > 95) {
        subContent = extractContent.substr(0, 95) + '...';
      } else if (idx < 0) {
        subContent =
          extractContent.length > 95 ? extractContent.substr(0, 95) + '...' : extractContent;
      } else {
        let secondIdx = extractContent.indexOf('<br>', idx + 4);

        if (secondIdx > 95) {
          subContent = extractContent.substr(0, 95) + '...';
        } else if (secondIdx < 0) {
          subContent =
            extractContent.length > 95 ? extractContent.substr(0, 95) + '...' : extractContent;
        } else {
          subContent = extractContent.substr(0, secondIdx) + '...';
        }
      }
    }

    this.contentPreview = subContent;
  },
  methods: {
    next: function(e) {
      e.stopPropagation();
      this.currentNumber += 1;
    },
    prev: function(e) {
      e.stopPropagation();
      this.currentNumber -= 1;
    },
    showContent: function() {
      this.$emit('showModal', this.boardItem);
    },
    goMypage(suggest) {
      if (this.nickname == suggest.nickname) this.$router.push("/mypage");
      else this.$router.push({ name: "UserPage", params: { suggest } })
    },
    userSearch() {
      UserApi.requestGetUser(
        this.boardItem.nickname,
        (res) => {
          this.goMypage(res.data.object);
        },
        () => {}
      );
    },
  },
  mounted() {
    this.editor = new Editor({
      editable: false,
      content: this.contentPreview,
      extensions: [
        StarterKit,
        Paragraph.extend({
          parseHTML() {
            return [{ tag: 'div' }];
          },
          renderHTML({ HTMLAttributes }) {
            return [
              'div',
              mergeAttributes(this.options.HTMLAttributes, HTMLAttributes, HTMLAttributes, {
                class: 'feedContent',
              }),
              0,
            ];
          },
        }),
      ],
    });
  },
  beforeDestroyed() {
    this.editor.destroy();
  },
};
</script>

<style lang="scss">
.feedContent {
  word-break: break-all;
  padding-top: 0px !important;
}

.replyShowContentBtn {
  cursor: pointer;
}
</style>
