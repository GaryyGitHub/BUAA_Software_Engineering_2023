<!-- 个人主页-投稿 -->
<template>
  <!-- 左侧-导航栏 -->
  <div class="container">
    <div class="nav-bar">
      <el-tabs v-model="leftNavBar" @tab-click="handleClick" tab-position="left" type="card">
        <!-- 右侧内容：视频 -->
        <el-tab-pane name="video" >
          <template #label>
            <span class="slot-text"><el-icon :size="18"><VideoPlay /></el-icon>
              <div style="margin-left: 10px;">视频</div>
            </span>
            <span class="slot-num">{{ videoList.length }}</span>
          </template>
          <!-- 贯穿始终 -->
          <div class="video-content">
            <!-- 标题 -->
            <div class="title">
              <span class="title-text">
                {{ uidVisiting === uidVisited ? '我' : 'TA' }}的视频
              </span>
              <div class="nav-bar-video" v-if="videoList.length > 0">
                <el-tabs v-model="ruleNavBar" @tab-click="handleClickVideo" tab-position="top" type="">
                  <el-tab-pane label="最新发布" name="1" ></el-tab-pane>
                  <el-tab-pane label="最多播放" name="2" ></el-tab-pane>
                  <el-tab-pane label="最多点赞" name="3" ></el-tab-pane>
                </el-tabs>
              </div>
            </div>
            <!-- 视频展示框 -->
            <div class="video-container" v-if="videoList.length > 0">
              <div>
                <div class="video-box" v-for="(item, index) in videoList" :key="index">
                  <a :href="`/video/${item.video.vid}`" target="_blank" class="video-cover">
                    <img :src="item.video.coverUrl">
                    <div class="video-duration">{{ handleTime(item.video.duration) }}</div>
                  </a>
                  <div @click="openNewPage(`/video/${item.video.vid}`)" target="_blank" class="video-title">{{ item.video.title }}</div>
                  <div class="video-info">
                    {{ item.stats.play }}次观看 · {{ item.stats.good }}次点赞 · {{ handleDate(item.video.uploadDate) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="no-video" v-else>
              这里空空如也~{{ uidVisiting === uidVisited ? '快去创作你的第一条视频吧！' : 'UP还没有上传视频' }}
            </div>
          </div>
        </el-tab-pane>
        <!-- 右侧内容：专栏 -->
        <el-tab-pane name="article" >
          <template #label>
            <span class="slot-text"><el-icon :size="18"><Tickets /></el-icon>
              <div style="margin-left: 10px;">专栏</div>
            </span>
            <span class="slot-num">{{ articleCount }}</span>
          </template>
          <!-- 贯穿始终 -->
          <div class="article-content">
            <div class="title">
              <span class="title-text">
                {{ uidVisiting === uidVisited ? '我' : 'TA' }}的专栏
              </span>
            </div>
            <!-- 专栏展示框 -->
            <div class="video-container" v-if="articleCount > 0">
              <div class="article-list">
                <div class="video-box" v-for="(item, index) in articleList" :key="index">
                  <a :href="`/article/${item.article.aid}`" target="_blank" class="video-cover">
                    <img :src="item.article.coverUrl">
                  </a>
                  <div @click="openNewPage(`/article/${item.article.aid}`)" target="_blank" class="video-title">{{ item.article.title }}</div>
                </div>
              </div>
            </div>
            <div class="no-video" v-else>
              这里空空如也~{{ uidVisiting === uidVisited ? '快去发表你的第一个专栏吧！' : 'UP还没有发布专栏' }}
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { handleTime, handleDate } from '@/utils/utils.js'
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex';
import { get } from '@/network/request'

const route = useRoute()
const router = useRouter()
const store = useStore()

const uidVisited = Number(route.params.uid)
const uidVisiting = computed(() => {
  return store.state.user.uid;
});

// 排序规则
const sortRule = ref(1)
const videoList = ref([])
const articleList = ref([])
const articleCount = ref(0)

// 两个标签页的状态
const leftNavBar = ref('video')
const ruleNavBar = ref('1')

const handleClick = (tab) => {
  leftNavBar.value = tab.props.name
  const path = `/space/${route.params.uid}/upload/${tab.props.name}`
  if(path !== router.path) {
    router.push(path)
  }
  // await getVideoList()
}

const handleClickVideo = async(tab) => {
  ruleNavBar.value = tab.props.name
  sortRule.value = tab.props.name
  await getVideoList()
}

// 用户投稿的视频
const getVideoList = async() => {
  const res = await get("/video/user-works", {
    params: {
      uid: uidVisited,
      rule: sortRule.value,
      page: 1,
      quantity: 30,
    }
  })
  videoList.value = res.data.data.list
  console.log('videoList', videoList.value)
}

// 用户投稿的专栏
const getArticleList = async() => {
  const res = await get("/article/user-works", {
    params: {
      uid: uidVisited,
      page: 1,
      quantity: 30
    }
  })
  articleList.value = res.data.data.list
  articleCount.value = res.data.data.count
  console.log('articleList', articleList.value)
}

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

// created
// 这个很关键：别的页面过来，create的时候看router.path，初始化activityNavBar
if (route.path.split('/').length === 4) {
  leftNavBar.value = 'video'
} else {
  leftNavBar.value = route.path.split('/')[4]
}
console.log('uid', uidVisited, uidVisiting.value)

onMounted(async() => {
  await getVideoList()
  await getArticleList()
})
</script>

<style scoped>
.container {
  margin-bottom: 50px;
}

.nav-bar {
  height: 100%;
}

.nav-bar /deep/ .el-tabs__item {
  font-size: 16px;
  width: 200px;
  padding: 0 30px;
}
.nav-bar /deep/ .el-tabs__content {
  border-left: 1px solid #aaaaaa90;
  padding-left: 10px;
}

.nav-bar /deep/ .el-tabs__item.is-left.is-active {
  border-right: 1px solid #e4e7ed;
}

.nav-bar /deep/ .el-tabs--left .el-tabs__item.is-left, .el-tabs--right .el-tabs__item.is-left {
  justify-content: space-between;
} 

.slot-text {
  /* 此法使得icon和文字对齐 */
  display: flex;
  align-items: center;
}

.slot-num {
  color: black;
  border: 1px solid #aaaaaa;
  border-radius: 5px;
  padding: 1px 4px;
}

.title {
  margin: 15px 30px 20px;
  display: flex;
  align-items: center;
  justify-content: start;
  height: 30px;
}

.title-text {
  font-size: 24px;
  font-weight: bold;
  color: black;
  text-align: left;
  display: flex;
  align-items: center;
}

.video-container {
  display: flex;
  justify-content: flex-start;
}

.nav-bar-video {
  margin-top: 10px;
  margin-left: 50px;
}

.nav-bar-video /deep/ .el-tabs__active-bar {
  background-color: #166fe8; /* 修改底部横杠的颜色 */
  height: 2.5px; /* 修改底部横杠的高度 */
}

.nav-bar-video /deep/ .el-tabs__nav-wrap::after {
  position: static !important;
}

.nav-bar-video /deep/ .el-tabs__item {
  width: 80px;
  font-size: 14px;
  padding: 0 0px;
}

.video-box {
  /* background-color: #9d0f0f; */
  display: block;
  float: left;
  width: 220px;
  margin: 0 0 10px;
  padding: 10px;
  height: 210px;
}

.video-cover {
  background: url('../../../assets/img/video-placeholder.png') 50%;
  background-size: cover;
  border-radius: 10px;
  display: block;
  width: 220px;
  height: 140px;
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
</style>