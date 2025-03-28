import { defineStore } from 'pinia';
import { apiGetUserInfo } from '../apis/user/user.js';

export default defineStore('user', {
    state: () => ({
        info: {},
        token: sessionStorage.getItem("token"),
        lookId: null
    }),
    actions: {
        setUserInfo(userData) {
            this.info = userData.user; // 只存储 user 对象
            this.token = userData.token;
            sessionStorage.setItem("token", userData.token);
        },
        logout() {
            this.info = {};
            this.token = null;
            this.lookId = null;
            sessionStorage.removeItem("token");
        },
        async restoreUserInfo() {
            if (this.token && !this.info.id) {
                try {
                    const { data } = await apiGetUserInfo(this.info.id || ""); // 默认空字符串
                    if (data.state) {
                        this.info = data.data;
                    } else {
                        console.error("Restore failed:", data.message);
                        this.logout();
                    }
                } catch (err) {
                    console.error("Failed to restore user info:", err);
                    this.logout();
                }
            }
        }
    }
});