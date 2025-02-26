import { createRouter, createWebHistory } from 'vue-router'

// 新增什么页面，在这里注册即可
const Index = () => import('@/views/IndexPage.vue')
const Search = () => import('@/views/SearchPage.vue')
const SpacePage = () => import('@/views/Space/SpacePage.vue')
const SpaceDynamic = () => import('@/views/Space/children/SpaceDynamic.vue')
const SpaceHistory = () => import('@/views/Space/children/SpaceHistory.vue')
const SpaceFavlist = () => import('@/views/Space/children/SpaceFavlist.vue')
const SpaceHome = () => import('@/views/Space/children/SpaceHome.vue')
const SpaceSetting = () => import('@/views/Space/children/SpaceSetting.vue')
const SpaceUpload = () => import('@/views/Space/children/SpaceUpload.vue')
const MessagePage = () => import('@/views/MessagePage.vue')
const UploadPage = () => import('@/views/Upload/UploadPage.vue')
const WeeklyData = () => import('@/views/Upload/children/WeeklyData.vue')
const VideoPage = () => import('@/views/VideoPage.vue')
const ArticlePage = () => import('@/views/ArticlePage.vue')
const UploadVideo = () => import('@/views/Upload/children/VideoUploadNew.vue')
const UploadArticle = () => import('@/views/Upload/children/TextUpload.vue')

const routes = [
  // 新增页面路由在这里注册
  { path: '/', redirect: '' },
  { path: '', name: 'Index', component: Index, meta: { requestAuth: false } },
  // requestAuth: false 表示不需要登录即可访问该页面，如果需要登录才能访问，则设置为 true
  { // 搜索
    path: '/search', 
    name: 'Search', 
    component: Search, 
    meta: { requestAuth: false }, 
    props: (route) => ({ keyword: route.query.keyword})
  },
  { // 个人空间
    path: '/space',
    component: SpacePage,
    meta: { requestAuth: false },
    children: [
      { path: '/space/:uid/home', component: SpaceHome, meta: { requestAuth: false } },
      { path: '/space/:uid/dynamic', component: SpaceDynamic, meta: { requestAuth: false } },
      { path: '/space/:uid/favlist', component: SpaceFavlist, meta: { requestAuth: false }, props: route => ({ fid: route.query.fid })  },
      { path: '/space/:uid/history', component: SpaceHistory, meta: { requestAuth: true } },
      { path: '/space/:uid/setting', component: SpaceSetting, meta: { requestAuth: true } },
      { path: '/space/:uid/upload', component: SpaceUpload, meta: { requestAuth: false } },
      { path: '/space/:uid/upload/video', component: SpaceUpload, meta: { requestAuth: false } },
      { path: '/space/:uid/upload/article', component: SpaceUpload, meta: { requestAuth: false } },
    ]
  },
  { // 消息
    path: '/message',
    component: MessagePage,
    meta: { requestAuth: true }
  },
  { // 投稿
    path: '/upload/video',
    component: UploadPage,
    meta: { requestAuth: true },
    children: [
      { path: '/upload/video', component: UploadVideo, meta: { requestAuth: true } },
      { path: '/upload/article', component: UploadArticle, meta: { requestAuth: true } },
      { path: '/upload/data', component: WeeklyData, meta: { requestAuth: true } },
    ]
  },
  { // 视频
    path: '/video/:vid', 
    component: VideoPage, 
    meta: { requestAuth: true }
  },
  { // 专栏
    path: '/article/:aid', 
    component: ArticlePage, 
    meta: { requestAuth: true }
  },
]

const router = createRouter({
    history: createWebHistory(),
    routes
});

// 缓存没有token，跳回首页
router.beforeEach((to, from, next) => {
    if (to.meta.requestAuth && !localStorage.getItem("teri_token")) {
        next({ name: "Index" });
    } else {
        next();
    }
});

export default router