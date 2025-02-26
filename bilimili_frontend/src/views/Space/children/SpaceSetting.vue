<!-- 个人主页-信息设置 -->
<template>
  <div class="info-container">
    <!-- 改个人信息 -->
    <div class="title">我的信息</div>
    <div class="info-content">
      <div class="info-item">
        <span class="info-label">昵称：</span>
        <el-input class="input-box" v-model="nickname" placeholder="请输入昵称" maxlength="20" show-word-limit/>
      </div>
      <div class="info-item">
        <span class="info-label">用户ID：</span>
        <span class="uid">{{ user.uid }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">性别：</span>
        <div class="gender-radio">
        <el-radio-group v-model="gender" >
          <el-radio :value="1">男</el-radio>
          <el-radio :value="0">女</el-radio>
          <el-radio :value="2">保密</el-radio>
        </el-radio-group>
        </div>
      </div>
      <div class="info-item">
        <span class="info-label">个性签名：</span>
        <el-input class="input-box2" type="textarea" clearable :autosize="{ minRows: 3, maxRows: 5 }" v-model="description" placeholder="设置一下自己的签名吧~"/>
      </div>
      <div class="confirm">
        <div class="confirm-botton" :class="{'disable': neverChanged }" @click="confirm">修改</div>
      </div>
    </div>
    <!-- 改头像 -->
    <div class="title">我的头像</div>
    <div class="info-content" >
      <div class="avatar-container">
        <div style="display: flex; flex-direction: column;">
          <div class="avatar-box">
            <img :src="user.avatar_url" alt="" class="avatar-img" />
          </div>
          <span class="avatar-text">当前头像</span>
        </div>
        <el-divider direction="vertical" style="border-width: 2px; height: 250px;"/>
        
        <div class="upload-box" style="display: flex; flex-direction: column;">
          <el-upload
            action="#"
            :auto-upload="false"
            :multiple="false"
            :show-file-list="false"
            :on-change="uploadFile"
            drag
            accept="image/jpg,image/jpeg,image/png">
            <div class="avatar-box">
              <img v-if="newAvatarURL" :src="newAvatarURL" alt="" class="avatar-img" />
              <div v-else>
                <el-icon :size="60" style="color: #999"><UploadFilled /></el-icon>
                <div class="el-upload__text">
                  <em>点击上传</em>或拖拽至此处
                </div>
              </div>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                jpg/jpeg/png格式，大小5MB以内
              </div>
            </template>
          </el-upload>
          <div class="confirm">
            <div class="confirm-botton" :class="{'disable': !newAvatarURL }" @click="confirmAvatar">更换头像</div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
</template>

<script setup>
import { ref, computed, onMounted, watch, defineEmits } from 'vue'
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus';
import { post } from '@/network/request'

const store = useStore()
const emit = defineEmits(['userVistedChange'])

const nickname = ref("")
const description = ref("")
const gender = ref(2)

const user = computed(() => {return store.state.user})

const neverChanged = computed(() => {
  return nickname.value === user.value.nickname && description.value === user.value.description && gender.value === user.value.gender
})

// 个人信息修改
const confirm = async() => {
  console.log(user.value.nickname, user.value.description, user.value.gender)
  console.log(nickname.value, description.value, gender.value)
  if (neverChanged.value) {
    ElMessage.warning('信息未修改')
    return
  }
  // nickname.value = nickname.value.trim()
  if (nickname.value === "") {
    ElMessage.warning('昵称不能为空')
    return
  }
  const formData = new FormData()
  formData.append('nickname', nickname.value)
  formData.append('description', description.value)
  formData.append('gender', gender.value)
  const res = await post("/user/info/update", formData, {
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
  })
  if(!res.data) return
  store.state.user.nickname = nickname.value
  store.state.user.description = description.value
  store.state.user.gender = gender.value
  ElMessage.success('信息修改成功')
  emit('userVistedChange')    // 给父组件SpacePage的回调，通知父组件刷新用户信息
}

// 头像上传相关
const newAvatarURL = ref('')
const imgFile = ref(null)

const uploadFile = (item) => {
  imgFile.value = item.raw
  newAvatarURL.value = URL.createObjectURL(item.raw)
  console.log('newAvatarURL', newAvatarURL.value)
  console.log('form', imgFile.value)
}

// 头像修改
const confirmAvatar = async() => {
  if(!newAvatarURL.value) {
    ElMessage.warning('请先上传头像')
    return
  }
  const isLessThan5M = imgFile.value.size / 1024 / 1024 < 5
  if(!isLessThan5M) {
    ElMessage.warning('图片大小不能超过5MB，请重新上传')
    return
  }
  const formData = new FormData()
  formData.append('file', imgFile.value)
  const res = await post("/user/avatar/update", formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      Authorization: "Bearer " + localStorage.getItem("teri_token"),
    }
  })
  console.log('换头像的res',res)
  if (!res.data) return
  store.state.user.avatar_url = res.data.data
  ElMessage.success('头像修改成功')
  emit('userVistedChange')    // 给父组件SpacePage的回调，通知父组件刷新用户信息
  newAvatarURL.value = ''
  // location.reload()
}

