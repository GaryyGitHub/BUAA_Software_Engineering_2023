<template>
    <div class="videoUpload">
        <div class="before-upload-wrap" :style="selected ? 'display: none' : ''">
            <div class="before-upload-content-wrap">
                <div class="before-upload-video">
                    <div
                        class="video-input-wrap"
                        @drag.prevent
                        @drop.prevent
                        @drop="handleDrop"
                        @click="selectVideo"
                    >
                        <el-icon size="40"><UploadFilled /></el-icon>
                        <div class="warning-context">
                            <span>你的视频在发布之前将处于私享状态。</span>
                        </div>
                        <div class="upload-btn">选择视频</div>
                        <input
                            type="file"
                            accept=".mp4"
                            ref="videoInput"
                            @change="handleChange"
                            style="display :none;"
                        >
                    </div>
                </div>
            </div>
            <div class="warning-context">
                <span>向 BiliMili 提交视频，即表示你确认同意 BiliMili 的<a target="_blank">服务条款</a>和<a target="_blank">社区准则</a>。</span>
            </div>
            <div class="warning-context">
                <span>请勿侵犯他人的版权或隐私权。 <a target="_blank">服务条款</a></span>
            </div>
        </div>

        <div class="after-upload-wrap" :style="selected ? '' : 'display: none;'">
            <div class="video-info-bar">
                <span>发布视频</span>
                <div class="cancel-btn" @click="handleCancel">取消发布</div>
            </div>
            <div class="info-body">
                <div class="file-name">文件名：{{ videoName }}</div>
                <div class="upload-progress-wrap">
                    <div class="upload-detail">
                        <div class="upload-progress-info">
                            <div class="progress-status" v-if="progress === 100">
                                <i class="iconfont icon-wancheng success"></i>
                                <span class="success">分片上传完成</span>
                            </div>
                            <div class="progress-status" v-else-if="isFailed">
                                <i class="iconfont icon-shibai failed"></i>
                                <span class="failed">分片上传失败</span>
                            </div>
                            <div class="progress-status" v-else-if="!isPaused">
                                <i class="iconfont icon-shangchuanzhong"></i>
                                <span class="status">上传中</span>
                                <span>{{ progress }}%</span>
                            </div>
                            <div class="file-status-manage refresh" v-if="progress === 100 || isPaused" @click="selectVideo">
                                <i class="iconfont icon-genghuan"></i>
                                <span>更换视频</span>
                            </div>
                        </div>
                        <div class="upload-progress">
                            <div class="file-progress-duration">
                                <div class="file-progress-current" :class="progress ===100 ? 'success-bg' : isFailed ? 'failed-bg' : 'doing-bg'"
                                     :style="`width: ${progress}%;`"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="video-info">
                    <div class="info-title">基本信息</div>
                    <div class="info-list">
                        <div class="info-subtitle">
                            <span class="info-subtitle-char"></span>
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
                            <span class="info-subtitle-text">标题</span>
                        </div>
                        <el-input
                            v-model="form.title"
                            maxlength="50"
                            show-word-limit
                            type="text"
                        />
                    </div>
                    <div class="info-list" style="align-items: flex-start;">
                        <div class="info-subtitle" style="padding-top: 30px;">
                            <span class="info-subtitle-char"></span>
                            <span class="info-subtitle-text">说明</span>
                        </div>
                        <el-input
                            class="desc-text"
                            v-model="form.description"
                            maxlength="200"
                            placeholder="向观看者介绍你的视频"
                            show-word-limit
                            type="textarea"
                        ></el-input>
                    </div>
                    <div class="info-title">视频属性</div>
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

                    <div class="info-list">
                        <div class="info-subtitle">
                            <span class="info-subtitle-char">*</span>
                            <span class="info-subtitle-text">分区</span>
                        </div>
                        <CategorySelect v-model:category="form.category"></CategorySelect>
                    </div>

                    <div class="info-list">
                        <div class="upload-submit-wrap">
                            <span class="upload-submit-btn" @click="handleSubmit">提交投稿</span>
                        </div>
                    </div>
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
    <video :src="videoURL" ref="VideoInfo" style="display: none"></video>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import SparkMD5 from 'spark-md5';
