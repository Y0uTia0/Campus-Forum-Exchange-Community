<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {reactive, ref, computed} from "vue";
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import ImageResize from "quill-image-resize-vue";
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module";
import axios from "axios";
import {accessHeader, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import {get} from "@/net";
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store/index.js";


const props = defineProps({
  show: Boolean,
  defaultTitle: {
    default: '',
    type: String
  },
  defaultText: {
    default: '',
    type: String
  },
  defaultType: {
    default: null,
    type: Number
  },
  submitButton: {
    default: '立即发表主题',
    type: String
  },
  submit: {
    default: (editor, success) => {
      post('/api/forum/create-topic', {
        type: editor.type.id,
        title: editor.title,
        content: editor.text
      }, () => {
        ElMessage.success("帖子发表成功!")
        success()
      })
    },
    type: Function
  }
})

Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)

const emit = defineEmits(['close', 'success'])
const store = useStore()

const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false,
  uploading: false
});

const editorOptions = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike", "clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        {header: [1, 2, 3, 4, 5, 6, false]},
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        {indent: '-1'}, {indent: '+1'}
      ],
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: ['Resize', 'DisplaySize']
    },
    ImageExtend: {
      action: axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png,image/jpeg',
      response: (resp) => {
        if (resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);
      },
      start: () => editor.uploading = true,
      success: () => {
        ElMessage.success('图片上传成功!')
        editor.uploading = false
      },
      error: () => {
        ElMessage.warning('图片上传失败!')
        editor.uploading = false
      }
    }
  }
}

const refEditor = ref()

function initEditor() {
  if (props.defaultText)
    editor.text = new Delta(JSON.parse(props.defaultText))
  else
    refEditor.value.setContents('', 'user')
  editor.title = props.defaultTitle
  editor.type = findTypeById(props.defaultType) || null;
}

function deltaToText(delta) {
  if (!delta?.ops) return "";
  let str = "";
  for (const op of delta.ops) {
    if (typeof op.insert === 'string') str += op.insert;
  }
  return str.replace(/\s/g, "");
}

function findTypeById(id) {
  for (let type of store.forum.types) {
    if (type.id == id)
      return type
  }
}

const contentLength = computed(() => deltaToText(editor.text).length)

function submitTopic() {
  const text = deltaToText(editor.text)
  if (text.length > 20000) {
    ElMessage.warning('字数超出限制,无法发布主题!')
    return
  }
  if (!editor.title) {
    ElMessage.warning('请填写标题!')
    return
  }
  if (!editor.type) {
    ElMessage.warning('请选择一个合适的帖子类型!')
    return
  }
  props.submit(editor,( ) =>emit('success'))

}

</script>

<template>
    <el-drawer :model-value="show"
               direction="btt"
               @open="initEditor"
               :close-on-click-modal="false"
               :size="1000"
               @close="emit('close')">
      <template #header>
        <div>
          <div style="font-weight: bold">发表新帖子</div>
          <div style="font-size: 13px">发表内容之前,请遵守相关法律法规,不要出现骂人等不文明行为.</div>
        </div>
      </template>
      <div style="display: flex;gap: 10px">
        <div style="width: 160px">
          <el-select placeholder="请选择主题类型..." value-key="id" v-model="editor.type"
                     :disabled="!store.forum.types.length">
            <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item" :label="item.name">
              <div>
                <color-dot :color="item.color"/>
                <span style="margin-left: 10px">{{ item.name }}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title" placeholder="请输入帖子标题..." :prefix-icon="Document"
                    style="height: 100%" maxlength="30"/>
        </div>
      </div>
      <div style="margin-top: 5px;font-size: 13px;color: grey">
        <color-dot :color="editor.type ? editor.type.color : '#dedede'"/>
        <span style="margin-left: 5px">{{ editor.type ? editor.type.desc : '请在上方选择一个帖子类型!' }}</span>
      </div>
      <div style="margin-top: 10px;height: 440px;overflow: hidden;border-radius: 5px" v-loading="editor.uploading"
           element-loading-text="正在上传图片,请稍后...">
        <quill-editor v-model:content="editor.text" style="height: calc(100% - 45px)"
                      content-type="delta" ref="refEditor"
                      placeholder="今天想分享点什么呢?" :options="editorOptions"/>
      </div>
      <div style="display: flex;justify-content: space-between;margin-top: 5px">
        <div style="color: grey;font-size: 13px">
          当前字数 {{ contentLength }} (最大支持20000字)
        </div>
        <div>
          <el-button type="success" plain :icon="Check" @click="submitTopic">{{ submitButton }}</el-button>
        </div>
      </div>
    </el-drawer>
</template>

<style scoped>
:deep(.el-drawer) {
  width: 1000px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}

:deep(.el-drawer__header) {
  margin: 0;
}




</style>