import { createApp } from "vue";
import App from "./App.vue";
import ElementPlus from "element-plus";
import { ElMessage } from "element-plus";
import "element-plus/theme-chalk/index.css";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import axios from "axios";
import { get, post } from "./network/request";
import router from "./router";
import store from "./store";

import VMdPreview from '@kangc/v-md-editor/lib/preview';
import '@kangc/v-md-editor/lib/style/preview.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';
// highlight.js
import hljs from 'highlight.js';
// Prism
import Prism from 'prismjs';
// 代码高亮
import 'prismjs/components/prism-json';
// 显示代码行数
import createLineNumbertPlugin from '@kangc/v-md-editor/lib/plugins/line-number/index';
// 快速复制代码
import createCopyCodePlugin from '@kangc/v-md-editor/lib/plugins/copy-code/index';
import '@kangc/v-md-editor/lib/plugins/copy-code/copy-code.css';
// katex
import createKatexPlugin from '@kangc/v-md-editor/lib/plugins/katex/cdn';

VMdPreview.use(githubTheme, {
  Hljs: hljs,
});
VMdPreview.use(githubTheme, {
  Prism,
});
VMdPreview.use(createLineNumbertPlugin());
VMdPreview.use(createCopyCodePlugin());
VMdPreview.use(createKatexPlugin());

// 全局样式表
import "./assets/css/base.css";

const app = createApp(App);

// 添加全局变量
app.config.globalProperties.$message = ElMessage;
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$get = get;
app.config.globalProperties.$post = post;
// 注册全部element图标
app.use(ElementPlus, { locale: zhCn });
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(store);
app.use(router);
app.use(VMdPreview);
app.mount("#app");
