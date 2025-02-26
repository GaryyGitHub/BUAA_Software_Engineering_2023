<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
    // data() {
    //     return {
    //         // 加载蒙版的显隐过度
    //         markDisplay: "none",
    //         isMarkShow: false,
    //     }
    // },
    methods: {
        // 获取频道列表
        async getChannels() {
            let res = await this.$get("/category/getall");
            // console.log("频道列表: ", res);
            this.$store.commit("updateChannels", res.data.data);
        },

        // async getHotSearch() {
        //     const res = await this.$get("/search/hot/get");
        //     this.$store.commit("updateTrendings", res.data.data);
        // },

        // 加载蒙版的延迟渲染效果
        // show() {
        //     this.markDisplay = "";
        //     this.isMarkShow = true;
        // },
        // hide() {
        //     this.isMarkShow = false;
        //     setTimeout(() => {
        //         this.markDisplay = "none";
        //     }, 200);
        // },

        // 开启实时通信消息服务，定义在store中
        async initIMServer() {
            await this.$store.dispatch("connectWebSocket");
            const connection = JSON.stringify({
                code: 100,
                content: "Bearer " + localStorage.getItem('teri_token'),
            });
            this.$store.state.ws.send(connection);
        },

        // 关闭websocket
        async closeIMWebSocket() {
            await this.$store.dispatch("closeWebSocket");
        },

        // 获取当前用户的收藏夹列表
        // async getFavorites() {
        //     const res = await this.$get("/favorite/get-all/user", {
        //         params: { uid: this.$store.state.user.uid },
        //         headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
        //     });
        //     if (!res.data) return;
        //     // 将默认置顶
        //     const defaultFav = res.data.data.find(item => item.type === 1);
        //     const list = res.data.data.filter(item => item.type !== 1);
        //     list.unshift(defaultFav);
        //     this.$store.commit("updateFavorites", list);
        // },

        // 获取用户赞踩的评论集合
        // async getLikeAndDisLikeComment() {
        //     const res = await this.$get("/comment/get-like-and-dislike", {
        //         params: { uid: this.$store.state.user.uid },
        //         headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
        //     });
        //     if (!res.data) return;
        //     this.$store.commit("updateLikeComment", res.data.data.userLike);
        //     this.$store.commit("updateDislikeComment", res.data.data.userDislike);
        // }
    },
    async created() {
        // 如果缓存中有token，尝试获取用户数据，并建立全双工通信
        if (localStorage.getItem("teri_token")) {
            await this.$store.dispatch("getPersonalInfo");
        }
        // 有可能上面获取信息时token过期就清掉了 所以这里再做个存在判断
        // if (localStorage.getItem("teri_token")) {
            // this.$store.dispatch("getMsgUnread");
            // await this.initIMServer();
            // await this.getFavorites();
            // await this.getLikeAndDisLikeComment();
        // }
        this.getChannels();
        // this.getHotSearch();
    },
    mounted() {
        window.addEventListener('beforeunload', this.closeIMWebSocket);    // beforeunload 事件监听标签页关闭
    },
    async beforeUnmount() {
        await this.closeIMWebSocket();
        window.removeEventListener('beforeunload', this.closeIMWebSocket);
    },
    // watch: {
    //     "$store.state.isLoading"(current) {
    //         if (current) {
    //             this.show();
    //         } else {
    //             this.hide();
    //         }
    //     }
    // }
}
</script>

<style>
#app {
  /* 字体要在这里设定，而非main.js */
  font-family: 'Harmony Font';
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
