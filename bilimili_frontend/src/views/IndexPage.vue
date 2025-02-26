<!-- 首页 -->
<template>
  <NavBar></NavBar>
  <div class="change" @click="getRandomVideos">
    <el-icon :size="20" style="animation: rotating 2s linear reverse;"><Refresh /></el-icon>换一换
  </div>
  <div class="container">
    <div class="content">
      <div class="upper">
        <div class="navbar-item" @click="isLogin ? openNewPage(`/space/${user.uid}/dynamic`) : handleLogin()">
          <el-icon :size="30"><MagicStick /></el-icon>
          <span class="navbar-item-text">动态</span>
        </div>
        <!-- 频道列表 -->
        <div class="channel">
          <el-row :gutter="0" justify="space-evenly">
            <el-col :span="2" v-for="(item,key) in channelList.slice(0, 9)" :key="key">
              <div class="grid-content">{{ item.mcName }}</div>
            </el-col>
          </el-row>
          <el-row :gutter="0" justify="space-evenly">
            <el-col :span="2" v-for="(item, key) in channelList.slice(9, 18)" :key="key">
              <div class="grid-content">{{ item.mcName }}</div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="videos-1">
        <!-- 轮播图 -->
        <div class="carousel">
          <el-carousel :interval="3000" motion-blur class="carousel-wrapper" >
            <el-carousel-item v-for="(item, key) in carouselJson" :key="key" class="carousel-item"
              @click="openNewPage(`${item.target}`)">
              <img :src="item.url" alt="">
              <div class="carousel-title">{{ item.title }}</div>
            </el-carousel-item>
          </el-carousel>
        </div>
        <div class="video-list-1">
          <el-row :gutter="0" justify="space-evenly">
            <el-col :span="8" v-for="(item, key) in randomVideoList.slice(0, 3)" :key="key" style="margin-bottom: 20px;">
              <a :href="`/video/${item.video.vid}`" target="_blank" class="video-cover">
                <img :src="item.video.coverUrl">
                <div class="video-duration">{{ handleTime(item.video.duration) }}</div>
              </a>
              <div @click="openNewPage(`/video/${item.video.vid}`)" target="_blank" class="video-title">{{ item.video.title }}</div>
              <div class="video-up" @click="openNewPage(`/space/${item.user.uid}/home`)">
                UP主：{{ item.user.nickname }}
              </div>
              <div class="video-info">
                {{ item.stats.play }}次观看 · {{ item.stats.good }}次点赞 · {{ handleDate(item.video.uploadDate) }}
              </div>
            </el-col>
            <el-col :span="8" v-for="(item, key) in randomVideoList.slice(3, 6)" :key="key">
              <a :href="`/video/${item.video.vid}`" target="_blank" class="video-cover">
                <img :src="item.video.coverUrl">
                <div class="video-duration">{{ handleTime(item.video.duration) }}</div>
              </a>
              <div @click="openNewPage(`/video/${item.video.vid}`)" target="_blank" class="video-title">{{ item.video.title }}</div>
              <div class="video-up" @click="openNewPage(`/space/${item.user.uid}/home`)">
                UP主：{{ item.user.nickname }}
              </div>
              <div class="video-info">
                {{ item.stats.play }}次观看 · {{ item.stats.good }}次点赞 · {{ handleDate(item.video.uploadDate) }}
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="videos-2">
        <el-row :gutter="0" justify="space-evenly">
          <!-- 这是随机视频的后5个 -->
            <el-col :span="4" v-for="(item, key) in randomVideoList.slice(6, 11)" :key="key">
              <a :href="`/video/${item.video.vid}`" target="_blank" class="video-cover">
                <img :src="item.video.coverUrl">
                <div class="video-duration">{{ handleTime(item.video.duration) }}</div>
              </a>
              <div @click="openNewPage(`/video/${item.video.vid}`)" target="_blank" class="video-title">{{ item.video.title }}</div>
              <div class="video-up" @click="openNewPage(`/space/${item.user.uid}/home`)">
                UP主：{{ item.user.nickname }}
              </div>
              <div class="video-info">
                {{ item.stats.play }}次观看 · {{ item.stats.good }}次点赞 · {{ handleDate(item.video.uploadDate) }}
              </div>
            </el-col>
          </el-row>
          <!-- 此处开始是其他视频，共10个 -->
          <el-row :gutter="0" justify="space-evenly">
            <el-col :span="4" v-for="(item, key) in otherVideoList.slice(0, 5)" :key="key" style="margin-bottom: 10px;">
              <a :href="`/video/${item.video.vid}`" target="_blank" class="video-cover">
                <img :src="item.video.coverUrl">
                <div class="video-duration">{{ handleTime(item.video.duration) }}</div>
              </a>
              <div @click="openNewPage(`/video/${item.video.vid}`)" target="_blank" class="video-title">{{ item.video.title }}</div>
              <div class="video-up" @click="openNewPage(`/space/${item.user.uid}/home`)">
                UP主：{{ item.user.nickname }}
              </div>
              <div class="video-info">
                {{ item.stats.play }}次观看 · {{ item.stats.good }}次点赞 · {{ handleDate(item.video.uploadDate) }}
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="0" justify="space-evenly">
            <el-col :span="4" v-for="(item, key) in otherVideoList.slice(5, 10)" :key="key">
              <a :href="`/video/${item.video.vid}`" target="_blank" class="video-cover">
                <img :src="item.video.coverUrl">
                <div class="video-duration">{{ handleTime(item.video.duration) }}</div>
              </a>
              <div @click="openNewPage(`/video/${item.video.vid}`)" target="_blank" class="video-title">{{ item.video.title }}</div>
              <div class="video-up" @click="openNewPage(`/space/${item.user.uid}/home`)">
                UP主：{{ item.user.nickname }}
              </div>
              <div class="video-info">
                {{ item.stats.play }}次观看 · {{ item.stats.good }}次点赞 · {{ handleDate(item.video.uploadDate) }}
              </div>
            </el-col>
        </el-row>
      </div>
    </div>
    <span class="dixian">-- 你已试探到我的底线 --</span>
  </div>
