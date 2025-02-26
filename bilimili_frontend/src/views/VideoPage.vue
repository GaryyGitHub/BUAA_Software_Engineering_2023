<!-- 视频详情 测试用uid：15-->
<template>
  <NavBar></NavBar>
  <div class="container">
    <div class="content">
      <!-- 上部：视频标题，UP -->
      <el-row :gutter="0" justify="space-evenly">
        <!-- 左上：视频标题相关 -->
        <el-col :span="17" class="title-left">
          <span class="title">{{ videoInfo.title }}</span>
          <span class="title-info">播放量：{{ statsInfo.play }} · 弹幕量：{{ statsInfo.danmu }}</span>
          <span class="title-info">发布时间：{{ videoInfo.uploadDate }}</span>
        </el-col>
        <!-- 右上：UP主信息 -->
        <el-col :span="7" class="title-right">
          <div class="avatar">
            <a :href="`/space/${upInfo.uid}/home`" target="_blank" class="avatar-container">
              <img :src="upInfo.avatar_url" alt="" class="avatar-img" />
            </a>
          </div>
          <div class="up-info">
            <span class="up-name" @click="openNewPage(`/space/${upInfo.uid}/home`)">{{ upInfo.nickname }}</span>
            <span class="up-desc" >{{ upInfo.description }}</span>
            <div v-if="userVisiting.uid !== upInfo.uid && store.state.isLogin === true">
              <div class="follow-btn" v-if="isfollowing === false" @click="followSuccess">
                <el-icon :size="18" style="margin-right: 5px;"><CirclePlusFilled /></el-icon>
                <span>关注</span>
              </div>
              <div class="follow-btn-cancel" v-else @click="followCancel">
                <el-icon :size="18" style="margin-right: 5px;"><CircleCheckFilled /></el-icon>
                <span>已关注</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <!-- 下部：视频详情 -->
      <el-row :gutter="0" justify="space-evenly">
        <!-- 左下：视频+评论 -->
        <el-col :span="17" class="content-left">
          <!-- 视频组件 -->
          <PlayerWrapper 
            style="margin-left: 40px;"
            :videoUrl="videoInfo.videoUrl"
            :title="videoInfo.title"
            :duration="videoInfo.duration"
            :user="upInfo"
            >
          </PlayerWrapper>
          <!-- 交互 -->
          <div class="interaction">
            <!-- 点赞 -->
            <div class="interaction-item" :class="{'pink': hasLiked}" @click="changeLike">
              <!--el-icon :size="30"><Flag /></el-icon> -->
              <i class="iconfont icon-dianzan"></i>
              <span class="interaction-text">点赞</span>
            </div>
            <!-- 收藏 -->
            <div class="interaction-item" :class="{'pink': hasFaved}" @click="favoriteVisible = true">
              <!--<el-icon :size="30"><StarFilled /></el-icon> -->
              <i class="iconfont icon-shoucang" :class="{'pink': hasFaved}" @click="favoriteVisible = true"></i>
              <span class="interaction-text">收藏</span>
            </div>
          </div>
          <!-- 评论区 -->
          <div class="comment">
            <!-- 输入框 -->
            <div class="input" v-if="store.state.isLogin === true">
              <span class="input-label">发表评论</span>
              <div class="input-right">
                <el-input class="input-box" type="textarea" clearable :autosize="{ minRows: 2, maxRows: 5 }" v-model="newComment" placeholder="新的风暴已经出现，你的秒评何时出现 :-)"/>
                <div class="confirm">
                  <div class="confirm-botton" @click="uploadComment">发布</div>
                </div>
              </div>
            </div>
            <div v-else style="color: black; font-size: 16px; text-align: center; margin-top: 20px;">请先登录再发表评论吧~</div>
            <!-- 评论具体内容 -->
            <div class="comment-content">
              <span class="comment-title" >评论 - 共{{ commentList.length }}条</span>
              <div class="no-content" v-if="commentList.length === 0">还没人发过评论，快来勇闯无人区~</div>
              <div class="comment-box" v-else v-for="(item, index) in commentList" :key="index">
                <div class="comment-right">
                  <!-- 头像 -->
                  <div class="avatar" style="float: left;">
                    <a :href="`/space/${item.user.uid}/home`" target="_blank"  class="avatar-container">
                      <img :src="item.user.avatar_url" alt="" class="avatar-img" />
                    </a>
                  </div>
                  <!-- 文字 -->
                  <div class="comment-text">
                    <!-- 用户名字 -->
                    <div class="comment-info" @click="openNewPage(`/space/${item.user.uid}/home`)">
                      {{ item.user.nickname }}
                    </div>
                    <!-- 具体内容 -->
                    <div class="comment-detail">
                      {{ item.content }}
                    </div>
                    <!-- 发布时间 -->
                    <div class="comment-time">
                      {{ handleDateTime3(item.createTime) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <span class="dixian" v-if="commentList.length > 0">-- 你已试探到我的底线 --</span>
          </div>
        </el-col>
        <!-- 右下：推荐视频 -->
        <el-col :span="7" class="content-right">
          <span class="recommend-title">猜你想看：</span>
          <div class="video-container" v-if="otherVideoList.length > 0">
            <div>
              <div class="video-content" v-for="(item, index) in otherVideoList" :key="index">
                <div class="video-box">
                  <a :href="`/video/${item.video.vid}`" target="_blank" class="video-cover">
                    <img :src="item.video.coverUrl">
                    <div class="video-duration">{{ handleTime(item.video.duration) }}</div>
                  </a>
                </div>
                <div class="video-right">
                  <div @click="openNewPage(`/video/${item.video.vid}`)" target="_blank" class="video-title">{{ item.video.title }}</div>
                  <div class="video-info">
                    {{ item.stats.play }}次观看 · {{ item.stats.good }}次点赞 · {{ handleDate(item.video.uploadDate) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <el-dialog v-model="favoriteVisible" :close-on-click-modal="false" destroy-on-close align-center>
        <FavoriteBox :already-selected="favSelectedList" :vid="vid" @favorite="updateCollect"/>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import PlayerWrapper from '@/components/Video/PlayerWrapperNew.vue'
import { handleTime, handleDate, handleDateTime3, } from '@/utils/utils.js'
import NavBar from '@/components/NavBar.vue';
import FavoriteBox from '@/components/FavoriteBox.vue';
import { onMounted, ref, computed } from 'vue';
import { get, post } from '@/network/request'
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex';

const store = useStore()
const route = useRoute()
const router = useRouter()

// ------------------关注相关------------------
const isfollowing = ref(false)
const userVisiting = computed(() => {
  return store.state.user;
});
// 向后端更新关注状态，还有前后端交互的bug！！
const updateIsFollowing = async() => {
  const formData = new FormData();
  formData.append("uidFollow", upInfo.value.uid);
  formData.append("uidFans", userVisiting.value.uid);
  formData.append("isFollowing", isfollowing.value);

  console.log('asdfasdf', upInfo.value.uid, userVisiting.value.uid, isfollowing.value)

  const res = await post('/following/update', formData, {
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
  })
  // const res = await axios.post("/following/update", {
  //   uidFollow: uidVisited,
  //   uidFans: userVisiting.value.uid,
  //   isFollowing: isfollowing.value,
  // }, {
  //   headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
  // })
  console.log('res_update->', res);
}
// 关注成功的反馈
const followSuccess = async() => {
  console.log('userVisiting->', userVisiting.value);
  ElMessage.success("关注成功");
  await updateIsFollowing();
  isfollowing.value = true;
}
// 取关成功
const followCancel = async() => {
  ElMessage.success("取关成功");
  await updateIsFollowing();
  isfollowing.value = false;
}

// ------------------视频功能相关------------------
const videoInfo = ref({})
const statsInfo = ref({})
const categoryInfo = ref({})
const upInfo = ref({})
const otherVideoList = ref([]);

const vid = ref(route.params.vid)

// 获取视频详情信息
const getVideoInfo = async() => {
  const res = await get('/video/getone', {
    params: { vid: vid.value }
  })
  videoInfo.value = res.data.data.video
  statsInfo.value = res.data.data.stats
  categoryInfo.value = res.data.data.category
  upInfo.value = res.data.data.user

  console.log('videoInfo', videoInfo.value)
  console.log('statsInfo', statsInfo.value)
  console.log('categoryInfo', categoryInfo.value)
  console.log('upInfo', upInfo.value)
}

// 获取随机推荐
const getOtherVideos = async () => {
  const res = await get("/video/cumulative/visitor", {
    params: {vids: ""}
  })
  otherVideoList.value = res.data.data.videos;
  console.log('getOtherVideos', otherVideoList.value)
}
    
// ------------------收藏功能相关------------------
// 显示收藏弹窗
const favoriteVisible = ref(false);
// 已经在其中的收藏夹列表
const favSelectedList = ref([])
const hasFaved = ref(false)
const hasLiked = ref(false)

const changeLike = () => {
  hasLiked.value = !hasLiked.value
}

// 获取当前用户收藏了视频的收藏夹列表，可与icon效果挂钩
const getCollectedFids = async() => {
  const res = await get("/video/collected-fids", {
    params: {vid: vid.value},   // 模拟vid=3的情况
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
  })
  const temp = res.data.data
  temp.sort()
  favSelectedList.value = temp
  if(favSelectedList.value.length > 0) {
    hasFaved.value = true
  }
  console.log('favSelectedList', favSelectedList.value)
}

// 收藏成功的反馈
const updateCollect = (info) => {
  favSelectedList.value = info.fids
  let favoriteChange = info.num
  console.log('favOfVideoList->', favSelectedList.value, 'favoriteChange->', favoriteChange)
  favoriteVisible.value = false
  // 此处还可有icon变色的效果
  ElMessage.success('收藏成功')
}

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

//------------------评论相关------------------
// 获取当前视频评论
const commentList = ref([])
const getComment = async() => {
  const res = await get("/comment/get", {
    params: {
      vid: vid.value,
      offset: 0,
      type: 2,
    }
  })
  commentList.value = res.data.data.comments
  console.log('commentList', commentList.value)
}

// 上传评论
const newComment = ref('')
const uploadComment = async() => {
  if(newComment.value === '') {
    ElMessage.error('评论不能为空')
    return
  }
  console.log('newComment', newComment.value)
  const formData = new FormData();
  formData.append("vid", vid.value);
  formData.append("root_id", 0);
  formData.append("parent_id", 0);
  formData.append("to_user_id", upInfo.value.uid)
  formData.append("content", newComment.value);
  const res = await post("/comment/add", formData, {
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token"), }
  })
  console.log('uploadComment', res)
  if(!res.data) return
  newComment.value = ''
  ElMessage.success('评论发布成功~')
  await getComment()
}

onMounted(async() => {
  await getVideoInfo()
  await getCollectedFids()
  await getOtherVideos()
  await getComment()
  console.log('vid->', vid.value)

})
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 85px;
}

.content {
  border-radius: 10px;
  background-color: #fff;
  border: 1px solid #eeeeee;
  width: 1500px;
}

.title-left {
  margin-top: 30px;
  display: flex;
  justify-content: start;
  align-items: start;
  flex-direction: column;
}

.title {
  text-align: left;
  margin-left: 30px;
  font-size: 24px;
  color: black;
}

.title-info {
  margin-top: 10px;
  text-align: left;
  color: #848484;
  margin-left: 40px;
  font-size: 14px;
}

.title-right {
  margin-top: 30px;
  display: flex;
  justify-content: start;
  align-items: start;
  flex-direction: row;
}

.avatar {
  cursor: pointer;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #bcbcbc;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--el-box-shadow);
}

.avatar-container {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.avatar-img {
  position: relative;
  display: inline-block;
  line-height: 1;
  width: 100%;
  height: 100%;
  object-fit: cover;
  vertical-align: middle;
  border-radius: 50%;
  background-color: transparent;
}

.up-info {
  display: flex;
  justify-content: start;
  flex-direction: column;
  margin-left: 15px;
}

.up-name {
  text-align: left;
  cursor: pointer;
  color: black;
  font-size: 20px;
}

.up-name:hover {
  color: #166fe8;
}

.up-desc {
  text-align: left;
  margin-top: 10px;
  color: #848484;
  font-size: 14px;
}

.follow-btn {
  margin-top: 10px;
  font-size: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 35px;
  border-radius: 10px;
  background-color: #f25d8e;
  border: 2px solid #f25d8e;
  cursor: pointer;
  color: white;
  transition: background-color 0.3s ease;
}

.follow-btn-cancel {
  margin-top: 10px;
  font-size: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 35px;
  border-radius: 10px;
  border: 2px solid #f25d8e;
  background-color: white;
  cursor: pointer;
  color: #f25d8e;
  transition: background-color 0.3s ease;
}

.content-left{
  margin-top: 5px;
  display: flex;
  justify-content: start;
  align-items: start;
  flex-direction: column;
}

.interaction {
  display: flex;
  justify-content: start;
  align-items: center;
  margin-top: 10px;
  margin-left: 40px;
  margin-bottom: 20px;
  flex-direction: row;
  color: black;
}

.interaction-item {
  /* 此法使得icon和文字对齐 */
  cursor: pointer;
  margin-right: 30px;
  display: flex;
  align-items: center;
  transition: color .2s ease;
}

.interaction-item:hover {
  color: #f25d8e;
}

.pink {
  color: #f25d8e;
}

.interaction-text {
  font-size: 18px;
  margin-left: 5px;
}

.comment {
  margin-left: 40px;
  border-top: 1px solid #eeeeee;
}

.input {
  margin: 15px 10px 0;
  display: flex;
  justify-self: start;
  align-items: start;
}

.input-label {
  margin-top: 10px;
  font-size: 17px;
  margin-right: 15px;
  color: black;
}

.input-right {
  display: flex;
  justify-self: start;
  flex-direction: column;
  align-items: flex-end;
}

.input-box {
  width: 800px;
  box-shadow: var(--el-box-shadow-light);
}

.input-box >>> .el-textarea__inner {
  padding-left: 15px;
  line-height: 30px;
  font-family: 'Harmony Font';
  font-size: 16px !important;
  border: 1px solid #166fe8;
  border-radius: 8px;
}

.confirm {
  margin-top: 20px;
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
  transition: color 0.2s ease;
}

.confirm-botton:hover {
  background-color:#f25d8e;
  color: white;
}

.comment-content {
  margin-top: 10px;
  display: flex;
  justify-content: start;
  align-items: start;
  flex-direction: column;
  position: relative;
}

.comment-title {
  color: black;
  font-size: 20px;
  text-align: left;
}

.no-content {
  margin-top: 20px;
  font-size: 18px;
  color: black;
  text-shadow: 1px 1px 1px #dedede;
}

.comment-box {
  margin-top: 15px;
  display: flex;
  justify-content: start;
  flex-direction: row;
  position: relative;
  width: 900px;
  border-radius: 10px;
  border: 1px solid #fafafa;
  box-shadow: var(--el-box-shadow-light);
}

.comment-right {
  margin: 20px 20px 20px;
}

.comment-text {
  display: flex;
  justify-content: flex-start;
  flex-direction: column;
}

.comment-info {
  display: flex;
  align-self: flex-start;
  cursor: pointer;
  margin-left: 20px;
  color: #686868;
  font-size: 16px;
}

.comment-detail {
  line-height: 28px;
  color: black;
  font-size: 16px;
  margin-left: 20px;
  margin-top: 10px;
  max-width: 790px;
  text-align: left;
}

.comment-time {
  align-self: flex-start;
  color: #999;
  font-size: 14px;
  margin-left: 20px;
  margin-top: 10px;
}

.dixian {
  font-size: 13px;
  margin-top: 40px;
  color: #999;
}

.content-right {
  margin-top: 30px;
  display: flex;
  justify-content: start;
  align-items: start;
  flex-direction: column;
}

.recommend-title {
  font-size: 24px;
  color: black;
}

.video-container {
  display: flex;
  justify-content: flex-start;
}

.video-content {
  display: flex;
  flex-direction: row;
  justify-content: start;
}

.video-box {
  padding: 10px 0 10px;
  height: 120px;
}

.video-right {
  margin-left: 5px;
  margin-right: 10px;
  flex-direction: column;
}

.video-cover {
  background: url('../assets/img/video-placeholder.png') 50%;
  background-size: cover;
  border-radius: 10px;
  display: block;
  width: 220px;
  height: 120px;
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

.a {
  display: block;
  background-color: #cd8181;
  margin-top: 800px;
}
</style>