import CategorySelect from '@/components/CategorySelectNew.vue';

export default {
    name : "VideoUpload",
    components : {
        CategorySelect,
    },
    data() {
        return {
            isPopover: false,
            selected: null,
            videoName: "",
            progress: 0,
            isFailed: false,
            isPaused: false,
            isCanceled: false,
            form: {
                title: "",
                type: 1,
                authorization: false,
                category: [{id: "anime", name: "番剧"}, {id: "finish", name: "完结动画"}],
                tag: [],
                description: "",
            },
            videoURL: null,
            videoHash: null,
            chunkCurrent: 0,
            imgURL: null,
            coverPer: 0.0,
            coverURL: null,
            picURL: null,
            imgFile: null,
            duration: 0,
        }
    },

    computed : {
        categoryList() {
            return this.store.state.channels;
        }
    },

    methods: {
        videoInfoInit(file) {
            const video = this.$refs.VideoInfo;
            this.videoURL = URL.createObjectURL(file);
            video.oncanplay = async() => {
                this.duration = video.duration;
            };
            console.log("duration", video.duration);
        },

        openDialog() {
            this.isPopover = true;
        },

        uploadFile(item) {
            this.imgFile = item.raw;
            this.picURL = URL.createObjectURL(item.raw);
            console.log('picUrl', this.picURL);
            console.log('form', this.imgFile);
        },

        confirmCover() {
            this.coverURL = this.picURL;
            // 换完源后关闭对话框
            this.isPopover = false;
            // 初始化上传的图片
            this.picURL = null;
        },

        videoInit() {
            this.selected = null;
            this.videoName = "";
            this.progress = 0;
            this.coverURL = null;
            this.form = {
                title: "",
                type: 1,
                authorization: false,
                category: [{id: "anime", name: "番剧"}, {id: "finish", name: "完结动画"}],
                tag: [],
                description: ""
            }
            this.videoURL = null;
            this.videoHash = null;
            this.imgURL = null;
            this.coverPer = 0.0;
            this.picURL = null;
            this.duration = 0;
        },

        selectVideo() {
            this.videoURL = null;
            this.$refs.videoInput.click();
        },


        async handleFile(event, isDrop = false) {
            if (!this.$store.state.isLogin) {
                ElMessage.error('请登录后查看');
                this.$store.dispatch("logout");
                this.$router.push("/");
                return;
            }

            const file = isDrop ? event.dataTransfer.files[0] : event.target.files[0];
            const maxSize = 200 * 1024 * 1024;

            if(!file) {
                return;
            }

            if(isDrop && file.type !== "video/mp4") {
                ElMessage.error("请以MP4格式上传视频");
                return;
            }

            if(file.size <= maxSize) {
                this.isPaused = false;
                this.selected = file;
                this.coverPer = 0.0;
                this.picURL = null;
                this.videoName = file.name.replace(".mp4", "");
                this.form.title = this.videoName;
                this.$emit("changeNavBarShow", false);
                this.videoInfoInit(file);
                this.videoHash = await this.flash(this.selected);
                await this.upload();
            } else {
                ElMessage.error("视频过大，请调整码率");
            }
        },

        // videoInfoInit(file) {
            
        // },
        
        handleChange(event) {
            this.handleFile(event);
        },

        handleDrop(event) {
            event.preventDefault();
            this.handleFile(event, true);
        },

        handleCancel () {
            ElMessageBox.confirm(
                '当前视频详细信息将被清空，确定取消发布？',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            )
                .then(this.cancel);
        },

        async cancel() {
            this.isPaused = true;
            this.isCanceled = true;
            await this.cancelUpload(this.videoHash);
            this.videoInit();
            setTimeout(() => {
                this.isCancel = false;
            }, 3000);
            this.$emit("changeNavBarShow", true);
        },

        handleAuth () {
            this.form.authorization = !this.form.authorization;
        },

        async submit() {
            try {
                // 判断分片上传情况、表单填写情况
                if (this.progress < 100) {
                    ElMessage.error('视频尚未上传完成');
                    return;
                }
                if (this.form.title.trim().length === 0) {
                    ElMessage.error('填写标题');
                    return;
                }

                this.$store.state.isLoading = true;

                const response = await fetch(this.coverURL);
                const blob = await response.blob();
                const cover = new File([blob], `${this.hash}${Date.now()}.jpg`, { type: 'image/jpeg' });

                let auth = this.form.authorization ? 1 : 0;

                const formData = new FormData();
                formData.append('cover', cover);
                formData.append('hash', this.videoHash);
                formData.append('title', this.form.title);
                formData.append('type', this.form.type);
                formData.append('auth', auth);
                formData.append('duration', this.duration);
                formData.append('mcid', this.form.category[0].id);
                formData.append('scid', this.form.category[1].id);
                formData.append('tags', "");
                formData.append('descr', this.form.description);

                const postResponse = await this.$post("/video/add", formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        Authorization: "Bearer " + localStorage.getItem("teri_token"),
                    }
                });

                if (postResponse.data.code === 200) {
                    ElMessage.success('视频投稿成功');
                    this.videoInit();
                    this.$emit("changeNavBarShow", true);
                } else {
                    ElMessage.error('投稿失败，请稍后再试');
                }
            } catch (error) {
                ElMessage.error('error');
            } finally {
                this.$store.state.isLoading = false;
            }
        },

        async submitMoment() {
            const formData = new FormData();
            formData.append("uid", this.$store.state.user.uid);
            formData.append("content", "新视频上线");

            const response = await this.$post("/post/add", formData, {       // 与后端同步！
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("teri_token"),
                }
            });
            if (!response.data) {
                return;
            }
            this.$emit("addDynamic", response.data.data);
        },

        async handleSubmit () {
            await this.submit();
            await this.submitMoment();
        },

        async upload() {
            const chunks = this.chunkInit(this.selected);
            this.isPaused = this.isFailed = false;

            try{
                const result = await this.getCurrentChunk(this.videoHash);
                this.chunkCurrent = result.data.data;

                while (this.chunkCurrent < chunks.length) {
                    if (this.isPaused) {
                        if (this.isCanceled) {
                            await this.cancelUpload(this.videoHash);
                            this.isCanceled = false;
                        }
                        return;
                    }

                    const chunk = chunks[this.chunkCurrent];
                    const formData = new FormData();
                    formData.append('chunk', chunk);
                    formData.append('hash', this.videoHash);
                    formData.append('index', this.chunkCurrent);

                    const res = await this.submitChunk(formData);

                    if (res.data.code !== 200) {
                        ElMessage.error('分片上传失败');
                    }

                    this.chunkCurrent++;
                    this.progress = Math.round(((this.chunkCurrent) / chunks.length) * 100);
                }
                this.progress = 100;
            } catch (e) {
                ElMessage.error(e.message || '分片上传失败');
                this.isFailed = true;
                this.isPaused = true;
            }
        },

        flash(file) {
            const spark = new SparkMD5();
            spark.append(file.name + file.size.toString());
            return Promise.resolve(spark.end());
        },

        chunkInit(file) {
            const CHUNK_SIZE = 10*1024*1024;
            return Array.from({ length: Math.ceil(file.size / CHUNK_SIZE) }, (_, i) =>
                file.slice(i * CHUNK_SIZE, (i + 1) * CHUNK_SIZE)
            );
        },

        async getCurrentChunk(hash) {
            return await this.$get("/video/ask-chunk", {
                params: { hash: hash },
                headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
            });
        },

        async submitChunk(formData) {
            return this.$post("/video/upload-chunk", formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: "Bearer " + localStorage.getItem("teri_token"),
                }
            });
        },

        async cancelUpload(hash) {
            return await this.$get("/video/cancel-upload", {
                params: { hash: hash },
                headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
            });
        },
    }
}
</script>