</template>

<script setup>
import { handleTime, handleDate } from '@/utils/utils.js'
import NavBar from '@/components/NavBar.vue';
import { get } from '@/network/request'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex';
import { onMounted, ref, computed, } from 'vue';
import carouselJson from '@/assets/carousel.json';
import { ElMessage } from 'element-plus';

const router = useRouter()
const store = useStore()

const channelList = ref([]);
const randomVideoList = ref([]);
const otherVideoList = ref([]);

const isLogin = computed(() => {
  return store.state.isLogin;
});

const user = computed(() => {
  return store.state.user;
});

// 获取全站所有频道列表
const getChannels = async () => {
  const res = await get("/category/getall");
  channelList.value = res.data.data;
  console.log('channel', channelList.value)
}

// 获取随机推荐的11条视频
const getRandomVideos = async () => {
  const res = await get("/video/random/visitor");
  randomVideoList.value = res.data.data;
  console.log('randomVideoList', randomVideoList.value)
}

const getOtherVideos = async () => {
  const res = await get("/video/cumulative/visitor", {
    params: {vids: ""}
  })
  otherVideoList.value = res.data.data.videos;
  console.log('getOtherVideos', otherVideoList.value)
}

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

const handleLogin = () => {
  ElMessage.error('请先登录');
}

onMounted(async() => {
  await getChannels();
  await getRandomVideos();
  await getOtherVideos();
  console.log(carouselJson)
})
</script>

<style scoped>
.change {
  cursor: pointer;
  background-color: white;
  border-radius: 5px;
  border: 1px solid #999;
  padding: 5px;
  width: 30px;
  position: absolute;
  top: 260px;
  right: 80px;
  font-size: 17px;
  transition: background-color 0.3s ease;
  transition: color 0.2s ease;
}

