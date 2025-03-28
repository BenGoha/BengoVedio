import { createPinia } from "pinia";
import videojs from "video.js";
import "video.js/dist/video-js.css";
import { createApp } from 'vue';
import App from './App.vue';
import vuetify from './plugins/vuetify';
import router from './router';
import { useUserStore } from './stores'; // 导入 useUserStore
// 创建应用
const bengoApp = createApp(App)
bengoApp.use(createPinia())
bengoApp.use(router)
bengoApp.use(vuetify)
bengoApp.config.globalProperties.$video = videojs;
// bengoApp.mount('#app')

// 恢复用户状态
const userStore = useUserStore();
userStore.restoreUserInfo().then(() => {
    bengoApp.mount('#app');
}).catch(err => {
    console.error("存储状态失败:", err);
    bengoApp.mount('#app'); // 即使恢复失败也挂载应用
});