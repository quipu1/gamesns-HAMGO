<template>
  <div style="display : flex;" class="modal modal-container">
    <div class="overlay" @click="$emit('close-modal')" style="right:0px; bottom:0px;"></div>
    <div class="modal-res">
      <div id="modalScroll" class="modal-card" style="overflow:scroll;">
        <div class="feed-item" style="border: 0px;">
          <div class="top modalfeedTop">
            <div
              class="profile-image"
              :style="{ 'background-image': 'url(' + defaultProfile + ')' }"
            >
              <img :src="'http://localhost:8080/account/file/' + boardItem.uid" />
            </div>
            <div class="user-info">
              <div class="media-body">
                <p class="m-0 fname" style="padding-right: 10px">{{ boardItem.nickname }}</p>
                <p class="m-0 time">
                  {{ boardItem.createDate | moment('from', 'now') }}
                </p>
              </div>
            </div>
            <Dropdown v-if="mine" :boardItem="boardItem"/>
            <!-- 종료 버튼 -->
            <div class="close-icon" @click="$emit('close-modal')">
            <i class="fas fa-times"></i>
            </div>
          </div>
          <!-- 이미지-->
          <div class="mcardbox-item" v-if="imgShow">
            <div class="mimage-slider" style="padding: 0;">
              <div class="mslider" style="padding: 0;" v-if="img_src.length > 1">
                <button class="prev" @click="prev"><i class="black fas fa-chevron-left"></i></button>
                <button class="next" @click="next"><i class="black fas fa-chevron-right"></i></button>
              </div>
              <!-- 이미지 -->
              <div
                class="mffimg"
                style="padding: 0px;"
                v-for="number in [currentNumber]"
                v-bind:key="number"
                transition="fade"
              >
                <img alt="" :src="img_src[Math.abs(currentNumber) % img_src.length]" />
              </div>
            </div>
          </div>
          <!--/ cardbox-item -->
          <div style="mcontent">
            <div id="origin">
              <div>
                <editor-content v-if="boardItem.contents" :editor="editor" />
              </div>
              <div id="metaDataDiv" class="metadata-div" v-show="metaLoading" @click="goLink"></div>
              <div id="loading" class="metadata-div" v-if="!metaLoading && metaPresent">
                <div class="spinner"></div>
              </div>
            </div>
          </div>

          <div class="cardbox-base">
            <div class="likebox">
              <Like :boardItem="boardItem" />
              <Reply :boardItem="boardItem" :now_reply_num="now_reply_num" />
            </div>
            <div class="sharebox">
              <Sharelink :boardItem="boardItem" />
            </div>
          </div>
          <!--/ cardbox-base -->

          <!-- 댓글 및 댓글 입력 -->
          <div class="putting-up" style="border-bottom: 2px solid #f4f4f4;">
            <input
              id="replyInput"
              v-model="replyContent"
              class="modalfeeld-search__input"
              type="text"
              placeholder="댓글 입력"
              @keyup.enter="submitReply"
            />
            <button class="modalfeed-putting-up" @click="submitReply">
              게시
            </button>
          </div>
          <ul class="img-comment-list">
            <li class="list" v-for="reply in replyList" :key="reply.rid">
              <div class="small-user-img-div">
                <img
                  :src="'http://localhost:8080/account/file/' + reply.uid"
                  class="small-user-img"
                />
                <!-- 임의의 이미지가 들어가는거라, user의 프로필사진이 나오게 해야 함. -->
              </div>
              <div class="comment-text">
                <strong
                  ><a href="">{{ reply.nickname }}</a></strong
                >
                <p>{{ reply.content }}</p>
                <span class="date sub-text">
                  {{ reply.regDate | moment('from', 'now') }}
                </span>
              </div>
              <a v-if="uid == reply.uid" class="modalcomment-delete-btn" @click="deleteReply(reply.rid)" style="color: #CD5C5C;" >삭제</a>
            </li>
          </ul>
        </div>
      </div>
      <div>
        <link
          href="//netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css"
          rel="stylesheet"
          id="bootstrap-css"
        />
        <link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet" />
      </div>
    </div>
  </div>
</template>
<script>
import defaultProfile from '../../assets/images/profile_default.png';
import { Editor, EditorContent } from '@tiptap/vue-2';
import StarterKit from '@tiptap/starter-kit';
import Image from '@tiptap/extension-image';
import Link from '@tiptap/extension-link';
import Paragraph from '@tiptap/extension-paragraph';
import { mergeAttributes } from '@tiptap/core';
import http from '@/util/http-common.js';
import UserApi from '../../api/UserApi';
import Sharelink from './Sharelink';
import Like from './Like';
import Reply from './Reply';
import Dropdown from './Dropdown';
import Vue from 'vue';
import vueMoment from 'vue-moment';
import moment from 'moment';
import '@/components/css/feed/metadata.scss';

moment.locale('ko');

Vue.use(vueMoment, { moment });

var timer;