.change:hover {
  background-color: #f25d8e;
  color: white;
}

.container {
  margin-top: 95px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.content {
  padding-bottom: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: relative;
  width: 1400px;
  border-radius: 10px;
  border: 1px solid #eeeeee;
  background-color: white;
}

.upper{
  border-top-right-radius: 20px;
  border-top-left-radius: 20px;
  width: 1400px;
  background: url('../assets/img/index-background.jpg');
  background-position: center center;
  background-size: cover;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  flex-direction: row;
}

.navbar-item {
  cursor: pointer;
  color: rgb(255, 255, 255);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.navbar-item-text {
  font-size: 20px;
  margin-top: 10px;
}

.channel {
  border-left: solid 1px #eeeeee;
  padding: 20px 10px 20px;
  width: 1200px;
}

.el-row {
  margin-bottom: 20px;
}

.el-row:last-child {
  margin-bottom: 0;
}

.el-col {
  border-radius: 4px;
}

.grid-content {
  opacity: 0.9;
  user-select: none;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  min-height: 36px;
  background-color: #eeeeee;
  color: #777777;
  margin: 0 10px 0;
  transition: background-color 0.3s ease;
  transition: color 0.2s ease;
}

.grid-content:hover {
  background-color: #f25d8e;
  color: white;
  opacity: 1;
}

.videos-1 {
  margin-top: 30px;
  width: 1400px;
  display: flex;
  justify-content: start;
  align-items: start;
  flex-direction: row;
}

.carousel-wrapper {
  margin: 0px 30px 20px;
  width: 550px;
  height: 400px;
  box-shadow: var(--el-box-shadow);
}

.carousel-item {
  cursor: pointer;
  background: url('../assets/img/video-placeholder.png') 50%;
  background-size: cover;
  border-radius: 10px;
  width: 550px;
  height: 400px;
  overflow: hidden;
}

.carousel-item img {
  display: block;
  width: 100%;
  height: 100%;
  /* 适应盒子尺寸，避免拉伸 */
  object-fit: cover;
  /* 保持锐利的边缘，避免模糊 */
  image-rendering: crisp-edges;
}

.carousel-title {
  position: absolute;
  left: 15px;
  bottom: 25px;
  background: rgba(0, 0, 0, 0.541);
  color: white;
  font-size: 22px;
  padding: 2px 4px;
  z-index: 100;
  border-radius: 4px;
}

.video-list-1 {
  /* margin-top: 40px; */
  width: 780px;
  /* background-color: #a03f3f; */
}

.video-cover {
  background: url('../assets/img/video-placeholder.png') 50%;
  background-size: cover;
  border-radius: 10px;
  display: block;
  width: 240px;
  height: 150px;
  overflow: hidden;
  position: relative;
  box-shadow: var(--el-box-shadow-light);
}

.video-cover img {
  display: block;
  width: 100%;
  height: 100%;
  /* 适应盒子尺寸，避免拉伸 */
  object-fit: cover;
  /* 保持锐利的边缘，避免模糊 */
  image-rendering: crisp-edges;
}

.video-duration {
  position: absolute;
  right: 8px;
  bottom: 8px;
  background: rgba(0, 0, 0, 0.541);
  color: white;
  font-size: 13px;
  padding: 2px 4px;
  z-index: 100;
  border-radius: 4px;
}

.video-title {
  width: 240px;
  cursor: pointer;
  text-align: left;
  font-size: 15px;
  margin-top: 8px;
  transition: color .2s ease;
}

.video-title:hover {
  color: #166fe8;
}

.video-up {
  cursor: pointer;
  width: 240px;
  text-align: left;
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  transition: color .2s ease;
}

.video-up:hover {
  color: #166fe8;
}

.video-info {
  width: 240px;
  text-align: left;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.videos-2 {
  margin-top: 30px;
  width: 1400px;
}

.dixian {
  font-size: 13px;
  margin-top: 40px;
  color: #999;
}
</style>