<style scoped>
.status {
    margin-right: 8px;
}

.videoUpload {
    margin-left: 200px;
    padding-top: 8px;
    overflow: auto;
}

.before-upload-wrap {
    margin: 0 auto;
    max-width: 850px;
    width: 80%;
}

.before-upload-content-wrap {
    margin-top: 20px;
    border: 2px dashed #ccc;
    width: 800px;
    height: 400px;
}

.before-upload-video {
    margin-top: 140px;
    margin-bottom: 30px;
    justify-content: center;
    text-align: center;
    display: flex;
    font-size: 13px;
    color: #797F87;
}

.video-input-wrap {
    padding-bottom: 20px;
    flex: 1;
    display: inline-block;
    cursor: pointer;

}

.warning-context {
    margin-top: 18px;
    text-align: center;
    color: #797F87;
    fill: #797F87;
    font-size: 12px;
    line-height: 20px;
}

.warning-context a {
    color: #FF6699;
    cursor: pointer;
}

.warning-context a:hover {
    color: #FF6699;
    text-decoration: underline;
}

.upload-btn {
    color: #FFFFFF;
    margin: 20px auto;
    width: 180px;
    height: 46px;
    cursor: pointer;
    background: #FF6699;
    border-radius: 4px;
    text-align: center;
    line-height: 44px;
    white-space: nowrap;
}

