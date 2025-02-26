<!-- 个人主页-收藏夹 -->
<template>
  <div class="container">
    <div class="nav-bar">
      <el-tabs v-model="leftNavBar" @tab-click="handleClick" tab-position="left" type="card">
        <el-tab-pane v-for="item in favList" :key="item.fid" :name="item.fid">
          <!-- 左侧-导航栏 -->
          <template #label>
            <span class="slot-text" v-if="item.fid === leftNavBar">
              <el-icon :size="18"><FolderOpened /></el-icon>
              <div style="margin-left: 10px;">{{ item.title }}</div>
            </span>
            <span class="slot-text" v-else>
              <el-icon :size="18"><Folder /></el-icon>
              <div style="margin-left: 10px;">{{ item.title }}</div>
            </span>
          </template>
          <div class="right-content">
            <!-- 标题 -->
            <div class="title">
              <span class="title-text">
                {{ uidVisiting === uidVisited ? '我' : 'TA' }}的收藏夹
              </span>
              <div class="nav-bar-video" v-if="favVideoList.length > 0">
                <el-tabs v-model="ruleNavBar" @tab-click="handleClickVideo" tab-position="top" type="">
                  <el-tab-pane label="最新发布" name="1" ></el-tab-pane>
                  <el-tab-pane label="最多播放" name="2" ></el-tab-pane>
                  <el-tab-pane label="最多点赞" name="3" ></el-tab-pane>
                </el-tabs>
              </div>
            </div>
            <!-- 收藏夹具体内容 -->
            <div class="fav-container">
              <!-- 头图 -->
              <div class="fav-box">
                <span class="fav-cover" v-if="item.visible === 1 || item.uid === uidVisiting">
                  <img v-if="item.cover" :src="item.cover">
                  <div class="fav-num">{{ item.visible === 1 ? '公开' : '私密' }} · {{ item.count }}</div>
                </span>
                <div class="fav-title">{{ item.title }}</div>
              </div>
              <!-- 视频展示 -->
              <div class="fav-content" v-if="favVideoList.length > 0">
                <div>
                  <div class="video-box" v-for="(video, index2) in favVideoList" :key="index2">
                    <a :href="`/video/${video.video.vid}`" target="_blank" class="video-cover">
                      <img :src="video.video.coverUrl">
                      <div class="video-duration">{{ handleTime(video.video.duration) }}</div>
                      <div class="video-hover">
                        <div class="hover-text">
                          <p>播放量：{{ video.stats.play }}</p>
                          <p>点赞：{{ video.stats.good }}</p>
                          <p>收藏：{{ video.stats.collect }}</p>
                          <p>弹幕：{{ video.stats.danmu }}</p>
                          <p>发布日期：{{ handleDate(video.video.uploadDate) }}</p>
                        </div>
                      </div>
                    </a>
                    <div @click="openNewPage(`/video/${video.video.vid}`)" target="_blank" class="video-title">{{ video.video.title }}</div>
                    <div class="video-info">
                      UP主：<span class="up" @click="openNewPage(`/space/${video.user.uid}/home`)">{{ video.user.nickname }}</span>
                    </div>
                    <div class="video-info" >
                      收藏于 {{ handleDate(video.info.time) }}
                    </div>
                  </div>
                </div>
              </div>
              <div class="no-video" v-else>
                这个收藏夹暂时还没有视频~
              </div>
            </div>
            
          </div>
          
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { handleTime, handleDate } from '@/utils/utils.js'
import { computed, ref, onMounted, watch } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import { get } from '@/network/request'

const route = useRoute()
const router = useRouter()
const store = useStore()

const uidVisited = Number(route.params.uid)
const uidVisiting = computed(() => {
  return store.state.user.uid;
});

const leftNavBar = ref(Number(route.query.fid) || uidVisited)
const ruleNavBar = ref('1')
const sortRule = ref(1)

const handleClick = (tab) => {
  leftNavBar.value = tab.props.name
  const path = `/space/${route.params.uid}/favlist?fid=${tab.props.name}`
  if(path !== router.path) {
    router.push(path)
  }
}

