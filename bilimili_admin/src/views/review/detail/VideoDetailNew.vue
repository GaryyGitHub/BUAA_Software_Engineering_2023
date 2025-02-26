<template>
    <div class="video-detail-wrap">
        <div class="video-container-wrap">
            <div class="video-container">
                <div class="video-section">
                    <div id="player" class="video-player">
                        <video :src="video.videoUrl" controls></video>
                    </div>
                    <div class="video-status status-options-area">
                        <div class="current-status">
                            <div class="status" v-if="video.status === 0">
                                <i class="iconfont icon-shenhezhong"></i>
                                <span>待审核</span>
                            </div>
                            <div class="status" v-if="video.status === 1">
                                <i class="iconfont icon-wancheng"></i>
                                <span>已通过</span>
                            </div>
                            <div class="status" v-if="video.status === 2">
                                <i class="iconfont icon-shibai"></i>
                                <span>未通过</span>
                            </div>
                            <div class="status-btn">
                                <el-button type="success" class="btn" @click="handleStatusChange(1)" plain>
                                    <span>通过审核</span>
                                </el-button>
                                <el-button type="warning" class="btn" @click="handleStatusChange(2)" plain>
                                    <span>不予过审</span>
                                </el-button>
                                <el-button type="danger" class="btn" @click="handleStatusChange(3)" plain>
                                    <span>永久删除</span>
                                </el-button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="video-info">
                    <div class="video-status info">
                        <div class="info-list">
                            <div class="list-title">标题</div>
                            <div class="list-content">{{video.title}}</div>
                        </div>
                        <div class="info-list">
                            <div class="list-title">属性</div>
                            <div class="list-content">
                                <span class="type" v-if="video.type === 1">自制</span>
                                <span class="type" v-if="video.type === 2">转载</span>
                                <div class="authorization" v-if="video.type === 1 && video.authorization">
                                    <span>未经授权 禁止转载</span>
                                </div>
                            </div>
                        </div>
                        <div class="info-list">
                            <div class="list-title">分区</div>
                            <div class="list-content">{{ category.mcName }} &nbsp;→&nbsp; {{ category.scName }}</div>
                        </div>
                        <div class="info-list">
                            <div class="list-title">说明</div>
                            <div class="list-content">
                                <span class="video-text" v-html="linkifyText(video.description)"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ElMessage } from 'element-plus'

export default {
    name: "VideoDetail",
    data() {
        return {
            video: {},
            user: {},
            category: {},
        }
    },
    methods: {
        async handleStatusChange(status) {
            ElMessage.success('操作成功！');
            this.$store.state.isLoading = true;

            const formData = new FormData();
            formData.append('vid', this.$route.params.vid);
            formData.append('status', status);

            try {
                const res = await this.$post('/video/change/status', formData, {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("bm_token")}`,
                    }
                });

                if (res.data && res.data.code === 200) {
                    if (status === 3) {
                        this.$router.push('/review/video/form');
                    } else {
                        await this.getVideoDetail();
                    }
                }
            } finally {
                this.$store.state.isLoading = false;
            }
        },

        linkifyText(text) {
            if (text) {
                // 匹配URL的正则表达式
                var urlRegex = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#/%=~_|$?!:,.]*[A-Z0-9+&@#/%=~_|$])/gi;

                // 将匹配到的URL替换为带有链接的HTML
                var linkedText = text.replace(urlRegex, function (url) {
                    return '<a href="' + url + '" class="v-url" target="_blank">' + url + '</a>';
                });

                return linkedText;
            }
            return text; // 如果 text 未定义，则返回原始值
        },

        async getVideoDetail() {
            const res = await this.$get('/review/video/getone', {
                params: {
                    vid: this.$route.params.vid,
                },
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("bm_token"),
                },
            });
            if (res.data.data) {
                this.video = res.data.data.video;
                this.user = res.data.data.user;
                this.category = res.data.data.category;
            }
        },

        initPlayer() {
            const player = document.getElementById('player');
            player.style.height = `${player.offsetWidth * (9 / 16)}px`;
        },
    },
    async created() {
        await this.getVideoDetail();
    },
    mounted() {
        this.initPlayer();
        window.addEventListener('resize', this.initPlayer);
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.initPlayer);
    },
}
</script>

<style scoped>
.video-detail-wrap {
    flex: 1 1 auto;
}

.video-container-wrap {
    width: 100%;
    padding: 16px;
    margin-right: auto;
    margin-left: auto;
    position: relative;
}

@media (min-width: 960px) {
    .video-container-wrap {
        max-width: 900px;
    }
}

@media (min-width: 1120px) {
    .video-container-wrap {
        max-width: 1050px;
    }
}

@media (min-width: 1280px) {
    .video-container-wrap {
        max-width: 1200px;
    }
}

.video-container {
    width: 100%;
    display: flex;
    position: relative;
}

.video-section {
    width: 67%;
    max-width: 720px;
}

.video-player {
    width: 100%;
    box-shadow: 2px 2px 8px rgba(21, 21, 21, 0.25);
    background-color: #0f0f0f;
    border-radius: 16px;
}

.video-player video {
    width: 100%;
    height: 100%;
    border-radius: 16px;
}

.video-status {
    border-radius: 4px;
    box-shadow: 0 2px 10px -1px rgba(83, 83, 83, 0.1), 0 1px 10px rgba(85, 85, 85, 0.07), 0 1px 30px rgba(85, 85, 85, 0.03);
    background-color: #fff;
    display: block;
    overflow: hidden;
    padding: 0;
    transition-property: box-shadow,opacity,background;
    z-index: 0;
}

.status-options-area {
    box-shadow: 0 2px 10px -1px rgba(83, 83, 83, 0.1), 0 1px 10px rgba(85, 85, 85, 0.07), 0 1px 30px rgba(85, 85, 85, 0.03);
    margin-top: 16px;
    border-radius: 40px;
}

.info {
    padding: 0 16px 30px 20px;
}

.current-status {
    height: 64px;
    padding: 0 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.status {
    display: flex;
    align-items: center;
}

.status .iconfont {
    font-size: 12px;
    margin-right: 5px;
}

.icon-shenhezhong {
    color: #a86c04;
}

.icon-wancheng {
    color: #04732d;
}

.icon-shibai {
    color: #b9110b;
}

.btn {
    border-radius: 18px;
    padding: 0 10px;
}

.type {
    flex: 0 0 auto;
    width: 45px;
}

.video-info {
    margin: 0 0 0 16px;
    flex: 1;
    min-width: 300px;
    max-width: 500px;
    overflow: hidden;
    color: #61666D;
}

.info-list {
    display: flex;
    margin-top: 20px;
}

.list-title {
    flex: 0 0 auto;
    width: 70px;
    font-size: 16px;
    font-weight: 550;
    color: #18191C;
}

.list-content {
    flex: 1;
    display: flex;
}

.video-text {
    word-break: break-all;
    width: 100%;
    padding: 10px;
    background-color: rgba(255, 255, 255, 0.7);
    border: 2px solid #e0e0e0;
    border-radius: 10px;
    white-space: pre-line;
}

</style>