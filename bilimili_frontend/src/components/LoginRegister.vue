<!-- 登录注册弹窗组件，需要外套el-dialog -->
<template>
<div class="a">
  <div class="welcome">
    <div class="img-box">
        <img src="../assets/img/logo-pink.png"/>
        <span class="welcome-text">你喜欢的视频</span>
        <span class="welcome-text">都在这里</span>
        <span class="welcome-text">加入我们——→</span>
    </div>
  </div>
  <div class="login-register" >
    <el-tabs v-model="activity" @tab-click="handleClick" stretch class="container">
      <el-tab-pane label="登&nbsp;录" name="login" lazy>
        <div class="login-boxes">
          <el-input class="box" v-model="usernameLogin"  placeholder="请输入账号">
            <template #prefix><el-icon :size="22"><User /></el-icon></template>
          </el-input>
          <el-input class="box" v-model="passwordLogin" type="password" show-password placeholder="请输入密码">
            <template #prefix><el-icon :size="22"><Lock /></el-icon></template>
          </el-input>
          <el-button class="commit" type="primary" @click="handleLogin" size="large" plain>登&nbsp;录</el-button>
        </div>
      </el-tab-pane>
      <el-tab-pane label="注&nbsp;册" name="register" lazy>
        <div class="register-boxes">
          <el-input class="box" v-model="usernameRegister" placeholder="请设置账号">
            <template #prefix><el-icon :size="22"><User /></el-icon></template>
          </el-input>
          <el-input class="box" v-model="passwordRegister" type="password" show-password placeholder="请设置密码，6-14位，数字和字母组成">
            <template #prefix><el-icon :size="22"><Lock /></el-icon></template>
          </el-input>
          <el-input class="box" v-model="confirmPassword" type="password" show-password placeholder="请确认密码">
            <template #prefix><el-icon :size="22"><Key /></el-icon></template>
          </el-input>
        </div>
        <el-button class="commit" type="primary" @click="handleRegister" size="large" plain>注&nbsp;册</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
</div>
</template>

<script setup>
import axios from 'axios';
import { ElMessage } from 'element-plus'
import { ref, defineEmits } from 'vue'
import { useStore } from 'vuex'

const usernameLogin = ref('')      // 登录账号
const passwordLogin = ref('')      // 登录密码
const usernameRegister = ref('')   // 注册账号
const passwordRegister = ref('')   // 注册密码
const confirmPassword = ref('')    // 注册确认密码
const activity = ref('login')      // 默认显示登录页面，鼠标点到注册按钮时，activity变为register

// 特别注意，这是组合式API的特性，emit需要提前注册，不像选项式API可以直接使用this.$emit
const emit = defineEmits(['success'])
const store = useStore()

const handleClick = (tab) => {
  activity.value = tab.props.name
//   console.log(activity.value)
}

const handleLogin = async() => {
//   console.log(usernameLogin.value, passwordLogin.value)
  // 前端进行登录逻辑判断
  if(usernameLogin.value.trim() === '') {
    ElMessage.error('还没输入账号哦~')
    passwordLogin.value = ""
    return
  }
  if(passwordLogin.value === '') {
    ElMessage.error('还没输入密码哦~')
    return
  }
  // TODO: 前后端通信
  try {
    const res = await axios.post("/api/user/account/login", {
      username: usernameLogin.value.toString(),
      password: passwordLogin.value.toString(),
    });
    console.log('postLogin', res)
    // 登录失败，提示错误信息
    if(res.data.code !== 200) {
      ElMessage.error(res.data.message)
      return
    }
    // 登陆成功，首先更新浏览器缓存token
    localStorage.setItem("teri_token", res.data.data.token)
    store.commit("updateUser", res.data.data.user)
    store.commit("updateIsLogin", true)

    ////////// 登录成功后，有许多初始化工作：开启websocket，获取各类点赞、动态等信息 ///////////
    emit('success');    // 触发登录成功事件，父组件关闭弹窗
    ElMessage.success(res.data.message)
  } catch (error) {
    ElMessage.error('登录失败，请检查账号或密码')
    return
  }
}

const handleRegister = async() => {
  // 前端进行注册逻辑判断
  if (usernameRegister.value.trim() === "") {
    ElMessage.error("账号不能为空哦~")
    passwordRegister.value = ""
    confirmPassword.value = ""
    return
  }
  if (passwordRegister.value === "" || confirmPassword.value === "") {
    ElMessage.error("密码不能为空哦~")
    passwordRegister.value = ""
    confirmPassword.value = ""
    return
  }
  if (passwordRegister.value !== confirmPassword.value) {
    ElMessage.error("两次输入的密码不一致~")
    passwordRegister.value = ""
    confirmPassword.value = ""
    return
  }

  // TODO: 前后端通信
    const res = await axios.post("/api/user/account/register", {
      username: usernameRegister.value.toString(),
      password: passwordRegister.value.toString(),
      confirmedPassword: confirmPassword.value.toString(),
    });
    console.log('postRegister', res)
    // 注册失败，提示错误信息
//////////// 不知为何，后端传的是500ERROR ///////////////
    if(res.data.code !== 200) {
      ElMessage.error(res.data.message)
      return
    }
    // 登陆成功，首先更新浏览器缓存token
    // localStorage.setItem("teri_token", res.data.data.token)
    // store.commit("updateUser", res.data.data.user)
    // store.commit("updateIsLogin", true)

////////// 登录成功后，有许多初始化工作：开启websocket，获取各类点赞、动态等信息，先不做 ///////////
    emit('success');    // 触发登录成功事件，父组件关闭弹窗
    ElMessage.success('注册成功！请移步登录界面')
}
</script>


<style scoped>
.a {
  position: relative;
  display: flex;
  width: 100%;
  height: 100%;
}

.welcome {
  position: relative;
  width: 360px;
  height: 360px;
}

.welcome-text {
  margin-top:20px;
  text-align: left;
  font-size: 30px;
  font-weight: bold;
  text-shadow: 2px 2px 2px rgba(217, 133, 147, 0.6);
}

.img-box {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 360px;
  height: 360px;
  visibility: visible;
  align-items: center;
  padding-top: 30px;
}

.img-box img{
  width: 60%;
  height: auto;
  margin-bottom: 20px;
}

.login-register {
  width: 350px;
  height: 350px;
  margin: 40px;
}

.container {
  width: 90%;
  margin: 0 auto;
}

.login-boxes {
  margin-top: 30px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.register-boxes {
  margin-top: 15px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.box {
  margin-bottom: 30px;
  width: 100%;
}

.box >>> .el-input__inner {
  padding-left: 5px;
  font-family: 'Harmony Font';
  height: 40px;
  font-size: 15px !important;
}

.box >>> .el-input__wrapper {
  border-radius: 15px;
}

.commit{
  margin-top: 10px;
  width: 100px;
  font-family: 'Harmony Font';
  font-size: 16px;
}

.login-register /deep/ .el-tabs__item {
  font-size: 17px;
}
</style>