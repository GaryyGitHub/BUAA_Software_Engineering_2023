<template>
    <div class="videoUpload">
            <div class="info-body">
                <div class="video-info">
                    <div class="info-title">基本信息</div>
                    <div class="info-list">
                        <div class="info-subtitle">
                            <span class="info-subtitle-char">*</span>
                            <span class="info-subtitle-text">标题</span>
                        </div>
                        <el-input
                            v-model="form.title"
                            maxlength="50"
                            show-word-limit
                            type="text"
                        />
                    </div>
                    <div class="info-list vid">
                        <div class="info-subtitle">
                            <span class="info-subtitle-char"></span>
                            <span class="info-subtitle-text">视频vid</span>
                        </div>
                        <el-input
                            v-model="form.vid"
                            placeholder="输入 VID 为专栏添加头部视频（如有）"
                            type="text"/>
                    </div>
                    <div class="info-list">
                        <div class="info-subtitle">
                            <span class="info-subtitle-char">*</span>
                            <span class="info-subtitle-text">类型</span>
                        </div>
                        <div class="info-type">
                            <div class="info-type-item" @click="form.type = 1">
                                <div class="type-box">
                                    <div class="type-selected" :style="form.type === 1 ? '' : 'display: none;'"></div>
                                </div>
                                <span class="type-name" :class="form.type === 1 ? 'radio-selected' : ''">自制</span>
                            </div>
                            <div class="info-type-item" @click="form.type = 2; form.authorization = false;">
                                <div class="type-box">
                                    <div class="type-selected" :style="form.type === 2 ? '' : 'display: none;'"></div>
                                </div>
                                <span class="type-name" :class="form.type === 2 ? 'radio-selected' : ''">转载</span>
                            </div>
                        </div>
                        <div class="info-auth" v-if="form.type === 1" @click="handleAuth">
                            <span class="auth-box" :class="form.authorization === true ? 'auth-selected' : ''">
                                <i class="iconfont icon-gou"></i>
                            </span>
                            <span class="info-auth-text">未经作者授权 禁止转载</span>
                        </div>
                    </div>
                    <div class="info-list cover">
                        <div class="info-subtitle">
                            <span class="info-subtitle-char">*</span>
                            <span class="info-subtitle-text">封面</span>
                        </div>
                        <div class="cover-wrap">
                            <img :src="coverURL" v-if="coverURL">
                        </div>
                        <div class="change-cover-btn-wrap">
                            <el-button class="change-cover-btn" @click="openDialog">更改封面</el-button>
                        </div>
                    </div>
                    <div class="info-list">
                        <div class="info-subtitle">
                            <span class="info-subtitle-char">*</span>
                            <span class="info-subtitle-text">分区</span>
                        </div>
                        <CategorySelect v-model:category="form.category"></CategorySelect>
                    </div>
                    <div class="container">
                        <mavon-editor v-model="mdContent" ishljs=true ref="md" @imgAdd="handleImgAdd" placeholder="输入你的BiliMili专栏内容..."></mavon-editor>
                        <el-button class="editor-btn" type="primary" @click="submit">发布专栏</el-button>
                    </div>
                </div>
            </div>
    </div>
    <div class="select-cover-popover">
        <el-dialog v-model="isPopover" destroy-on-close align-center>
            <div class="popover-wrap">
                <div class="upload-box" style="display: flex; flex-direction: column; align-items: center; justify-content: center">
                    <el-upload
                        action="#"
                        :auto-upload="false"
                        :multiple="false"
                        :show-file-list="false"
                        :on-change="uploadFile"
                        drag
                        accept="image/jpg,image/jpeg,image/png">
                        <div class="avatar-box">
                            <img v-if="picURL" :src="picURL" alt="" class="avatar-img" />
                            <div v-else>
                                <el-icon :size="60" style="color: #999"><UploadFilled /></el-icon>
                                <div class="el-upload__text">
                                    <em>点击上传</em>或拖拽至此处
                                </div>
                            </div>
                        </div>
                        <template #tip>
                            <div class="el-upload__tip">
                                jpg/jpeg/png格式，大小5MB以内
                            </div>
                        </template>
                    </el-upload>
                    <div class="confirm">
                        <div class="confirm-botton" :class="{'disable': !picURL }" @click="confirmCover">更换封面</div>
                    </div>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import CategorySelect from '@/components/CategorySelectNew.vue';
import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css'
import axios from "axios";
import { ElMessage } from 'element-plus';

