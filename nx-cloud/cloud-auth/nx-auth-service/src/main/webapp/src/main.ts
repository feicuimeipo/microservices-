import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPersist from 'pinia-plugin-persist'

import App from "./App.vue";
import router from "./router";

import ElementPlus from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";

import "@/styles/index.scss";
import "uno.css";
import * as ElIcons from "@element-plus/icons-vue"

// If you want to use ElMessage, import it.
import "element-plus/theme-chalk/src/message.scss";

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPersist);

//全局引入图标
for (const name in ElIcons) {
    app.component(name,(ElIcons as any)[name]);
}

app.use(pinia);
app.use(ElementPlus, { size: "large", locale: zhCn });
app.use(router);
app.mount("#app");