onMounted(() => {
  console.log('user', user.value)
  nickname.value = user.value.nickname
  description.value = user.value.description
  gender.value = user.value.gender
})

watch(store.state.user, (curr) => {
  user.value = curr
  },
  { deep: true } // 深度观察
);

// 解决刚挂载的时候输入框里显示不了初始信息的问题
watch(user, (newVal) => {
  nickname.value = newVal.nickname
  description.value = newVal.description
  gender.value = newVal.gender
})
</script>

<style scoped>
.title {
  margin: 15px 30px 20px;
  display: flex;
  align-items: center;
  justify-content: start;
  font-size: 24px;
  font-weight: bold;
  color: black;
  text-align: left;
}

.info-content {
  margin-bottom: 30px;
  padding-bottom: 60px;
  border-bottom: 1px solid #e6e6e6;
  justify-content: center;
}

.info-item {
  display: flex;
  justify-self: start;
  align-items: center;
  margin-top: 30px;
}

.info-label {
  text-align: right;
  width: 200px;
  font-size: 17px;
  margin-right: 15px;
  color: black;
}

.uid {
  font-size: 16px;
  color: #999;
}

.input-box {
  width: 450px;
}

.input-box >>> .el-input__inner {
  padding-left: 5px;
  font-family: 'Harmony Font';
  height: 40px;
  font-size: 16px !important;
}

.input-box >>> .el-input__wrapper {
  border: 1px solid #166fe8;
  border-radius: 8px;
  padding-right: 0;
  padding-top: 0;
  padding-bottom: 0;
}

.input-box >>> .el-input__suffix {
  margin-right: 5px;
}

.input-box2 {
  width: 600px;
}

.input-box2 >>> .el-textarea__inner {
  padding-left: 15px;
  line-height: 30px;
  font-family: 'Harmony Font';
  font-size: 16px !important;
  border: 1px solid #166fe8;
  border-radius: 8px;
}

.gender-radio >>> .el-radio__label{
  font-size: 16px;
}

.confirm {
  margin-top: 40px;
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

.disable {
  background-color: #e4e4e4;
  color: #999;
  border: 1px solid #999;
}

.disable:hover {
  background-color: #e4e4e4;
  color: #999;
  text-decoration: line-through;
}

.avatar-container {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  margin-left: 200px;
  margin-right: 200px;
}

.avatar-box {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  border: 1px solid #e6e6e6;
  box-shadow: var(--el-box-shadow-dark);
  display: flex;
  /* flex-direction: column; */
  overflow: hidden;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-text {
  margin-top: 20px;
  color: black;
  font-size: 20px;
}

.upload-box /deep/ .el-upload {
  --el-upload-dragger-padding-vertical: 40px;
}
</style>