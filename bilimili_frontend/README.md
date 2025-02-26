# 2024_summer_porject_frontend

BiliMili视频网站 13小组 前端部分

## 工作进度
### 7.23 高铭
1. 主文件夹下，只有vue.config.js在后期需要改
2. public文件夹下，index.html和favicon.ico不用再改，是浏览器标签页的显示内容
3. src下，我的改动：
    - 新增router/index.js，以后的新页面需要在这里注册。安装了vue-router（ **理论上不需要安装，原因是vue的版本是3.2.13太低，现在最新已经3.4.33** ）。
    > 以后在设计登录的时候，localStorage.setItem也需要token，我在router.beforeEach设成了`bili_token`（我们上学期甚至没有改这个，直接用的teri_token）。localStorage就是浏览器缓存，形式是键值对。到时候设计登录的时候，键是`bili_token`，值就是用户信息
    - 新增viewer，加了一个IndexPage.vue，主页就是这个。
    - 改动App.vue，不再显示默认页，上学期这里的代码是显示loading的gif图，有点复杂。后期需要在此添加从后端获取各类用户信息的代码
    - 改动main.js，把router的改动体现出来

### 7.25 高铭
1. 设置了全局样式，详见src/assets/css。
2. 添加了Harmony Font使用的字体至src/assesrs/fonts，并全局应用在main.js和App.vue中。
3. 设计了NavBar组件，对应以前的HeaderBar插件，插入了logo，搜索框，以及各类导航按钮。
4. 配置了Element Plus插件，以后可以使用这里面的插件（功能很多，用途很广，比如搜索框）。

### 7.26 高铭
1. 完成了NavBar组件的开发，包括logo、搜索框、各类导航按钮、投稿按钮和头像，完善了UI设计，设计了一些hover和focus的动画效果。
2. 配置了Element Icon插件，以后可以使用这里面的图标。
3. 增添了store/index.js，用来定义、更新全局变量。
> 组合式API中，可以通过调用 `useStore` 函数，来在 `setup` 钩子函数中访问 `store`。这与在组件中使用选项式 API 访问 `this.$store` 是等效的。
```javascript
import { useStore } from 'vuex'
export default {
  setup () {
    const store = useStore()
  }
}
```

### 7.27 高铭
1. 配置了ElMessage插件，以后可以在页面上显示一些提示信息。
2. 把先前的选项式API改写成了**组合式API**，script部分的代码确实简洁了一些，也更加先进。需要注意的是组合式API下不使用this，例如`this.$store.state.isLogin`现在需注册`const store = useStore()`，然后写成`store.state.isLogin`。
**建议前端的各位以后也用组合式API来写代码，这样代码更加简洁，也更加符合Vue的理念（更好降重）。**
3. 初步开发了登录注册的弹窗，模仿并对先前的UI设计有所改变。除前后端通信外，UI、功能、逻辑已经基本完善。前端其他人可以帮我查一下。waiting for backend.
*One more thing*, 我在NavBar.vue改变了el-dialog的风格，这里对组件风格的改动去掉了scoped，可能会对以后使用el-dialog产生影响，要注意！！