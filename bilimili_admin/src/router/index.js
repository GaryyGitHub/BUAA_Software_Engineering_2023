import { createRouter, createWebHistory } from "vue-router";
const Index = () => import("@/views/IndexVue.vue");
const Login = () => import("@/views/LoginVue.vue");
const Home = () => import("@/views/homePage/HomePage.vue");
const Video = () => import("@/views/review/VideoReviewNew.vue");
const VideoDetail = () => import("@/views/review/detail/VideoDetailNew.vue");
const Article = () => import("@/views/review/ArticleReviewNew.vue");
const ArticleDetail = () => import("@/views/review/detail/ArticleDetailNew.vue");

const routes = [
  { path: "/", redirect: "" },
  {
    path: "",
    redirect: "/home",
    component: Index,
    meta: { requestAuth: true },
    children: [
      { path: '/home', component: Home, meta: { requestAuth: true } },
      {
        path: '/review',
        redirect: '/review/video',
        children: [
          {
            path: '/review/video',
            redirect: '/review/video/form',
            children: [
              { path: '/review/video/form', component: Video, meta: { requestAuth: true } },
              { path: '/review/video/detail/:vid', name: 'videoDetail', component: VideoDetail, meta: { requestAuth: true } },
            ]
          },
          { path: '/review/article',
            redirect: '/review/article/form',
            children: [
              { path: '/review/article/form', component: Article, meta: { requestAuth: true} },
              { path: '/review/article/detail/:aid', name: 'articleDetail', component: ArticleDetail, meta: {requestAuth: true} },
            ]
          },
        ]
      },
    ]
  },
  {
    path: "/login",
    name: "login",
    component: Login,
    meta: { requestAuth: false },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 本地没有token就跳到登录界面
router.beforeEach((to, from, next) => {
  if (to.meta.requestAuth && !localStorage.getItem("bm_token")) {
    next({ name: "login" });
  } else {
    next();
  }
});

export default router;