const handleClickVideo = async(tab) => {
  ruleNavBar.value = tab.props.name
  sortRule.value = tab.props.name
  await getFavVideoList()
  console.log('ruleNavBar', sortRule.value)
}

// 收藏夹列表
const favList = ref([])
// 当前收藏夹中的视频列表
const favVideoList = ref([])

// 获取用户收藏夹列表
const getFavList = async() => {
  const res = await get("/favorite/get-all/visitor", {
    params: { uid: uidVisited }
  })
  // 优先显示默认收藏夹
  const defaultFav = res.data.data.find(item => item.type === 1);
  const list = res.data.data.filter(item => item.type !== 1);
  list.unshift(defaultFav);
  favList.value = list
  console.log('favList', favList.value)
}

// 获取当前收藏夹中的视频列表
const getFavVideoList = async() => {
  favVideoList.value = []
  const res = await get("/video/user-collect", {
    params: {
      fid: leftNavBar.value,
      rule: sortRule.value,
      page: 1,
      quantity: 30
    }
  })
  favVideoList.value = res.data.data
  console.log('favVideoList', favVideoList.value)
}

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

onMounted(async() => {  
  await getFavList()
  await getFavVideoList()
})

// 当切换收藏夹时，更新当前收藏夹中的视频列表
watch(() => leftNavBar.value, (newVal) => {
  getFavVideoList()
  console.log('NOW leftNavBar', newVal)  
})
</script>

<style scoped>
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
  padding-bottom: 100px;
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

.title {
  margin: 15px 30px 10px;
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

.nav-bar-video {
  margin-left: 100px;
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

.nav-bar-video /deep/ .el-tabs__content {
  padding-left: 10px;
  padding-bottom: 0;
}

.fav-container {
  display: flex;
  justify-content: start;
}

.fav-box {
  display: block;
  float: left;
  width: 200px;
  padding: 15px;
}

.fav-cover {
  background: url('../../../assets/img/video-placeholder.png') 50%;
  background-size: cover;
  border-radius: 10px;
  display: block;
  width: 200px;
  height: 200px;
  overflow: hidden;
  position: relative;
}

.fav-cover img {
  display: block;
  width: 100%;
  height: 100%;
  /* 适应盒子尺寸，避免拉伸 */
  object-fit: cover;
  /* 保持锐利的边缘，避免模糊 */
  image-rendering: crisp-edges;
}

.fav-cover {
  display: inline-block;
  vertical-align: middle;
  -webkit-transform: perspective(1px) translateZ(0);
  transform: perspective(1px) translateZ(0);
  box-shadow: 0 0 1px rgba(0, 0, 0, 0);
  -webkit-transition-duration: 0.3s;
  transition-duration: 0.3s;
  -webkit-transition-property: box-shadow, transform;
  transition-property: box-shadow, transform;
}
.fav-cover:hover, .fav-cover:focus, .fav-cover:active {
  box-shadow: 0 10px 10px -10px rgba(0, 0, 0, 0.5);
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}

.fav-num {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 7px;
  text-align: center;
  color: #fff;
  background: rgba(0, 0, 0, 0.582);
  border-radius: 8px;
}

.fav-title {
  text-align: left;
  font-size: 18px;
  margin-top: 18px;
}

.fav-content {
  display: flex;
  justify-content: flex-start;
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

.video-hover {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 10;
  width: 100%;
  height: 100%;
  opacity: 0;
  background-color: rgba(0, 0, 0, 0.548);
  color: white;
  transition: opacity .2s ease;
}

.hover-text {
  font-size: 13px;
  line-height: 10px;
  text-align: left;
  padding-left: 20px;
  padding-top: 5px;
}

.video-cover:hover .video-hover {
  opacity: 1;
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
  margin-bottom: 5px;
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
  /* margin-top: 50px; */
  align-items: center;
  text-align: center;
  font-size: 20px;
  color: #999;
}

.up {
  cursor: pointer;
  transition: color .2s ease;
}

.up:hover {
  color: #166fe8;
}
</style>