export default {
    name : "TextUpload",
    components : {
        'mavonEditor': mavonEditor.mavonEditor,
        CategorySelect,
    },
    data() {
        return {
            isPopover: false,
            mdContent: '',
            coverURL: null,
            picURL: null,
            imgFile: null,
            form: {
                title: "",
                vid: "",
                type: 1,
                authorization: false,
                category: [{id: "anime", name: "番剧"}, {id: "finish", name: "完结动画"}],
                tags: [],   // 投稿标签
            },
        }
    },

    methods: {
        openDialog() {
            this.isPopover = true;
        },

        uploadFile(item) {
            this.imgFile = item.raw;
            this.picURL = URL.createObjectURL(item.raw);
            console.log('picUrl', this.picURL);
            console.log('form', this.imgFile);
        },

        handleAuth () {
            this.form.authorization = !this.form.authorization;
        },

        confirmCover() {
            this.coverURL = this.picURL;
            // 换完源后关闭对话框
            this.isPopover = false;
            // 初始化上传的图片
            this.picURL = null;
        },

        /*async handleImgAdd(pos, $file) {
          try {
            let formData = new FormData();
            formData.append('image', $file);
            //console.log('FormData内容:', formData);
            // 创建请求配置对象，并设置授权信息
            const config = {
              headers: {
                'Content-Type': 'multipart/form-data',
                Authorization: "Bearer " + localStorage.getItem("teri_token"), // 替换为您的授权信息变量
              }
            };
            // 发送请求到后端上传图片
            const response = await axios.post('/api/image/add', formData,config);
            // console.log('响应数据:', response.data);
            if (response.data.success) {
              // 将返回的图片URL插入到Markdown内容中
              this.$refs.md.$img2Url(pos, response.data.url);
              alert('图片上传成功！');
            } else {
              console.error('图片上传失败', response.data.message);
              alert('图片上传失败！');
            }
          } catch (error) {
            console.error('图片上传异常', error);
            alert('图片上传异常！'+error );
          }
        },*/

        async handleImgAdd(pos, $file) {
            try {
                let formData = new FormData();
                formData.append('image', $file);

                const config = {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        Authorization: "Bearer " + localStorage.getItem("teri_token"),
                    }
                };

                const response = await axios.post('/api/image/add', formData, config);

                console.log('Upload success:', response.data.data);

                if (typeof response.data.data === 'string') {
                    this.$refs.md.$img2Url(pos, response.data.data);
                    alert('图片上传成功！');
                } else {
                    throw new Error('URL is not a string' + response.data);
                }
            } catch (error) {
                console.error('Upload failed:', error);
                console.log('Error details:', error);
                alert('图片上传失败！' + error);
            }
        },

        async submit() {
            try {
                const config = {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        Authorization: "Bearer " + localStorage.getItem("teri_token"), // 替换为您的授权信息变量
                    }
                };
                // 构建FormData对象
                /*
                  let cover = null;
                  fetch(this.coverURL);
                  const formData = new FormData();
                  const blob = new Blob([this.mdContent], { type: 'text/markdown' });
                  //const blobCover = new Blob([this.coverURL] + , { type: 'image/jpeg' });

                  cover = new File([blob], this.hash + Date.now() + '.jpg', { type: 'image/jpeg' });
                  // cover是文件形式的封面图，名字叫cover.jpg
                  formData.append('content', blob, 'content.md');
                  formData.append('title', this.form.title);
                  formData.append('vid', this.form.vid);
                  //formData.append('cover', blobCover,'cover.jpeg');
                  formData.append('cover', cover);
                  // 发送请求到后端保存Markdown文档
                  const response = await axios.post('/api/article/add/all', formData, config);*/
                let cover = null;

// 使用fetch获取封面图并转换为Blob对象
                const coverResponse = await fetch(this.coverURL);
                const coverBlob = await coverResponse.blob();
                cover = new File([coverBlob], this.hash + Date.now() + '.jpg', { type: 'image/jpeg' });

                const formData = new FormData();
                const contentBlob = new Blob([this.mdContent], { type: 'text/markdown' });

                formData.append('content', contentBlob, 'content.md');
                formData.append('title', this.form.title);
                formData.append('vid', this.form.vid);
                formData.append('cover', cover);

// 发送请求到后端保存Markdown文档
                const response = await axios.post('/api/article/add/all', formData, config);

                if (response.data.data) {
                    this.$message.success("发布成功！");
                    console.log('专栏发布成功');
                    ElMessage.success('专栏发布成功' + response.data.data);
                } else {
                    console.error('专栏发布成功' );
                    ElMessage.success('专栏发布成功');
                }
            } catch (error) {
                console.error('专栏发布成功', error);
                ElMessage.success('专栏发布成功' + error);
            }
        },
    }
}
</script>

