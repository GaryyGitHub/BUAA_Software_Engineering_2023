<!-- 个人主页-动态 -->
<template>
  <div class="container">
    <!-- 标题 -->
    <div class="title">
      <span class="title-text">
        {{ uidVisiting === uidVisited ? '我' : 'TA' }}的动态
      </span>
    </div>
    <!-- 发布输入框，仅当前visted===visiting时显示 -->
    <div class="input" v-if="uidVisiting === uidVisited">
      <span class="input-label">发布动态：</span>
      <div class="input-right">
        <el-input class="input-box" type="textarea" clearable :autosize="{ minRows: 2, maxRows: 5 }" v-model="newContent" placeholder="有什么想和大家分享的？发一条动态吧~"/>
        <div class="confirm">
          <div class="confirm-botton" @click="uploadDynamic">发布</div>
        </div>
      </div>
    </div>
    <!-- 动态具体内容 -->
    <div class="dynamic-content">
      <div class="no-content" v-if="dynamicList.length === 0">
        {{ uidVisiting === uidVisited ? '你还没有发过动态，快去发一条吧~' : 'TA还没有发过动态~' }}
      </div>
      <div class="dynamic-box" v-for="(item, index) in dynamicList" :key="index">
        <div class="dynamic-right">
          <!-- 头像 -->
          <div class="avatar">
            <a :href="`/space/${item.user.uid}/home`" target="_blank"  class="avatar-container">
              <img :src="item.user.avatar_url" alt="" class="avatar-img" />
            </a>
          </div>
          <!-- 文字 -->
          <div class="dynamic-text">
            <!-- 用户名字 -->
            <div class="dynamic-info" @click="openNewPage(`/space/${item.user.uid}/home`)">
              {{ item.user.nickname }}
            </div>
            <!-- 具体内容 -->
            <div class="dynamic-detail">
              {{ item.content }}
            </div>
            <!-- 发布时间 -->
            <div class="dynamic-time">
              {{ handleDateTime3(item.createDate) }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <span class="dixian">-- 你已试探到我的底线 --</span>
</template>

<script setup>
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import { computed, onMounted, ref, } from 'vue';
import { ElMessage } from 'element-plus';
import { post, get } from '@/network/request'
import { handleDateTime3 } from '@/utils/utils.js';

const store = useStore();
const route = useRoute()
const router = useRouter()

const uidVisited = Number(route.params.uid)
const uidVisiting = computed(() => {
  return store.state.user.uid;
});

// 新发布的动态内容
const newContent = ref('');
// 动态内容列表，按时间倒序排列
const dynamicList = ref([]);

// 向后端上传动态
const uploadDynamic = async() => {
  if(newContent.value === '') {
    ElMessage.warning('动态内容不能为空~');
    return
  }
  const formData = new FormData();
  formData.append('uid', uidVisited);
  formData.append('content', newContent.value);
  const res = await post("/post/add", formData, {
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token"), }
  })
  console.log('UploadDynamic', res)
  if(!res.data) return
  newContent.value = ''
  ElMessage.success('动态发布成功~')
  await getDynamic()
}

// 获取动态内容
const getDynamic = async() => {
  const res = await get('/post/get', {
    params: {
      uid: uidVisited,
    },
  })
  console.log('GetDynamic', res)
  if(!res.data) return
  let tempList = res.data.data
  dynamicList.value = tempList.slice().reverse();
  console.log('dynamicList', dynamicList.value)
}

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

onMounted(async() => {
  await getDynamic()
})

</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
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

.input {
  width: 1000px;
  margin: 15px 50px 0;
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
  align-items: flex-end
}

.input-box {
  width: 850px;
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

.dynamic-content {
  margin-top: 10px;
  display: flex;
  justify-content: start;
  align-items: center;
  flex-direction: column;
  position: relative;
}

.dynamic-box {
  margin-top: 15px;
  display: flex;
  justify-content: start;
  flex-direction: row;
  position: relative;
  width: 1000px;
  border-radius: 10px;
  border: 1px solid #fafafa;
  box-shadow: var(--el-box-shadow-light);
}

.dynamic-right {
  margin: 20px 50px 20px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #bcbcbc;
  display: flex;
  align-items: center;
  justify-content: center;
  float: left;
  box-shadow: var(--el-box-shadow-lighter)
}

.avatar-container {
  width: 60px;
  height: 60px;
  border-radius: 50%;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.dynamic-text {
  display: flex;
  justify-content: flex-start;
  flex-direction: column;
}

.dynamic-info {
  display: flex;
  align-self: flex-start;
  cursor: pointer;
  margin-left: 20px;
  color: #686868;
  font-size: 16px;
}

.dynamic-detail {
  line-height: 28px;
  color: black;
  font-size: 17px;
  margin-left: 20px;
  margin-top: 15px;
  max-width: 790px;
  text-align: left;
}

.dynamic-time {
  align-self: flex-start;
  color: #999;
  font-size: 14px;
  margin-left: 20px;
  margin-top: 15px;
}

.dixian {
  font-size: 13px;
  color: #999;
}

.no-content {
  font-size: 24px;
  color: black;
  text-align: center;
  font-weight: bold;
}
</style>