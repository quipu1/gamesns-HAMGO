<template>
  <div class="writing-total-container">
    <Header />
    <div class="Writing-top">
      <span>
        <label for="chooseFile" class="input-file-button">파일 업로드</label>
        <input
          v-on:change="fileChange($event.target.files)"
          type="file"
          name="file"
          id="chooseFile"
          multiple
          style="display:none"
          ref="fileupload"
        />
      </span>
      <span>
        <button class="finish-button" @click.prevent="registBoard()">작성 완료</button>
      </span>
    </div>
    <div class="Writing-container">
      <b-container fluid>
        <div class="imgPreviewDiv" v-if="uploadImgCnt > 0">
          <div class="internalImgPreviewDiv" v-for="(file, index) in files" :key="index">
            <div class="fileDeleteButton" @click="fileDelete(file.number)">
              <i class="fas fa-times-circle red"></i>
            </div>
            <img v-bind:src="file.preview" style="height:100%; width: 100%;" />
          </div>
        </div>
        <editor />
        <textarea
          name="hashtag-input"
          id="hashtag-input"
          placeholder="#해시태그를입력하세요"
          v-model="hashtags"
        ></textarea>
      </b-container>
    </div>
    <div>
      <link href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css" rel="stylesheet" />
    </div>
    <Footer />
  </div>
</template>

<script>
import Header from '@/components/layout/header/Header.vue';
import Footer from '@/components/layout/footer/Footer.vue';
import { mapActions, mapGetters } from 'vuex';
import Editor from './Editor';

export default {
  name: 'Writing',
  components: {
    Header,
    Footer,
    Editor,
  },
  data() {
    return {
      uid: '',
      nickname: '',
      files: [],
      filesSize: 0,
      uploadImgCnt: 0,
      content: '',
      metaData: null,
      hashtags: '',
    };
  },
  created() {
    this.uid = this.$store.state.uid;
    this.nickname = this.$store.state.nickname;
  },
  methods: {
    ...mapActions(['addBoard', 'setBoardContent']),
    fileChange(fileList) {
      let num = -1;

      fileList.forEach((file, index) => {
        // 전체 이미지의 용량이 5MB를 초과한다면
        if (this.filesSize + file.size > 5242880) {
          alert('이미지는 최대 5MB 까지 업로드 가능합니다.\n다시 시도해주세요.');
          return false;
        }

        this.files = [
          ...this.files,
          {
            file: file,
            preview: URL.createObjectURL(file),
            number: index + this.uploadImgCnt,
          },
        ];

        this.filesSize += file.size;
        num = index;
      });

      console.log(this.files);

      if (num > -1) {
        this.uploadImgCnt = this.uploadImgCnt + num + 1;
      }

      // 파일업로더 값을 초기화하여 동일 이미지를 올려도 올라갈 수 있게함
      this.$refs.fileupload.value = null;
    },
    fileDelete(val) {
      this.files = this.files.filter((data) => data.number !== Number(val));
      this.uploadImgCnt -= 1;
    },
    registBoard() {
      let formData = new FormData();

      formData.append('uid', this.uid);
      formData.append('content', this.boardContent);
      formData.append('hashtags', this.hashtags);

      this.files.forEach((element) => {
        formData.append('multipartFiles', element.file);
      });

      for (let key of formData.entries()) {
        console.log(`${key}`);
      }

      this.setBoardContent('');

      this.addBoard(formData);
    },
    // registHashtag() {
    //   let hashtagData = new hashtagData();

    //   hashtagData.append('hashtags',this.hashtags)
    //   this.addHashtag(this.hashtags);
    // }
  },
  computed: {
    ...mapGetters(['boardContent']),
  },
};
</script>

<style>
@import '../../css/home/writing/writing.css';
@import '../../css/home/writing/writing.scss';
</style>
