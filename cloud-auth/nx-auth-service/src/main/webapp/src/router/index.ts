import { createRouter, createWebHistory } from "vue-router";
import home from "@/views/main/HomeView.vue";
import {getAllUrlParam} from "@/utils";

const base_url = import.meta.env.BASE_URL;
const router = createRouter({
  history: createWebHistory(base_url),
  routes: [
    {
      path: "/",
      redirect: "/login",
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/auth/LoginView.vue"),
    },
    {
      path: "/register",
      name: "register",
      component: () => import("../views/auth/RegisterView.vue"),
    },
    {
      path: "/loginSuccess",
      name: "loginSuccess",
      component: () => import("../views/auth/LoginSuccess.vue"),
    },
    {
      path: "/registerSuccess",
      name: "registerSuccess",
      component: () => import("../views/auth/RegisterSuccess.vue"),
    },
    {
      path: "/study",
      name: "slide-verify",
      component: () => import("../views/study/Study.vue"),
    },
    {
      path: "/home",
      name: "home",
      component: home,
    },
    {
      path: "/about",
      name: "about",
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import("../views/AboutView.vue"),
    },
  ],
});



export default router;
