<!-- 个人主页 -->
<template>
  <NavBar></NavBar>
  <div class="space-container">
    <!-- 用户基本信息 -->
    <div class="module">
      <!-- 背景头图 -->
      <div class="background" :style="`background-image: url('${userVisited.bg_url}');`"></div>
      <div class="user-info">
        <div>
          <!-- 头像 -->
          <div class="avatar">
            <div class="avatar-container">
              <img :src="userVisited.avatar_url" alt="" class="avatar-img" />
            </div>
          </div>
          <!-- 关注、粉丝、简介 -->
          <div class="info">
            <span class="nickname">{{ userVisited.nickname }}
              <el-icon :size="18"><Female v-if="userVisited.gender === 0"/></el-icon>
              <el-icon :size="18"><Male v-if="userVisited.gender === 1"/></el-icon>
            </span>
            <span class="fans-follows">关注了{{ userVisited.followsCount }}位up主 · 有{{ userVisited.fansCount }}个粉丝</span>
            <span class="desc">{{ userVisited.description }}</span>
          </div>
        </div>
        <!-- 关注按钮 -->
        <div v-if="userVisiting.uid !== userVisited.uid && store.state.isLogin === true">
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
    </div>
    <!-- 导航栏 -->
    <div class="module content">
      <div class="nav-bar">
        <el-tabs v-model="activityNavBar" @tab-click="handleClick">
          <el-tab-pane label="主页" name="home" ></el-tab-pane>
          <el-tab-pane label="动态" name="dynamic" ></el-tab-pane>
          <el-tab-pane label="投稿" name="upload" ></el-tab-pane>
          <el-tab-pane label="收藏" name="favlist" ></el-tab-pane>
          <el-tab-pane v-if="uidVisited === userVisiting.uid" label="观看历史" name="history" ></el-tab-pane>
          <el-tab-pane v-if="uidVisited === userVisiting.uid" label="个人设置" name="setting" ></el-tab-pane>
        </el-tabs>
      </div>
      <!-- 各个子项分别展示各自的界面 -->
      <router-view @userVistedChange="userVistedChange"></router-view>
    </div>
    
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus';
import NavBar from '@/components/NavBar.vue';
import { get, post } from '@/network/request'
import { computed, onMounted, ref, watch } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router'
// import axios from 'axios'

const store = useStore();
const route = useRoute();
const router = useRouter();

const isfollowing = ref(false)
const activityNavBar = ref('home')

const handleClick = (tab) => {
  activityNavBar.value = tab.props.name;
  const path = `/space/${userVisited.value.uid}/${tab.props.name}`
  if(path !== route.path) {
    router.push(path)
  }
}

// 获取当前被访问主页的用户
const uidVisited = Number(route.params.uid)
const userVisited = ref({});
const getUserVisited = async(uid) => {
  const res = await get("/user/info/get-one", {
    params: { uid: uid }
  })
  userVisited.value = res.data.data;
}

// 获取当前操作电脑的用户
const userVisiting = computed(() => {
  return store.state.user;
});

// 从后端更新visiting是否关注了visited
const getIsFollowing = async() => {
  if(!store.state.isLogin) return   // 防止一些报错
  const res = await get("/isFans", {
    params: {
      uidFollow: uidVisited,
      uidFans: userVisiting.value.uid,
    }
  })
  // 更新前端关注状态
  if(res.data) isfollowing.value = res.data.data;
}

// 向后端更新关注状态，还有前后端交互的bug！！
const updateIsFollowing = async() => {

  const formData = new FormData();
  formData.append("uidFollow", uidVisited);
  formData.append("uidFans", userVisiting.value.uid);
  formData.append("isfollowing", isfollowing.value);

  console.log('asdfasdf', uidVisited, userVisiting.value.uid, isfollowing.value)

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
  console.log('userVisited->', userVisited.value);
  console.log('userVisiting->', userVisiting.value);
  ElMessage.success("关注成功");
  await updateIsFollowing();
  isfollowing.value = true;
  userVisited.value.fansCount += 1;
}

// 取关成功
const followCancel = async() => {
  ElMessage.success("取关成功");
  await updateIsFollowing();
  isfollowing.value = false;
  userVisited.value.fansCount -= 1;
}

// 刷新用户信息，是SpaceSetting的回调函数
const userVistedChange = async() => {
  await getUserVisited(uidVisited)
}

// created
// 这个很关键：别的页面过来，create的时候看router.path，初始化activityNavBar
activityNavBar.value = route.path.split('/')[3];

onMounted(async ()=> {
  await getUserVisited(uidVisited)
  await getIsFollowing()
})

// 这里也很关键，需要不断监听当前页面是不是有router变化
watch(() => route.path, (newRoute) => {
  activityNavBar.value = newRoute.split('/')[3];
});

</script>

<style scoped>
.space-container {
  margin-top: 85px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  position: relative;
}

.module {
  /* 限制宽度，更美观 */
  width: 1200px;
  margin: 0 auto;
  position: relative;
}

.module .background {
  border-radius: 20px;
  width: 1200px;
  height: 200px;
  background-position: center;
  background-size: cover;
}

.module .user-info {
  margin-top: 30px;
  display: flex;
  -ms-flex-align: center;
  align-items: center;
  justify-content:space-between;
}

.content {
  border: 1px solid #eeeeee;
  background-color: white;
  margin-top: 20px;
  border-radius: 10px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background-color: #bcbcbc;
  display: flex;
  align-items: center;
  justify-content: center;
  float: left;
  box-shadow: var(--el-box-shadow-light);
}

.avatar-container {
  width: 120px;
  height: 120px;
  border-radius: 50%;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.info {
  margin-top: 10px;
  margin-left: 20px;
  float: left;
  text-align: left;
  display: flex;
  flex-direction: column;
}

.nickname {
  font-size: 32px;
  font-weight: bold;
  color: black;
  /* text-shadow: 1px 1px 10px #898989; */
}

.fans-follows {
  font-size: 14px;
  margin-top: 10px;
}

.desc {
  font-size: 16px;
  margin-top: 10px;
  color: black;
}

.follow-btn {
  font-size: 15px;
  margin-left: 20px;
  margin-right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 40px;
  border-radius: 10px;
  background-color: #f25d8e;
  cursor: pointer;
  color: white;
  transition: background-color 0.3s ease;
}

.follow-btn-cancel {
  font-size: 15px;
  margin-left: 20px;
  margin-right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 40px;
  border-radius: 10px;
  border: 2px solid #f25d8e;
  background-color: white;
  cursor: pointer;
  color: #f25d8e;
  transition: background-color 0.3s ease;
}

.nav-bar /deep/ .el-tabs__item {
  margin-left: 30px;
  margin-top: 10px;
  font-size: 18px;
}

.nav-bar /deep/ .el-tabs__active-bar {
  background-color: #166fe8; /* 修改底部横杠的颜色 */
  height: 2.5px; /* 修改底部横杠的高度 */
}

</style>