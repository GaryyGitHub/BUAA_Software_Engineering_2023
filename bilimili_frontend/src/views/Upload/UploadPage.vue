<!-- 投稿页-主页面 -->
<template>
  <NavBar></NavBar>
  <!-- 导航栏，这次用el-menu罢 -->
  <div class="container">
    <el-menu
      :default-active="route.path.split('/')[2]"
      class="el-menu-vertical"
      background-color="#eeeeee" router
    >
      <el-sub-menu index="detail">
        <template #title>
          <el-icon :size="20"><Upload /></el-icon>
          <span style="margin-left: 10px;">投稿</span>
        </template>
        <el-menu-item index="video">
          <el-icon :size="20"><VideoCameraFilled /></el-icon>
          <span style="margin-left: 10px;">视频投稿</span>
        </el-menu-item>
        <el-menu-item index="article">
          <el-icon :size="20"><Tickets /></el-icon>
          <span style="margin-left: 10px;">专栏投稿</span>
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item index="data">
        <el-icon :size="20"><Opportunity /></el-icon>
        <span style="margin-left: 10px;">创作中心</span>
      </el-menu-item>
    </el-menu>
  </div>
  <router-view></router-view>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue';
import { useRoute } from 'vue-router'

const route = useRoute();

</script>

<style scoped>
.container {
  margin-top: 85px;
}

.el-menu-vertical {
  display: block;
  position: absolute;
  height: 812px;
  width: 210px;
  left: 0;
  top: 85px;
}

.container >>> .el-sub-menu__title {
  background-color: white  !important;
  font-size: 18px;
  height: 60px;
}

.container >>> .el-menu-item {
  background-color: white  !important;
  font-size: 18px;
  height: 60px;
}

.container >>> .el-aside {
  color: black;
}

.container >>> .el-menu-item:hover{
  outline: 0 !important;
  color: #166fe8 !important;
}
.container >>> .el-menu-item.is-active {
  color: #fff !important;
  background: #166fe8 !important;
}
</style>