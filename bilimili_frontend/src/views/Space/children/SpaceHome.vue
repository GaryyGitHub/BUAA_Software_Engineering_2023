<!-- 个人主页-首页 -->
<template>
   <!-- 视频 -->
   <div class="container">
      <!-- 标题 -->
      <div class="title">
         <span class="title-text" @click="router.push(`/space/${uidVisited}/upload/video`)">
            {{ uidVisiting === uidVisited ? '我' : 'TA' }}的视频
            <span class="count">{{ videoList.length }}</span>
         </span>
         <span class="more" @click="router.push(`/space/${uidVisited}/upload/video`)">
            <el-icon :size="13"><ArrowRight /></el-icon> 更多
         </span>
      </div>
      <!-- 视频展示框 -->
      <div class="video-container" v-if="videoList.length > 0">
         <!-- 导航栏 -->
         <div class="nav-bar-video">
            <el-tabs v-model="ruleNavBar" @tab-click="handleClick" tab-position="left">
               <el-tab-pane label="最新发布" name="1" ></el-tab-pane>
               <el-tab-pane label="最多播放" name="2" ></el-tab-pane>
               <el-tab-pane label="最多点赞" name="3" ></el-tab-pane>
            </el-tabs>
         </div>
         <!-- 视频列表 -->
         <div class="list">
            <div class="video-box" v-for="(item, index) in videoList" :key="index">
               <!-- 封面 -->
               <a :href="`/video/${item.video.vid}`" target="_blank" class="video-cover">
                  <img :src="item.video.coverUrl">
                  <div class="video-duration">{{ handleTime(item.video.duration) }}</div>
               </a>
               <!-- 标题 -->
               <div @click="openNewPage(`/video/${item.video.vid}`)" target="_blank" class="video-title">{{ item.video.title }}</div>
               <!-- 播放量·时间 -->
               <div class="video-info">{{ item.stats.play }}次观看 · {{ handleDate(item.video.uploadDate) }}</div>
            </div>
         </div>
      </div>
      <div class="no-video" v-else>
         这里空空如也~{{ uidVisiting === uidVisited ? '快去创作你的第一条视频吧！' : 'UP还没有上传视频' }}
      </div>
   </div>
   <!-- 专栏 -->
   <div class="container">
      <div class="title">
         <span class="title-text" @click="router.push(`/space/${uidVisited}/upload/article`)">
            {{ uidVisiting === uidVisited ? '我' : 'TA' }}的专栏
            <span class="count">{{ articleCount }}</span>
         </span>
         <span class="more" @click="router.push(`/space/${uidVisited}/upload/article`)">
            <el-icon :size="13"><ArrowRight /></el-icon> 更多
         </span>
      </div>
      <!-- 专栏展示 -->
      <div class="video-container" v-if="articleCount > 0">
         <div class="list article-list">
            <div class="video-box article-box" v-for="(item, index) in articleList" :key="index">
               <a :href="`/article/${item.article.aid}`" target="_blank" class="video-cover article-cover">
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
   <!-- 收藏夹 -->
   <div class="container">
      <div class="title">
         <span class="title-text" @click="router.push(`/space/${uidVisited}/favlist`)">
            {{ uidVisiting === uidVisited ? '我' : 'TA' }}的收藏夹
            <span class="count">{{ favList.length }}</span>
         </span>
         <span class="more" @click="router.push(`/space/${uidVisited}/favlist`)">
            <el-icon :size="13"><ArrowRight /></el-icon> 更多
         </span>
      </div>
      <!-- 收藏夹展示 -->
      <div class="fav-container">
         <div class="list">
            <div class="fav-box" v-for="(item, index) in favList" :key="index">
               <!-- 封面，给外人展示公开的，自己看所有的 -->
               <a class="fav-cover" 
                  :href="`/space/${uidVisited}/favlist?fid=${item.fid}`" 
                  target="_blank" 
                  v-if="item.visible === 1 || item.uid === uidVisiting"
               >
                  <img v-if="item.cover" :src="item.cover">
                  <!-- 收藏夹内视频数量 -->
                  <div class="fav-num">{{ item.visible === 1 ? '公开' : '私密' }} · {{ item.count }}</div>
               </a>
               <div @click="openNewPage(`/space/${uid}/favlist?fid=${item.fid}`)" target="_blank" class="fav-title">{{ item.title }}</div>
            </div>
         </div>
      </div>
   </div>
