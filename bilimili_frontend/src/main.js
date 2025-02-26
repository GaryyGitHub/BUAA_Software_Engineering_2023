import { createApp } from 'vue'
import App from './App.vue'
import router from './router'   // 即src/router/index.js。需引入的东西太多，故另外创建文件，下同
import store from './store'    // 即src/store/index.js
import axios from 'axios'
import { get, post } from './network/request'
// 引入图片裁剪插件

// 引入全局样式css
import "./assets/css/index.css"

// 各类插件在这里import进来
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as Icons from '@element-plus/icons-vue' // 引入所有图标，并命名为 Icons

// 注册全局组件
const app = createApp(App)

app.config.globalProperties.$axios = axios
app.config.globalProperties.$get = get
app.config.globalProperties.$post = post

for (let i in Icons) {
  app.component(i, Icons[i])
} // 通过遍历的方式注册所有 svg 组件，会牺牲一点点性能
app.use(ElementPlus)
app.use(store)
app.use(router)
app.mount('#app')
