<template>
  <div>
    <div class="character-count" v-if="editor">{{ editor.getCharacterCount() }}/{{ limit }}</div>
    <div class="editor-div">
      <editor-content class="editorContent" :editor="editor" />
    </div>
    <div id="metaDataDiv" class="metadata-div" v-show="metaLoading" @click="goLink"></div>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import { mergeAttributes } from '@tiptap/core';
import { Editor, EditorContent } from '@tiptap/vue-2';
import StarterKit from '@tiptap/starter-kit';
import Image from '@tiptap/extension-image';
import CharacterCount from '@tiptap/extension-character-count';
import HardBreak from '@tiptap/extension-hard-break';
import Paragraph from '@tiptap/extension-paragraph';
import Link from '@tiptap/extension-link';
import http from '@/util/http-common.js';
import '@/components/css/feed/editor.scss';
import '@/components/css/feed/metadata.scss';
import '@/components/css/feed/tiptap2.scss';

export default {
  components: {
    EditorContent,
  },
  data() {
    return {
      url: '',
      metaLoading: false,
      editor: null,
      limit: 1000,
      regex: /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)/,
    };
  },
  watch: {
    url: async function(val, oldval) {
      if (val === oldval || !val) {
        this.metaLoading = false;
        return;
      }

      this.metaLoading = false;

      const element = document.querySelector('#metaDataDiv');
      while (element.hasChildNodes()) {
        element.removeChild(element.firstChild);
      }

      await http
        .get('/common/getMeta', { params: { url: val } })
        .then((res) => {
          if (res.data.data == 'success') {
            const imgE = document.createElement('img'); // + addeventlistener
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
          }
        })
        .catch((err) => {
          console.err(err);
        });

      this.metaLoading = true;
    },
  },
  mounted() {
    this.editor = new Editor({
      autofocus: 'start',
      extensions: [
        StarterKit,
        Image,
        Link,
        CharacterCount.configure({
          limit: this.limit,
        }),
        HardBreak.extend({
          addKeyboardShortcuts() {
            return {
              Enter: () => this.editor.commands.setHardBreak(),
            };
          },
        }),
        Paragraph.extend({
          parseHTML() {
            return [{ tag: 'div' }];
          },
          renderHTML({ HTMLAttributes }) {
            return ['div', mergeAttributes(this.options.HTMLAttributes, HTMLAttributes), 0];
          },
        }),
      ],
      // editable: this.isOK,
      onUpdate: () => {
        this.setBoardContent(this.editor.getHTML());
        let url = this.regex.exec(this.editor.getHTML());

        if (url) {
          this.url = url[0];
        } else {
          this.metaLoading = false;
          this.url = '';
        }
      },
    });
  },
  methods: {
    ...mapActions(['setBoardContent']),
    goLink() {
      window.open(this.url);
    },
  },
  beforeDestroy() {
    this.editor.destroy();
  },
};
</script>