export default {
  //components: { Input },
  props: ['boardItem'],
  components: {
    EditorContent,
    Sharelink,
    Like,
    Reply,
    Dropdown,
  },
  data: () => {
    return {
      imgShow: false,
      metaLoading: false,
      metaPresent: false,
      defaultProfile,
      img_src: [],
      replyContent: '',
      nickname: '',
      isModalViewed: false,
      currentNumber: 0,
      editor: null,
      newData: null,
      url: '',
      replyList: [],
      now_reply_num: 0,
      mine: 0,
    };
  },
  mounted() {
    this.newData = this.boardItem.contents;
    this.editor = new Editor({
      editable: false,
      content: this.boardItem.contents,
      extensions: [
        StarterKit,
        Image,
        Link,
        Paragraph.extend({
          parseHTML() {
            return [{ tag: 'div' }];
          },
          renderHTML({ HTMLAttributes }) {
            return [
              'div',
              mergeAttributes(this.options.HTMLAttributes, HTMLAttributes, HTMLAttributes, {
                class: 'modalContent',
              }),
              0,
            ];
          },
        }),
      ],
    });

    const $modalScroll = document.querySelector('#modalScroll');
    $modalScroll.addEventListener('scroll', (e) => this.handleScroll(e));
  },
  created() {
    this.uid = this.$store.state.uid;
    if (this.uid == this.boardItem.uid) {
      this.mine = true
    }

    this.boardItem.imgFiles.forEach((element) => {
      this.img_src.push('http://localhost:8080/board/file/' + element.file_name);
    });

    if (this.img_src.length == 0 || !this.img_src) {
      this.imgShow = false;
    } else {
      this.imgShow = true;
    }

    this.replyList = [];
    this.getReplyList();
    this.nickname = this.$store.state.nickname;

    this.getMetaData();
  },
  methods: {
    goLink() {
      window.open(this.url);
    },
    async getMetaData() {
      const regex = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)/;
      let getUrl = regex.exec(this.boardItem.contents);

      if (getUrl) {
        this.metaPresent = true;

        await http
          .get('/common/getMeta', { params: { url: getUrl[0] } })
          .then((res) => {
            if (res.data.data == 'success') {
              this.url = res.data.object.url;

              const element = document.querySelector('#metaDataDiv');
              const imgE = document.createElement('img');
              imgE.src = res.data.object.img;

              const contentDiv = document.createElement('div');
              contentDiv.setAttribute('class', 'metaContent');

              const title = document.createElement('div');
              title.appendChild(document.createTextNode(res.data.object.title));
              title.setAttribute('class', 'metaTitle');
              const descE = document.createElement('div');
              descE.appendChild(document.createTextNode(res.data.object.desc));
              descE.setAttribute('class', 'metaDesc');

              contentDiv.appendChild(title);
              contentDiv.appendChild(descE);

              element.appendChild(imgE);
              element.appendChild(contentDiv);
              this.metaLoading = true;
            }
          })
          .catch((err) => {
            console.error(err);
          });
      } else {
        this.metaPresent = false;
      }
    },

    submitReply() {
      let data = {
        uid: this.$store.state.uid,
        bid: this.boardItem.bid,
        content: this.replyContent,
      };
      UserApi.requestAddReply(
        data,
        () => {
          this.replyContent = '';

          alert('댓글 작성이 성공하였습니다.');

          this.getReplyList();
          let data2 = {
            bid: this.boardItem.bid,
          };
          http
            .get('/reply/cnt', { params: data2 })
            .then(({ data }) => {
              this.now_reply_num = data;
              this.$emit('replyLengthUpdate',{ bid: this.boardItem.bid, len : data });
            })
            .catch(() => {
              console.log('reply num 에러입니다');
            });
        },
        () => {
          alert('댓글 가져오기 오류!');
        }
      );
    },
    getReplyList() {
      let data;
      if (this.replyList.length == 0) {
        data = {
          bid: this.boardItem.bid,
          lastRid: 0,
        };
      } else {
        data = {
          bid: this.boardItem.bid,
          lastRid: this.replyList[this.replyList.length - 1].rid,
        };
      }
      UserApi.requestReplyList(
        data,
        (list) => {
          this.replyList = this.replyList.concat(list);
        },
        () => {
          alert('댓글 가져오기 오류!');
        }
      );
    },
    deleteReply(rid){
      let data = {
        rid : rid,
        uid : this.uid,
      }
      let divdiv = document.getElementsByClassName("modal-card");
      divdiv[0].scrollTop = 0;
      
      UserApi.requestDeleteReply(
        data,
        (res) => {
          if(res.status){
            this.replyList = [];
            this.getReplyList();
          } else{
            alert('댓글 삭제 오류!');
          }
        },
        () => {
          alert('댓글 삭제 오류!');
        }
      );
    },
    handleScroll(e) {
      if (
        parseInt(e.target.scrollHeight) == parseInt(e.target.scrollTop + e.target.clientHeight) &&
        parseInt(e.target.scrollHeight) != 0
      ) {
        if (timer == null) {
          this.getReplyList();
          timer = setTimeout(function() {
            timer = null;
          }, 300);
        }
      }
    },
    next: function(e) {
      e.stopPropagation();
      this.currentNumber += 1;
    },
    prev: function(e) {
      e.stopPropagation();
      this.currentNumber -= 1;
    },
  },
  beforeDestroy() {
    this.replyList = [];
    this.editor.destroy();
  },
};
</script>
<style>
#origin li {
  display: list-item;
  list-style-type: disc;
  padding: 0;
  margin-left: 5px;
}

#loading {
  width: 100%;
  position: relative;
}
@keyframes spinner {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

#loading .spinner {
  content: '';
  box-sizing: border-box;
  position: relative;
  top: 50%;
  left: 50%;
  width: 64px;
  height: 64px;
  margin-top: -32px;
  margin-left: -32px;
  border-radius: 50%;
  border: 4px solid lightgrey;
  border-top-color: blue;
  animation: spinner 0.8s linear infinite;
}

.modal-container .likeBtn{
  margin-right: 15px;
}

.modalContent {
  margin: 15px 10px;
}

.modalfeedTop {
  position: sticky;
  top: 0px;
  background-color: #fff;
  z-index: 99;
}

@import '../css/feed/modalfeed.css';
@import '../css/feed/modalfeed.scss';
</style>
