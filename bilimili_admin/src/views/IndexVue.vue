<template>
    <div class="index-vue">
        <header class="header-bar">
            <el-popover
                :width="300"
                popper-style="box-shadow: rgba(6, 67, 199, 0.5) 0px 10px 38px -20px, rgba(6, 67, 199, 0.5) 0px 10px 20px -10px; padding: 8px;"
            >
                <template #reference>
                    <div class="avatar-out">
                        <img :src="user.avatar_url" :alt="`${user.nickname}的头像`">
                    </div>
                </template>
                <template #default>
                    <div
                        class="demo-rich-conent"
                        style="display: flex; gap: 16px; flex-direction: column"
                    >
                        <div class="avatar-in" style="width: 60px; height: 60px;">
                            <img :src="user.avatar_url" :alt="`${user.nickname}的头像`">
                        </div>
                        <div>
                            <p
                                class="demo-rich-content__name"
                                style="margin: 0 20px; font-weight: 500; font-size: 15px; color: #282828"
                            >
                                {{ user.nickname }}
                            </p>
                            <p
                                class="demo-rich-content__mention"
                                style="margin: 0 20px; font-size: 14px; color: var(--el-color-info)"
                            >
                                {{ user.description }}
                            </p>
                        </div>
                        <div class="user-file-popover">
                            <div class="popover-window" @click="logout">
                                <i class="iconfont icon-dengchu"></i>
                                <span>退出登录</span>
                            </div>
                        </div>
                    </div>
                </template>
            </el-popover>
        </header>
        <aside class="left-bar">
            <div class="top-logo-area">
                <a class="logo">
                    <img src="~assets/img/bilimili-pink.png" alt="">
                </a>
            </div>
            <div class="paths-area">
                <el-menu class="menu" router="true">
                    <el-menu-item index="/home">
                        <i class="iconfont icon-shouye1"></i>
                        <span>首页</span>
                    </el-menu-item>
                    <el-sub-menu index="/review">
                        <template #title>
                            <i class="iconfont icon-caidan"></i>
                            <span>审核管理</span>
                        </template>
                        <el-menu-item index="/review/video">
                            <i class="iconfont icon-shipinshenhe"></i>
                            <span>视频审核</span>
                        </el-menu-item>
                        <el-menu-item index="/review/article">
                            <i class="iconfont icon-wenzhang"></i>
                            <span>专栏审核</span>
                        </el-menu-item>
                    </el-sub-menu>
                </el-menu>
            </div>
        </aside>
        <main class="router-area">
            <router-view></router-view>
        </main>
    </div>
</template>

<script>
export default {
    name: "IndexVue",
    data() {
        return {
            path: ["/home",  "/review/video", "/review/article"],   // 用于判断当前活动页
        }
    },
    computed: {
        user() {
            return this.$store.state.user;
        },
    },
    methods: {
        logout() {
            this.$store.dispatch("logout");
        },
    },
    created() {
        this.active = this.path.find(p => this.$route.path.startsWith(p)) || this.active;
    },
}
</script>

<style scoped>
.index-vue {
    display: flex;
    flex-direction: column;
    flex: 1 1 auto;
    position: relative;
}

.header-bar {
    z-index: 1005;
    height: 70px;
    width: 100%;
    background-color: #fff;
    box-shadow: 0 2px 30px -1px #55555514;
    display: flex;
    align-items: center;
    position: fixed;
}

.avatar-out {
    margin: 0 20px 0 1600px;
    height: 40px;
    width: 40px;
    border-radius: 50%;
    cursor: pointer;
}

.avatar-out img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
}

.avatar-in {
    margin: 10px 0 0 20px;
    height: 40px;
    width: 40px;
    border-radius: 50%;
    cursor: pointer;
}

.avatar-in img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
}

.user-avatar img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
}

.popover-window {
    height: 38px;
    cursor: pointer;
    color: #61666D;
    border-radius: 10px;
    padding: 0 20px;
    display: flex;
    align-items: center;
}

.popover-window:hover {
    color:  #18191C;
    background-color: #f1f3f5;
}

.popover-window .iconfont {
    color: #18191C;
    margin-right: 12px;
}

.left-bar {
    position: fixed;
    z-index: 1008;
    height: 100%;
    background-color: #fff;
    box-shadow: 0 4px 10px 0 rgba(70, 70, 70, 0.08);
    width: 240px;
    display: flex;
    flex-direction: column;
}

.top-logo-area {
    height: 100px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 18px 10px -8px rgba(6, 67, 199, 0.2);
    z-index: 2000;
    flex: 1 0 auto;
}

.logo img {
    width: 200px;
    cursor: pointer;
}

.paths-area {
    flex: 0 1 auto;
    height: 100%;
    overflow-x: hidden;
    overflow-y: auto;
    --el-menu-active-color: #008AC5;
    --el-menu-level-padding: 40px;
    padding: 16px 8px;
}

.el-menu-item .iconfont, .el-sub-menu .iconfont {
    font-size: 20px;
    margin-right: 20px;
}

.el-menu-item:not(.is-active) .iconfont {
    color: #61666D;
}


.router-area {
    height: 100%;
    padding-left: 256px;
    display: flex;
    flex-direction: column;
    flex: 1 0 auto;
    max-width: 100%;
    padding-top: 70px;
    background-color: #fafafa;
}

.el-menu {
    border-right: unset;
}

.paths-area /deep/ .el-sub-menu__title, .el-menu-item {
    height: 50px;
    border-radius: 8px;
    margin-bottom: 8px;
}

.el-menu-item.is-active {
    background-color: #F1F2F3;
}

.paths-area /deep/ .el-sub-menu__title:hover, .el-menu-item:hover {
    background-color: #F6F7F8;
}
</style>