.after-upload-wrap {
    padding: 2px 30px;
}

.video-info-bar {
    box-shadow: 0 1px 0 #e7e7e7;
    display: flex;
    font-size: 18px;
    font-weight: 550;
    align-items: center;
    justify-content: space-between;
    color: #19181c;
    line-height: 28px;
    height: 70px;
    position: relative;
}

.cancel-btn {
    font-size: 14px;
    padding: 12px 16px;
    margin-right: 30px;
    border-radius: 4px;
    color: #61666D;
    border: 1px solid #e6eaf0;
    background-color: #ffffff;
    cursor: pointer;
    box-sizing: border-box;
    display: inline-block;
    line-height: 1;
    text-align: center;
}

.cancel-btn:hover {
    background-color: #FEF3F2;
}

.file-name {
    padding-top: 25px;
    color: #19181c;
    font-size: 14px;
}

.upload-progress-wrap {
    padding-top: 15px;
    width: 90%;
    display: flex;
}

.upload-detail {
    flex: 1;
}

.upload-progress-info {
    display: flex;
    padding-top: 5px;
}

.upload-progress-info .iconfont {
    font-size: 20px;
    margin-right: 8px;
}

.progress-status {
    flex: 1;
    height: 20px;
    line-height: 28px;
    font-size: 14px;
    display: flex;
    align-items: center;
    color: #797F87;
}

.success {
    color: #2AC864;
}

.failed {
    color: #E23D3D;
}

.success-bg {
    background-color: #2AC864;
}

.failed-bg {
    background-color: #E23D3D;
}

.doing-bg {
    background-color: #FF6699;
}

.file-status-manage {
    color: #FF6699;
    cursor: pointer;
    align-items: center;
    font-size: 14px;
    margin-left: 10px;
    display: flex;
}

.upload-progress {
    padding-top: 12px;
}

.file-progress-duration {
    height: 4px;
    width: 100%;
    background-color: #e6eaf0;
    border-radius: 4px;
}

.file-progress-current {
    height: 4px;
    transition: width 0.7s ease;
    border-radius: 4px;
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

.info-cover {
    width: 192px;
    height: 108px;
    border-radius: 5px;
    overflow: hidden;
}

.info-cover img {
    height: 100%;
    width: 100%;
    object-fit: cover;
}

.desc-text {
    width: 80%;
    margin-top: 12px;
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

.radio-selected {
    color: #FF6699;
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

.upload-submit-wrap {
    position: relative;
    padding: 32px 0 32px 134px;
}

.upload-submit-btn {
    margin-left: 16px;
    line-height: 40px;
    height: 40px;
    width: 120px;
    font-size: 14px;
    border-radius: 4px;
    color: #ffffff;
    background-color: #FF6699;
    display: inline-block;
    text-align: center;
    vertical-align: middle;
    cursor: pointer;
    transition: all .3s ease-in-out;
}

.upload-submit-btn:hover {
    background-color: #FF8CB0;
}

.cover-dialog /deep/ .el-dialog__body {
    margin: -30px 0 0 0;
    padding: 0;
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

.popover-wrap {
    margin-top: 30px;
    padding: 0 30px;
    height: 400px;
    transition: height .5s;
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

.upload-box /deep/ .el-upload {
    --el-upload-dragger-padding-vertical: 40px;
}

.cover-wrap {
    border-radius: 10px;
    max-width: 544px;
    max-height: 306px;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
}

.cover-wrap img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
</style>