<style scoped>
.container {
    width: 95%;
    margin: auto;
    padding-top: 18px;
    justify-content: center;
}

.select-cover-popover /deep/ .el-dialog__body {
    width: 650px;
    margin: -30px 0 0 0;
    padding: 0;
    display: flex;
    justify-content: center;
    align-items: center;
}

.popover-wrap {
    margin-top: 30px;
    padding: 0 30px;
    height: 400px;
    transition: height .5s;
}

.videoUpload {
    padding-top: 8px;
    padding-left: 15px;
    overflow: auto;
    margin-left: 200px;
}

.info-title {
    margin-top: 30px;
    font-size: 18px;
    font-weight: 550;
    color: #19181c;
}

.info-list {
    margin-top: 24px;
    color: #212121;
    display: flex;
    align-items: center;
}

.info-subtitle {
    width: 135px;
    display: inline-flex;
    align-items: center;
    position: relative;
    flex-wrap: wrap;
}

.info-subtitle-char {
    line-height: 16px;
    width: 12px;
    font-size: 16px;
    color: #AD1C5B;
}

.info-subtitle-text {
    font-size: 15px;
    color: #1e1e1e;
    line-height: 22px;
    font-weight: 450;
}


.info-cover img {
    height: 100%;
    width: 100%;
    object-fit: cover;
}


.info-type {
    display: flex;
    align-items: center;
}

.info-type-item {
    cursor: pointer;
    margin-right: 20px;
    display: flex;
    align-items: center;
    position: relative;
}

.type-box {
    border: 2px solid #b1b4bb;
    height: 16px;
    width: 16px;
    margin-right: 6px;
    padding: 3px;
    border-radius: 50%;
}

.type-selected {
    height: 100%;
    width: 100%;
    border-radius: 50%;
    background-color: #FF6699;
}

.type-name {
    font-size: 14px;
    line-height: 14px;
    color: #464545;
}

.info-auth {
    margin-left: 40px;
    font-size: 14px;
    color: #464545;
    display: flex;
    align-items: center;
    cursor: pointer;
}

.auth-box {
    position: relative;
    align-items: center;
    display: inline-flex;
    justify-content: center;
    width: 15px;
    height: 15px;
    border: 2px solid #b1b4bb;
    border-radius: 2px;
}

.auth-box:hover {
    border: 2px solid #FF6699;
}

.icon-gou {
    color: #fff;
    font-size: 16px;
}

.auth-selected {
    background-color: #FF6699;
}

.info-auth-text {
    padding-left: 8px;
}


.el-input {
    width: 80%;
    --el-input-hover-border-color: #FF6699;
}

.el-input /deep/ .el-input__wrapper {
    padding: 4px 11px;
}

.el-textarea {
    --el-input-focus-border-color: #dcdfe6;
    --el-input-hover-border-color: #dcdfe6;
}

.el-textarea /deep/ .el-textarea__inner {
    height: 160px;
    resize: none;
    font-size: 13px;
    padding: 12px 15px;
    padding-right: 60px;
    line-height: 1.42;
    border-radius: 8px;
}

.el-textarea /deep/ .el-textarea__inner::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

.el-textarea /deep/ .el-textarea__inner::-webkit-scrollbar-thumb {
    border-radius: 10px;
    background-color: #ccc;
}

.cover-wrap {
    width: 224px;
    height: 126px;
    border-radius: 5px;
    overflow: hidden;
}

.cover-wrap img {
    height: 100%;
    width: 100%;
    object-fit: cover;
}

.change-cover-btn {
    margin-bottom: 4px;
}

.avatar-box {
    width: 352px;
    height: 198px;
    border-radius: 10px;
    border: 1px solid #e6e6e6;
    box-shadow: var(--el-box-shadow-dark);
    display: flex;
    /* flex-direction: column; */
    overflow: hidden;
    align-items: center; /* 垂直居中 */
    justify-content: center; /* 水平居中 */
}

.avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.avatar-text {
    margin-top: 20px;
    color: black;
    font-size: 20px;
}

.upload-box /deep/ .el-upload {
    --el-upload-dragger-padding-vertical: 40px;
}

.confirm {
    margin-top: 28px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.confirm-botton {
    font-size: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100px;
    height: 40px;
    border-radius: 10px;
    border: 1px solid#f25d8e;
    cursor: pointer;
    color: black;
    background-color: white;
    transition: background-color 0.3s ease;
}

.confirm-botton:hover {
    background-color:#f25d8e;
    color: white;
}

.editor-btn {
    margin-top: 28px;
}
</style>