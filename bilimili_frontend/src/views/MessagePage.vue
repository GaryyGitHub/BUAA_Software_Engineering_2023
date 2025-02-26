<!-- 首页 -->
<template>
  <NavBar></NavBar>
  <div class="message-page">
    <div class="module ">
      <span class="title">消息传播姬</span>
      <el-divider />
      <span v-if="JSON.stringify(chatList) == '{}'" class="vice-title">最近收到的消息都在这里，暂时还没有收到消息哦~</span>
      <span v-else class="vice-title">最近收到的消息都在这里哦~</span>
    </div>
    <div class="content ">
      <div v-for="(item, index) in chatList" :key="index" class="chat-day">
        <span class="day">{{ index }}</span>
        <!-- 每一条消息 -->
        <div v-for="(chat, index) in item" :key="index" class="chat-box">
          <!-- 头像 -->
          <div class="avatar">
            <a :href="`/space/${chat.user.uid}/home`" target="_blank"  class="avatar-container">
              <img :src="chat.user.avatar_url" alt="" class="avatar-img" />
            </a>
          </div>
          <!-- 头像右侧：UP名字·发布时间，内容 -->
          <div class="chat-content">
            <div class="chat-info" @click="openNewPage(`/space/${chat.user.uid}/home`)">
              {{ chat.user.nickname }} · {{ handleTime(chat.time) }}
            </div>
            <div class="chat-text" v-html="chat.content"></div>
          </div>
        </div>
      </div>
    </div>
    <span class="dixian">-- 你已试探到我的底线 --</span>
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue';
import { get,  } from '@/network/request'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const tempList = ref([])
// 最终的按时间降序的消息列表
const chatList = ref([])

// 获取最近聊天列表，不过有用的只有对面给自己发的东西
const getChatList = async() => {
  const res = await get("/msg/chat/recent-list", {
    params: { offset: 0 },
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token"), },
  })
  const token = localStorage.getItem("teri_token");
  const authorizationHeader = `Bearer ${token}`;
  console.log('asdfasdf', authorizationHeader);
  tempList.value = res.data.data.list
  console.log('chat', tempList.value)
  let allmsg = tempList.value.flatMap(item=>
    item.detail.list.map(detail => ({
      content: detail.content,
      user: item.user,
      time: new Date(detail.time),
    }))
  )
  chatList.value = allmsg.sort((a, b) => new Date(b.time) - new Date(a.time))
}

// 按日期分组
const groupByDate = () => {
  let temp = chatList.value.reduce((acc, chat) => {
    const date = new Date(chat.time).toISOString().split('T')[0]
    if(!acc[date]) acc[date] = []
    acc[date].push(chat)
    return acc
  }, {})
  chatList.value = temp
  console.log('chatList', chatList.value)
}

const handleTime = (time) => {
  const inputDate = new Date(time);
  // 如果转换后的日期无效，返回 '未知时间'
  if (isNaN(inputDate.getTime())) {
    return '未知时间';
  }
  const hours = String(inputDate.getHours()).padStart(2, '0');
  const minutes = String(inputDate.getMinutes()).padStart(2, '0');
  return `${hours}:${minutes}`;
}

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_blank');
};

onMounted(async() => {
  await getChatList()
  groupByDate()
})
</script>

<style scoped>
.message-page {
  margin-top: 95px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: relative;
}

.module {
  /* 限制宽度，更美观 */
  display: flex;
  justify-content: center;
  flex-direction: column;
  position: relative;
  width: 1000px;
  border-radius: 20px;
  border: 1px solid #eeeeee;
  background-color: white;
}

.title {
  margin: 20px 20px 0;
  text-align: left;
  color: black;
  font-size: 28px;
  font-weight: bold;
}

.vice-title {
  margin: 0px 30px 20px;
  text-align: left;
  color: black;
  font-size: 18px;
}

.content {
  margin-top: 10px;
  display: flex;
  justify-content: start;
  align-items: center;
  flex-direction: column;
  position: relative;
}

.chat-day {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  position: relative;
  width: 1000px;
  border-radius: 20px;
  border: 1px solid #fafafa;
  background-color: rgb(249, 249, 249);
  text-align: left;
  box-shadow: var(--el-box-shadow-light);
  padding-bottom: 40px;
}

.day {
  font-size: 16px;
  font-weight: bold;
  margin: 25px 35px 0;
  color: black;
}

.chat-box {
  margin: 30px 45px 0;
  font-size: 17px;
  /* background-color: #9e4a4a; */
  display: flex;
  justify-content: start;
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

.chat-content {
  display: flex;
  justify-content: flex-start;
  flex-direction: column;
}

.chat-info {
  display: flex;
  align-self: flex-start;
  cursor: pointer;
  margin-left: 10px;
  color: #686868;
  font-size: 14px;
}

.chat-text {
  align-self: flex-start;
  margin-top: 10px;
  margin-left: 20px;
  border: 1px solid #999;
  border-top-right-radius: 10px;
  border-bottom-left-radius: 10px;
  border-bottom-right-radius: 10px;
  padding: 10px;
  background-color: #fff;
  color: black;
  text-align: left;
  box-shadow: var(--el-box-shadow-lighter);
  font-size: 16px;
  min-width: 10px;
  max-width: 750px;
}

.dixian {
  font-size: 13px;
  margin-top: 40px;
  color: #999;
}
</style>