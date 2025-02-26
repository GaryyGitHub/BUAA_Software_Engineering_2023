<!-- 默认搜索页 -->
<template>
  <NavBar :isShowInputBox="false"></NavBar>
  <div class="search-page">
    <!-- 搜索内容为空 -->
    <div class="default-search-container" v-if="!props.keyword">  
      <div class="search-header">
        <img src="../assets/img/logo-pink.png" class="search-logo">
        <span>搜索</span>
      </div>
      <SearchBox style="margin-top: 40px;" :keyword="props.keyword"/>
    </div>
    <!-- 搜索不为空 -->
    <div class="keyword-search-container" v-else>
      <div class="search-box-container">
        <SearchBox style="margin-top: 60px;" :keyword="props.keyword"/>
      </div>
    </div>
    <div class="search-result">
        <el-card class="result-container" v-for="(item, index) in videoList" :key="index">
          <div class="video-cover">
            <a :href="`/video/${item.video.vid}`" target="_blank">
              <img :src="item.video.coverUrl" class="img-container"/>
            </a>
          </div>
          <div class="video-info">
            <div class="video-title" @click="openNewPage(`/video/${item.video.vid}`)" v-html="highlightKeyword(props.keyword, item.video.title)"></div>
            <div class="video-introduction">
              {{ handleNum(item.stats.play) }} 播放 · {{ handleNum(item.stats.danmu) }} 弹幕
            </div>
            <div class="video-introduction">
              时长：{{ handleTime(item.video.duration) }}
            </div>
            <div class="video-introduction">
              发布时间：{{ handleDate(item.video.uploadDate) }}
            </div>
            <div class="video-up">
              <div class="avatar">
                <!-- 需要填充href -->
                <a :href="`/space/${item.user.uid}/home`" target="_blank" class="avatar-container">
                  <img :src="item.user.avatar_url" alt="" class="avatar-img" />
                </a>
              </div>
              <div @click="openNewPage(`/space/${item.user.uid}/home`)" class="nickname">{{ item.user.nickname }}</div>
            </div>
            <div class="video-introduction">
              简介：{{ item.video.descr='' ? '暂无' : item.video.descr }}
            </div>
          </div>
        </el-card>
      </div>
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue';
import SearchBox from '@/components/SearchBox.vue';
import { defineProps, ref, onMounted } from 'vue';
import { get } from '@/network/request'
import { highlightKeyword, handleNum, handleTime, handleDate } from '@/utils/utils'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  keyword: String
}); // 接收路由中传过来的keyword

const videoList = ref([])
// const pageNow = ref(1)

// 获取搜索视频列表
const getVideoList = async() => {
  videoList.value = []
  /////////这里暂时测试用随机视频看看效果，到时候要改一下！并且不用pageNow了，需要和后端对接//////////
  const kw = encodeURIComponent(props.keyword)
  const res = await get("/search/video/only-pass", {
    params: {
      keyword: kw, page: 1
    }
  })
  // const res = await get("/video/random/visitor");
  console.log("获取视频：", res)
  if(res.data) {
    videoList.value = res.data.data
  }
}

// 组件挂载时获取视频列表
onMounted(() => {
  if(props.keyword) {
    getVideoList()
  }
})

const openNewPage = (route) => {
  window.open(router.resolve(route).href);
};

</script>

<style scoped>
.default-search-container {
  top: 40%;
  position: absolute;
  left: 50%;
  -webkit-transform: translate(50%, 50%);
  transform: translate(-50%,-50%);
}

.keyword-search-container {
  top: 15%;
  position: absolute;
  left: 50%;
  -webkit-transform: translate(50%, 50%);
  transform: translate(-50%,-50%);
}

.search-header {
  padding-top: 10vh;
  display: flex;
  font-size: 60px;
  justify-content: center;
}

.search-logo {
  height: 80px;
  margin-right: 1vw;
}

.search-box-container {
  margin-top: 100px;
}

.search-result {
  margin-top: 280px; /* 调整搜索结果与搜索框的间距 */
  display: flex;
  flex-direction: column;
  align-items: center; /* 水平居中 */
}

.result-container {
  height: 300px;
  width: 1200px;
  margin: 5px;
  border-radius: 10px;
  display: flex;
  align-items: center;
}

.video-cover {
  flex-shrink: 0;
  float: left;
}

.img-container {
  width: 100%;
  border-radius: 10px;
  height: 280px;
  /* width: 100%; */
}

.video-info {
  max-width: 635px;
  margin-left: 20px;
  float: left;
  /* display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px; */
}

.video-title {
  margin-left: 10px;
  margin-top: 10px;
  margin-bottom: 15px;
  cursor: pointer;
  font-size: 22px;
  text-align: left;
}

.video-title:hover {
  font-weight: bold;
}

.video-introduction {
  margin-left: 10px;
  margin-top: 8px;
  text-align: left;
  font-size: 14px;
  color: #0000009b
}

.video-up {
  margin-left: 10px;
  margin-top: 25px;
  margin-bottom: 25px;
  color: #0000009b;
  text-align: left;
  font-size: 13px;
  display: flex;
  align-items: center;
}

.avatar {
  cursor: pointer;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background-color: #bcbcbc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-container {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}

.avatar-img {
  /* position: relative;
  display: inline-block;
  line-height: 1;
  width: 100%;
  height: 100%;
  vertical-align: middle;
  border-radius: 50%;
  background-color: transparent; */
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-img:hover {
  opacity: 0.5;
}

.nickname {
  cursor: pointer;
  margin-left: 7px;
}

.nickname:hover {
  font-weight: bold;
}
</style>