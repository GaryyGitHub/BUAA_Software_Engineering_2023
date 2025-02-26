<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "App",
  components: {},
  methods: {
    // 获取频道列表
    async getChannels() {
      let res = await this.$get("/category/getall");
      this.$store.commit("updateChannels", res.data.data);
    },
  },
  created() {
    if (localStorage.getItem("bm_token")) {
      this.$store.dispatch("getPersonalInfo");
    }
    this.getChannels();
  },
};
</script>

<style>
#app {
    margin: 0 auto;
    background-color: #ffffff;
}
</style>