</template>

<script setup>
import { handleTime, handleDate } from '@/utils/utils.js'
import { computed, ref, onMounted, } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { get } from '@/network/request'
// import axios from 'axios';

const route = useRoute()
const router = useRouter()
const store = useStore()

const uidVisited = Number(route.params.uid)
const uidVisiting = computed(() => {
  return store.state.user.uid;
});

const sortRule = ref(1)
const videoList = ref([])
const articleList = ref([])
// 只要length是0，后端就不传list了，因此前端要麻烦一点定义count
const articleCount = ref(0)
const favList = ref([])

const ruleNavBar = ref('1')

const handleClick = async(tab) => {
   ruleNavBar.value = tab.props.name
   sortRule.value = tab.props.name
   await getVideoList()
   // console.log(sortRule.value)
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

// 用户收藏夹
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

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

onMounted(async() => {
   await getVideoList()
   await getArticleList()
   await getFavList()
})

</script>

<style scoped>
.container {
   border-bottom: 1px solid #cccccc;
   margin-bottom: 20px;
   padding-bottom: 15px;
}

.title {
   margin: 15px 30px 20px;
   display: flex;
   align-items: center;
   justify-content: space-between;
   height: 30px;
}

.title-text {
   cursor: pointer;
   font-size: 24px;
   font-weight: bold;
   color: black;
   text-align: left;
   display: flex;
   align-items: center;
}

.count {
   text-align: center;
   margin-left: 10px;
   top: 50%;
   border: 2px solid #aaaaaa;
   color: #777;
   border-radius: 5px;
   font-size: 15px;
   padding: 1px 4px;
}

.more {
  width: 50px;
  cursor: pointer;
  position: relative;
  padding: 10px 20px;
  background: rgb(237, 237, 237);
  font-size: 14px;
  border-top-right-radius: 10px;
  border-bottom-left-radius: 10px;
  transition: all 0.5s;
}

.more::after,
.more::before {
  content: " ";
  width: 10px;
  height: 10px;
  position: absolute;
  border: 0px solid #fff;
  transition: all 0.5s;
}

.more::after {
  top: -1px;
  left: -1px;
  border-top: 3px solid black;
  border-left: 3px solid black;
}

.more::before {
  bottom: -1px;
  right: -1px;
  border-bottom: 3px solid black;
  border-right: 3px solid black;
}

.more:hover {
  border-top-right-radius: 0px;
  border-bottom-left-radius: 0px;
}
.more:hover::before,
.more:hover::after {
  width: 100%;
  height: 100%;
}

.video-container {
   display: flex;
   justify-content: flex-start;
}

.no-video {
   align-items: center;
   text-align: center;
   font-size: 20px;
   color: #999;
}

.nav-bar-video {
   margin: 20px 10px;
}

.nav-bar-video /deep/ .el-tabs__active-bar {
  background-color: #166fe8; /* 修改底部横杠的颜色 */
  height: 2.5px; /* 修改底部横杠的高度 */
}

.video-box {
   /* background-color: #9d0f0f; */
   display: block;
   float: left;
   width: 240px;
   margin: 0 0 10px;
   padding: 10px;
   height: 210px;
}

.video-cover {
   background: url('../../../assets/img/video-placeholder.png') 50%;
   background-size: cover;
   border-radius: 10px;
   display: block;
   width: 240px;
   height: 150px;
   overflow: hidden;
   position: relative;
   box-shadow: var(--el-box-shadow);
}

.article-box {
   width: 260px;
   margin: 0 4px 15px;
}

.article-cover {
   width: 260px;
   height: 170px;
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
   font-size: 17px;
   margin-top: 12px;
   transition: color .2s ease;
}

.video-title:hover {
   color: #166fe8;
}

.video-info {
   text-align: left;
   font-size: 13px;
   color: #999;
   margin-top: 5px;
}

.article-list {
   margin-left: 25px;
}

.fav-container {
   display: flex;
   justify-content: flex-start;
   margin-left: 15px;
}

.fav-box {
   /* background-color: #9d0f0f; */
   display: block;
   float: left;
   width: 200px;
   margin: 0 0 5px;
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
   box-shadow: var(--el-box-shadow);
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
   cursor: pointer;
   text-align: left;
   font-size: 18px;
   margin-top: 8px;
   transition: color .2s ease;
}

.fav-title:hover {
   color: #166fe8;
}
</style>