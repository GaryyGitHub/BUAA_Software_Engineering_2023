<!-- 上方导航栏 -->
<template>
  <div :class="isFixedBar ? 'navbar-fixed' : 'navbar'">
    <!-- 左侧导航栏 -->
    <div class="navbar-left">
      <a href="/">
        <img src="../assets/img/logo-pink.png" alt="" class="logo" 
          :style="imgStyle"
          @mouseenter="imgHovered = true"
          @mouseleave="imgHovered = false"/>
      </a>
      <a class="navbar-item" href="/">
        <el-icon :size="20"><House /></el-icon>
        <span class="navbar-item-text">首页</span>
      </a>
      <div class="navbar-item" @click="isLogin ? openNewPage('/message') : handleLogin()">
        <el-icon :size="20"><Message /></el-icon>
        <span class="navbar-item-text">消息</span>
      </div>
      <div class="navbar-item" @click="isLogin ? openNewPage(`/space/${user.uid}/dynamic`) : handleLogin()">
        <el-icon :size="20"><MagicStick /></el-icon>
        <span class="navbar-item-text">动态</span>
      </div>
      <div class="navbar-item" @click="isLogin ? openNewPage(`/space/${user.uid}/favlist`) : handleLogin()">
        <el-icon :size="20"><Star /></el-icon>
        <span class="navbar-item-text">收藏</span>
      </div>
      <div class="navbar-item" @click="isLogin ? openNewPage(`/space/${user.uid}/history`) : handleLogin()">
        <el-icon :size="20"><Clock /></el-icon>
        <span class="navbar-item-text">历史</span>
      </div>
      <div class="navbar-item" style="min-width: 65px" @click="isLogin ? openNewPage('/upload/data') : handleLogin()">
        <el-icon :size="20"><Opportunity /></el-icon>
        <span class="navbar-item-text">创作中心</span>
      </div>
    </div>

    <!-- 搜索框 -->
    <div class="navbar-middle" :style="props.isShowInputBox ? '' : 'background-color: #333;'">
      <div :style="props.isShowInputBox ? '' : 'display: none;'">
      <el-input clearable v-model="searchText" class="search-box" size="large" placeholder="要搜些什么呢..."
        @keyup.enter="handleSearch()"
        @focus="showPop()" >
        <!-- TODO: handleSearch(), showPop() -->
        <!-- 在搜索框的后面插入搜索icon  -->
        <template #append>
            <el-icon :size="25" style="cursor: pointer;" @click="handleSearch()"><Search /></el-icon>
        </template>
      </el-input>
      </div>
    </div>

    <!-- 用户头像和投稿按钮 -->
    <div class="navbar-right">
      <!-- 未登录 -->
      <div class="avatar" v-if="!isLogin">
        <div class="login-botton" @click="handleLogin()">登录</div>
      </div>
      <!-- 已登录 -->
      <div v-else>
        <el-popover
          placement="bottom"
          width="200"
          trigger="hover"
        >
          <div class="popover">
            <div class="poopover-user-name">{{ user.nickname }}</div>
            <div class="user-info">
              <div class="user-info-item">
                <span class="item1">{{ user.followsCount }}</span>
                <span class="item2">关注</span>
              </div>
              <div class="user-info-item">
                <span class="item1">{{ user.fansCount }}</span>
                <span class="item2">粉丝</span>
              </div>
            </div>
            <div class="popover-choices" @click="openNewPage(`/space/${user.uid}/home`)">
              <el-icon :size="20"><User /></el-icon>
              <span class="popover-content">个人主页</span>
            </div>
            <div class="popover-choices" @click="openNewPage(`/space/${user.uid}/upload`)">
              <el-icon :size="20"><Document /></el-icon>
              <span class="popover-content">投稿详情</span>
            </div>
            <div class="popover-choices" @click="openNewPage(`/space/${user.uid}/setting`)">
              <el-icon :size="20"><Edit /></el-icon>
              <span class="popover-content">修改信息</span>
            </div>
            <div class="popover-choices" @click="logout">
              <el-icon :size="20"><SwitchButton /></el-icon>
              <span class="popover-content">退出登录</span>
            </div>
          </div>
          <template #reference>
            <div class="avatar">
              <a :href="`/space/${user.uid}/home`" target="_blank" class="avatar-container">
                <img :src="user.avatar_url" alt="" class="avatar-img" />
              </a>
            </div>
          </template>
        </el-popover>
      </div>
      <!-- 投稿按钮 -->
      <div class="upload" @click="isLogin ? openNewPage('/upload/video') : handleLogin()">
        <el-icon :size="18" style="margin-right: 5px;"><Upload /></el-icon>
        <div>投稿</div>
      </div>
    </div>

    <!-- 弹出登录框 -->
     <el-dialog v-model="loginBoxVisible" :close-on-click-modal="false" align-center destroy-on-close>
       <LoginRegister @success="loginBoxVisible = false"/>
     </el-dialog>
  </div>

</template>

<script setup>
import LoginRegister from '@/components/LoginRegister.vue';
import { ref, computed, defineProps} from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router'
// import { ElMessage } from 'element-plus';

const searchText = ref('');    // 搜索框内容
const imgHovered = ref(false); // 鼠标是否悬停在logo上
const loginBoxVisible = ref(false); // 登录框是否可见，初始为false

const store = useStore();
const router = useRouter();

const props = defineProps({
  isShowInputBox: {
    type: Boolean,
    default: true
  },
  isFixedBar: {
    type: Boolean,
    default: true,
  }
});

// 从store中获取登录状态
const isLogin = computed(() => {
  return store.state.isLogin;
});

// 从store中获取用户信息
const user = computed(() => {
  return store.state.user;
});

