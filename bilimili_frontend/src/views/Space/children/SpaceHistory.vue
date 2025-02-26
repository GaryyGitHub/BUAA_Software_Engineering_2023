<!-- 个人主页-历史 -->
<template>
  <div class="container">
    <div class="title">
      <span class="title-text">我的观看历史</span>
      <span class="tip-text">排序方式：从左至右、从上至下，按时间降序排序</span>
    </div>
    <!-- 视频展示框 -->
    <div class="video-container" v-if="videoHistory.length > 0">
      <div>
        <div class="video-box" v-for="(item, index) in videoHistory" :key="index">
          <a :href="`/video/${item.vid}`" target="_blank" class="video-cover">
            <img :src="item.url">
            <div class="video-duration">{{ handleTime(item.duration) }}</div>
          </a>
          <div @click="openNewPage(`/video/${item.vid}`)" target="_blank" class="video-title">{{ item.title }}</div>
          <div class="video-info">
            播放量：{{ item.view }}
          </div>
          <div class="video-info">
            上次观看：{{ handleDateTime3(item.time) }}
          </div>
        </div>
      </div>
    </div>
    <div class="no-video" v-else>
      这里空空如也~快去看看精彩的站内视频吧
    </div>
  </div>
  <span class="dixian">-- 你已试探到我的底线 --</span>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { get } from '@/network/request'
import { onMounted, ref } from 'vue';
import { handleTime, handleDateTime3 } from '@/utils/utils.js'

const route = useRoute()
const router = useRouter()

const uidVisited = Number(route.params.uid)

// 视频历史记录相关数据
const videoHistory = ref([])

// 从后端获取历史记录
const getHistory = async() => {
  const res = await get('/Record/Video', {
    params: { uid: uidVisited },
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
  })
  console.log(res)
  let temp = res.data.data
  console.log(temp)
  videoHistory.value = temp.duration.map((item, index) => ({
    duration: item,
    time: temp.time[index],
    title: temp.title[index],
    url: temp.url[index],
    vid: temp.vid[index],
    view: temp.view[index],
  }))
  console.log(videoHistory.value)
}

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

onMounted(async() => {
  await getHistory()
})
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
}

.title {
  width: 1200px;
  margin: 15px 30px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 30px;
}

.title-text {
  margin-left: 40px;
  font-size: 24px;
  font-weight: bold;
  color: black;
  text-align: left;
  display: flex;
  align-items: center;
}

.tip-text {
  margin-right: 40px;
  font-size: 14px;
  color: #999;
  text-align: left;
  display: flex;
  align-items: center;
}

.wrapper {
  width: 1200px;
  /* background-color: #166fe8; */
}

.video-container {
  display: flex;
  justify-content: center;
  margin-left: 20px;
}

.video-box {
  display: block;
  float: left;
  width: 250px;
  margin: 0 10px 10px;
  padding: 10px;
  height: 220px;
}

.video-cover {
  background: url('../../../assets/img/video-placeholder.png') 50%;
  background-size: cover;
  border-radius: 10px;
  display: block;
  width: 250px;
  height: 155px;
  overflow: hidden;
  position: relative;
  box-shadow: var(--el-box-shadow);
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
  cursor: pointer;
  text-align: left;
  font-size: 15px;
  margin-top: 8px;
  transition: color .2s ease;
}

.video-title:hover {
  color: #166fe8;
}

.video-info {
  text-align: left;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.no-video {
  margin-top: 50px;
  align-items: center;
  text-align: center;
  font-size: 20px;
  color: #999;
}

.dixian {
  font-size: 13px;
  color: #999;
}
</style>