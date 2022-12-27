import Vue from "vue";
import App from "./App.vue";
import VueRouter from "vue-router";
import routes from "./routes";
import store from "./vuex/store";
import http from "@/util/http-common";
import { BootstrapVue } from "bootstrap-vue";

// Import Bootstrap an BootstrapVue CSS files (order is important)
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";

import io from "socket.io-client";
const socketio = io("http://localhost:3001");
Vue.prototype.$socketio = socketio;

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue);

Vue.config.productionTip = false;

Vue.use(VueRouter);

export const router = new VueRouter({
  mode: "history",
  routes,
});

// 뷰 네이게이션 가드
// 페이지를 이동할 때마다 헤더를 재설정해준다.
// router.beforeEach(async (to, from, next) => {
//   const accessToken = store.getters.accessToken;
//   http.defaults.headers.common['Authorization'] = `Bearer ${store.state.accessToken}`;
//   next();
// });

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount("#app");