const logout = () => {
  store.dispatch("logout");
};
// 处理搜索事件
const handleSearch = () => {
  let searchTextValue = searchText.value.trim();
  console.log('搜索内容：' + searchTextValue);
  console.log(user.value)// 打印一下现在的用户
  searchText.value = '';       // 清空搜索框内容
  if(searchTextValue === ''){  // 如果搜索框内容为空，则跳转到搜索默认页面
    openNewPage("/search")
  } else {
    openNewPage(`/search?keyword=${searchTextValue}`)
  }
};

// 展示推荐内容，暂时不做
const showPop = () => {
  console.log('需要显示推荐内容');
};

// 展示登录框或者登录界面
const handleLogin = () => {
  loginBoxVisible.value = true;
  console.log('应当弹出登录框', isLogin.value);
};

// 打开新页面
const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

// 图片缩放，花里胡哨
const imgStyle = computed(() => {
  return {
    transform: imgHovered.value ? "scale(1.1) scale(1)" : 'scale(1)',
    transition: 'transform 0.2s ease-in-out',
  };
});
</script>

<style scoped>
/* 跳动效果 */
@keyframes colorChange {
  0%, 100% {
    color: white;
  }
  50% {
    color: rgb(230, 120, 138);
  }
}
@keyframes jump {
  0%, 100% {
    transform: translateY(0); /* 起始和结束状态，图标回到原位 */
  }
  50% {
    transform: translateY(-7px); /* 中间状态，图标向上跳动 20px */
  }
}

.navbar {
  z-index: 1002;
  position: absolute ;
  box-sizing: border-box;
  width: 100%;
  /* max-width: 2560px; */
  top: 0;
  left: 0;
  /* 上面两行让NavBar顶头显示 */
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  padding: 15px 20px;
  background-color: #333;
  color: white;
  font-size: 16px;
}

.navbar-fixed {
  z-index: 1002;
  position: fixed ;
  box-shadow: 0 2px 4px black;
  box-sizing: border-box;
  width: 100%;
  /* max-width: 2560px; */
  top: 0;
  left: 0;
  /* 上面两行让NavBar顶头显示 */
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  padding: 15px 20px;
  background-color: #333;
  color: white;
  font-size: 16px;
}

.navbar-left {
  margin-left: 20px;
  margin-right: 150px;
  display: flex;
  align-items: center;
}

.navbar-item {
  cursor: pointer;
  text-decoration: none;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 25px;
  min-width: 35px;
}

.navbar-item-text {
  margin-top: 5px;
  text-decoration: none;
  color: white;
}

.navbar-item:hover {
  animation: colorChange 0.4s;
  text-decoration: underline;
  text-decoration-color: rgb(230, 120, 138);
  text-shadow: 2px 2px 3px rgb(230, 120, 138);
}

.logo {
  height: 100%;
  max-height: 50px; /* 你可以根据需要调整最大高度 */
  object-fit: contain;
  margin-right: 50px;
}

.navbar-middle {
  width: 500px;
  background-color: #bcbcbc;
  padding: 3px;
  border-radius: 20px;
  margin-right: 150px;
  transition: background-color 0.2s ease;
}

.navbar-middle:focus-within {
  background-color: white;
}

.search-box /deep/ .el-input__wrapper {
  width: 420px;
  font-size: 16px;
  border-radius: 20px;
  border:0;
  box-shadow: 0 0 0 0px;
}

.search-box /deep/ .el-input__inner {
  font-family: 'Harmony Font';
}

.search-box /deep/ .el-input-group__append {
  background-color: white;
  border-radius: 20px;
  border:0;
  box-shadow: 0 0 0 0px;
  transition: background-color 0.2s ease;
}

.search-box /deep/ .el-input-group__append:hover {
  background-color: #dedede;
}

.navbar-right {
  flex-grow: 1;
  display: flex;
  justify-content: space-evenly;
}

.input-div {
  width: 500px;
  border-radius: 95px;
  background-color: #fff;
  border-radius: 95px;
  margin-left: 33%;
  margin-top: 2%;
}

.popover {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.poopover-user-name {
  font-size: 20px;
  margin: 10px;
  color: black;
  font-weight: bold;
}

.user-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 15px;
}

.user-info-item {
  display: flex;
  flex-direction: column;
  padding-left: 15px;
  padding-right: 15px;
  align-items: center;
}

.item1 {
  font-size: 20px;
  color: black;
}

.item2 {
  font-size: 12px;
  color: #999;
}

.popover-choices {
  width: 120px;
  padding-left: 20px;
  padding-right: 20px;
  padding-top: 10px;
  padding-bottom: 10px;
  display: flex;
  justify-content: space-evenly;
  cursor: pointer;
  border-radius: 10px;
}

.popover-choices:hover {
  background-color: #e8e8e8;
}

.popover-content {
  font-size: 15px;
}

.avatar {
  cursor: pointer;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #bcbcbc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar:hover {
  animation: jump 0.35s;
}

.login-botton {
  z-index: 2;
  color: white;
  font-size: 15px;
}

.avatar-container {
  width: 50px;
  height: 50px;
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

.upload {
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

.upload:hover {
  background-color:hotpink;
}

</style>

<!-- 对组件风格的改动不能使用scoped -->
<style>
.el-dialog {
  position: relative;
  width: initial;
  border-radius: 15px;
}

.el-dialog__headerbtn {
  z-index: 3000;
  top: 20px;
  right: 20px;
  width: 30px;
  height: 30px; 
}

.el-dialog__close {
  font-size: 20px !important;
}

.el-dialog__body {
  position: relative;
  width: 100%;
  height: 100%;
}
</style>