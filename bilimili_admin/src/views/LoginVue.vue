<template>
    <div class="login">
        <div class="background-pic">
            <img src="~assets/img/login-bg.jpg">
        </div>
        <div class="login-wrap">
            <div class="header-text">Administrator</div>
            <div class="input-area">
                <div class="input-data account">
                    <i class="iconfont icon-zhanghao"></i>
                    <input type="text" v-model="username" >
                </div>
                <div class="input-data password">
                    <i class="iconfont icon-mima"></i>
                    <input type="password" v-model="password" >
                </div>
                <div class="btn-wrap">
                    <el-button class="login-btn" @click="login" color="#2136AC" plain>登录</el-button>
                </div>
            </div>
        </div>        
    </div>
</template>

<script>
import { ElMessage } from 'element-plus';
import axios from 'axios';

export default {
    name: "LoginVue",
    data() {
        return {
            username: "",
            password: "",
        }
    },
    methods: {
        async login() {
            if (!this.username) {
                ElMessage.error("请输入账号");
                return;
            }
            if (!this.password) {
                ElMessage.error("请输入密码");
                return;
            }

            this.$store.state.isLoading = true;

            try {
                const result = await axios.post("/api/admin/account/login", {
                    username: this.username,
                    password: this.password,
                });

                if (result.data.code === 200) {
                    localStorage.setItem("bm_token", result.data.data.token);
                    this.$store.commit("updateUser", result.data.data.user);
                    ElMessage.success(result.data.message);
                    this.$store.commit("updateIsLogin", true);
                    this.$router.push("/");
                } else {
                    ElMessage.error(result.data.message);
                }
            } catch {
                ElMessage.error("用户名或密码错误");
            } finally {
                this.$store.state.isLoading = false;
            }
        }

    },
}
</script>

<style scoped>
.background-pic {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.background-pic img {
    width: 100%;
    height: 100%;
}

.login-wrap {
    position: absolute;
    width: 450px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background-color: rgba(255, 255, 255, 0.3);
    border-radius: 8px;
    backdrop-filter: blur(15px);
    box-shadow: 0px 0px 30px rgba(0, 0, 0, 0.3);
}

.header-text {
    position: absolute;
    top: -40px;
    left: 50%;
    transform: translateX(-50%);
    font-weight: 600;
    font-size: 52px;
    color: #fff;
    text-shadow: 0 10px 15px rgba(0, 0, 0, 0.2);
}

.input-area {
    width: 100%;
    padding: 10px 40px 40px 40px;
}

.input-data {
    position: relative;
    width: 100%;
    height: 50px;
    border-bottom: 2px solid #2136AC;
    margin: 30px 0;
}

.input-data input {
    width: 100%;
    height: 100%;
    background: transparent;
    border: none;
    outline: none;
    font-size: 16px;
    letter-spacing: 1px;
    color: #162938;
    font-weight: 400;
    padding: 0 35px 0 5px;
}

.input-data .iconfont {
    position: absolute;
    right: 0;
    padding-right: 8px;
    color: #2136AC;
    line-height: 48px;
}

.btn-wrap {
    top: 220px;
    position: absolute;
    width: 55%;
    align-items: center;
    left: 50%;
    transform: translateX(-50%);
}

.login-btn {
    width: 100%;
    height: 45px;
    border-radius: 6px;
    font-size: 18px;
}
</style>