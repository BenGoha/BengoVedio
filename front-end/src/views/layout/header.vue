
<!--<template>-->
<!--    <v-app-bar floating name="app-bar" :elevation="0" color="#252632">-->
<!--        <v-app-bar-nav-icon icon="mdi-menu" @click="clickEvent(1, 1)"></v-app-bar-nav-icon>-->
<!--        <v-app-bar-title>BenGo视频平台</v-app-bar-title>-->

<!--        <div class="search-wrapper d-flex align-center">-->
<!--            <v-text-field class="e700-hide" hide-details :density="'comfortable'" prepend-inner-icon="mdi-magnify"-->
<!--                          single-line clearable @click="openSearch()" placeholder="搜索视频">-->
<!--            </v-text-field>-->
<!--            &lt;!&ndash; AI按钮 &ndash;&gt;-->
<!--            <v-btn color="primary" @click="openChatbot" icon class="ml-4" aria-label="Open Chatbot">-->
<!--                <v-icon>mdi-robot</v-icon>-->
<!--            </v-btn>-->
<!--        </div>-->

<!--        <v-toolbar-items variant="plain" class="pr-2">-->
<!--            <v-btn class="d700-hide" @click="openSearch()" :variant="'text'">-->
<!--                <v-icon>mdi-magnify</v-icon>-->
<!--                搜索-->
<!--            </v-btn>-->
<!--            <auth />-->
<!--        </v-toolbar-items>-->

<!--        &lt;!&ndash; 搜索对话框 &ndash;&gt;-->
<!--        <v-dialog v-model="showSearch" max-width="500px" :location="'top'">-->
<!--            <v-card>-->
<!--                <v-toolbar flat color="background">-->
<!--                    <v-text-field autofocus ref="searchInputRef" hide-details prepend-inner-icon="mdi-magnify"-->
<!--                                  single-line clearable v-model="searchKey" @click:clear="search()"-->
<!--                                  v-on:keyup.enter="search" placeholder="搜索视频">-->
<!--                    </v-text-field>-->
<!--                </v-toolbar>-->
<!--                <v-divider></v-divider>-->
<!--                <v-card-text>-->
<!--                    <h2 class="text-h6 mb-2" v-if="searchHistory.length > 0">搜索历史</h2>-->
<!--                    <v-chip-group v-model="searchKey" column @update:model-value="search">-->
<!--                        <v-chip filter :value="item" variant="outlined" v-for="item in searchHistory" :key="item">-->
<!--                            {{ item }}-->
<!--                        </v-chip>-->
<!--                    </v-chip-group>-->
<!--                </v-card-text>-->
<!--                <v-divider />-->
<!--                <v-card-text class="pt-0">-->
<!--                    <hotList elevation="0" />-->
<!--                </v-card-text>-->
<!--            </v-card>-->
<!--        </v-dialog>-->
<!--    </v-app-bar>-->

<!--    &lt;!&ndash; AI 聊天对话框 &ndash;&gt;-->
<!--    <v-dialog v-model="chatbotDialog" persistent max-width="80%" max-height="90%">-->
<!--        <v-card class="chatbot-card">-->
<!--            <v-card-title class="headline d-flex justify-space-between chatbot-header">-->
<!--                <span>DeepSeek-V1</span>-->
<!--                <v-btn icon @click="closeChatbot" class="close-btn">-->
<!--                    <v-icon>mdi-close</v-icon>-->
<!--                </v-btn>-->
<!--            </v-card-title>-->
<!--            <v-card-text class="chatbot-body">-->
<!--                &lt;!&ndash; 模型选择下拉菜单（保留原配置） &ndash;&gt;-->
<!--                <v-select-->
<!--                    v-model="selectedModel"-->
<!--                    :items="modelOptions"-->
<!--                    item-title="text"-->
<!--                    item-value="value"-->
<!--                    label="选择模型"-->
<!--                    :menu-props="{ contentClass: 'custom-menu', minHeight: '200px' }"-->
<!--                    variant="outlined"-->
<!--                    hide-details-->
<!--                >-->
<!--                </v-select>-->
<!--                <div class="chat-container" ref="chatContainer">-->
<!--                    <div v-for="(msg, index) in chatHistory" :key="index" :class="['chat-bubble', msg.type]">-->
<!--                        <div v-if="msg.type === 'user'">-->
<!--                            <strong>用户：</strong>-->
<!--                            <div class="response-content">-->
<!--                                <template v-for="(line, idx) in msg.content.split('\n')" :key="idx">-->
<!--                                    <div class="response-line">-->
<!--                                        <span v-if="line.match(/^(\d+\. |\-|\*)/)">🔹</span>-->
<!--                                        {{ line.replace(/^(\d+\. |\-|\*)\s*/, '') }}-->
<!--                                    </div>-->
<!--                                </template>-->
<!--                            </div>-->
<!--                        </div>-->
<!--                        <div v-else-if="msg.type === 'ai'" class="chat-bubble ai">-->
<!--                            <strong>助理：</strong>-->
<!--                            <div v-if="msg.reasoning_content && msg.reasoning_content.trim()" class="reasoning-chain">-->
<!--                                🧠 推理路径：-->
<!--                                <pre>{{ msg.reasoning_content }}</pre>-->
<!--                            </div>-->
<!--                            <div class="response-content">-->
<!--                                <template v-for="(line, idx) in msg.content.split('\n')" :key="'line-' + idx">-->
<!--                                    <div class="response-line">-->
<!--                                        <span v-if="line.match(/^(\d+\. |\-|\*|▶)/)" class="decorator">-->
<!--                                            {{ getDecorator(line) }}-->
<!--                                        </span>-->
<!--                                        {{ formatContent(line) }}-->
<!--                                    </div>-->
<!--                                </template>-->
<!--                            </div>-->
<!--                            <div class="timestamp">{{ formatTime(msg.timestamp) }}</div>-->
<!--                        </div>-->
<!--                    </div>-->
<!--                </div>-->
<!--                <v-textarea v-model="userMessage" label="输入你的问题" rows="2" outlined @keyup.enter="sendMessage"-->
<!--                            class="chat-input" :disabled="isLoading"></v-textarea>-->
<!--                <v-btn color="primary" @click="sendMessage" class="send-btn" :loading="isLoading">发送</v-btn>-->
<!--&lt;!&ndash;                <v-progress-circular v-if="isLoading" indeterminate color="primary" class="mt-2"></v-progress-circular>&ndash;&gt;-->
<!--                <v-btn color="secondary" @click="clearChatHistory" class="clear-btn mt-2">清空聊天记录</v-btn>-->
<!--                <v-btn color="warning" @click="resetConversation" class="reset-btn mt-2">重置对话</v-btn>-->
<!--            </v-card-text>-->
<!--        </v-card>-->
<!--    </v-dialog>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref, onMounted, nextTick, watch } from 'vue';-->
<!--import { useRouter } from 'vue-router';-->
<!--import { apiGetUserSearchHistory } from '../../apis/user/user';-->
<!--import hotList from '../../components/hotList.vue';-->
<!--import auth from '../auth/index.vue';-->
<!--import instance from "../../apis/request.js";-->

<!--const chatbotDialog = ref(false);-->
<!--const userMessage = ref("");-->
<!--const chatHistory = ref([]);-->
<!--const chatContainer = ref(null);-->
<!--const isLoading = ref(false);-->
<!--const selectedModel = ref("deepseek-reasoner"); // 默认模型-->

<!--// 模型选项-->
<!--const modelOptions = ref([-->
<!--    { text: "带思维链 (deepseek-reasoner)", value: "deepseek-reasoner" },-->
<!--    { text: "不带思维链 (deepseek-chat)", value: "deepseek-chat" }-->
<!--]);-->

<!--watch(selectedModel, (newVal) => {-->
<!--    console.log('Selected model changed to:', newVal);-->
<!--});-->

<!--// 工具方法-->
<!--const getDecorator = (line) => {-->
<!--    if (line.startsWith('1.')) return '❶';-->
<!--    if (line.startsWith('-')) return '➖';-->
<!--    if (line.startsWith('*')) return '⭐';-->
<!--    return '🔹';-->
<!--};-->

<!--const formatContent = (line) => {-->
<!--    return line.replace(/^(\d+\. |\-|\*)\s*/, '');-->
<!--};-->

<!--const formatTime = (timestamp) => {-->
<!--    return new Date(timestamp).toLocaleTimeString('zh-CN', {-->
<!--        hour: '2-digit',-->
<!--        minute: '2-digit'-->
<!--    });-->
<!--};-->

<!--const openChatbot = () => {-->
<!--    chatbotDialog.value = true;-->
<!--};-->

<!--const closeChatbot = () => {-->
<!--    chatbotDialog.value = false;-->
<!--};-->

<!--const sendMessage = async () => {-->
<!--    if (!userMessage.value.trim()) return;-->

<!--    isLoading.value = true;-->

<!--    // 添加用户消息到聊天历史-->
<!--    chatHistory.value.push({-->
<!--        content: userMessage.value,-->
<!--        type: 'user',-->
<!--        reasoning_content: '',-->
<!--        timestamp: Date.now()-->
<!--    });-->

<!--    try {-->
<!--        const response = await instance.post('/bengo/chat', {-->
<!--            message: userMessage.value,-->
<!--            model: selectedModel.value // 传递所选模型-->
<!--        });-->
<!--        const { reasoning_content, content } = response.data;-->

<!--        // 添加 AI 回复到聊天历史-->
<!--        chatHistory.value.push({-->
<!--            reasoning_content: selectedModel.value === "deepseek-reasoner" ? reasoning_content : '',-->
<!--            content,-->
<!--            type: 'ai',-->
<!--            timestamp: Date.now()-->
<!--        });-->

<!--    } catch (error) {-->
<!--        console.error("请求失败:", error);-->
<!--        chatHistory.value.push({-->
<!--            content: `请求失败，请稍后重试：${error.message}`,-->
<!--            type: 'ai',-->
<!--            reasoning_content: '',-->
<!--            timestamp: Date.now()-->
<!--        });-->
<!--    } finally {-->
<!--        isLoading.value = false;-->
<!--        userMessage.value = ""; // 清空输入框-->

<!--        // 滚动到底部-->
<!--        nextTick(() => {-->
<!--            const chatBody = chatContainer.value;-->
<!--            if (chatBody) {-->
<!--                chatBody.scrollTop = chatBody.scrollHeight;-->
<!--            }-->
<!--        });-->
<!--    }-->
<!--};-->

<!--// 清空聊天记录-->
<!--const clearChatHistory = () => {-->
<!--    chatHistory.value = [];-->
<!--};-->

<!--// 重置对话-->
<!--const resetConversation = async () => {-->
<!--    try {-->
<!--        await instance.get('/bengo/reset');-->
<!--        chatHistory.value = [];-->
<!--        chatHistory.value.push({-->
<!--            content: "对话已重置",-->
<!--            type: 'ai',-->
<!--            reasoning_content: '',-->
<!--            timestamp: Date.now()-->
<!--        });-->
<!--    } catch (error) {-->
<!--        console.error("重置对话失败:", error);-->
<!--        chatHistory.value.push({-->
<!--            content: "重置对话失败，请稍后重试。",-->
<!--            type: 'ai',-->
<!--            reasoning_content: '',-->
<!--            timestamp: Date.now()-->
<!--        });-->
<!--    }-->
<!--};-->

<!--// 搜索相关逻辑-->
<!--const { clickEvent } = defineProps({ clickEvent: { type: Function, default: (type, data) => {} } });-->
<!--const searchInputRef = ref();-->
<!--const showSearch = ref(false);-->
<!--const searchKey = ref("");-->
<!--const router = useRouter();-->
<!--const searchHistory = ref([]);-->

<!--const openSearch = () => {-->
<!--    showSearch.value = !showSearch.value;-->
<!--};-->

<!--const search = () => {-->
<!--    showSearch.value = false;-->
<!--    if (!searchKey.value || searchKey.value.length === 0) {-->
<!--        router.push({ path: "/" });-->
<!--        return;-->
<!--    }-->
<!--    router.push({ path: "/video/search/" + searchKey.value });-->
<!--    apiGetUserSearchHistory().then(({ data }) => {-->
<!--        searchHistory.value = data.data;-->
<!--    });-->
<!--    searchKey.value = "";-->
<!--};-->

<!--onMounted(() => {-->
<!--    apiGetUserSearchHistory().then(({ data }) => {-->
<!--        searchHistory.value = data.data;-->
<!--    });-->
<!--});-->
<!--</script>-->

<!--<style scoped>-->
<!--.search-wrapper {-->
<!--    display: flex;-->
<!--    justify-content: center;-->
<!--    align-items: center;-->
<!--    width: 100%;-->
<!--}-->

<!--.ml-4 {-->
<!--    margin-left: 16px;-->
<!--}-->

<!--.d-flex {-->
<!--    display: flex;-->
<!--}-->

<!--.chatbot-card {-->
<!--    background: linear-gradient(135deg, #6a1b9a, #8e24aa);-->
<!--    border-radius: 16px;-->
<!--    padding: 20px;-->
<!--}-->

<!--.chatbot-header {-->
<!--    font-size: 20px;-->
<!--    font-weight: bold;-->
<!--    color: white;-->
<!--    padding: 10px 0;-->
<!--}-->

<!--.close-btn {-->
<!--    margin-left: auto;-->
<!--}-->

<!--.chatbot-body {-->
<!--    padding: 10px;-->
<!--}-->

<!--.chat-container {-->
<!--    max-height: 300px;-->
<!--    overflow-y: auto;-->
<!--    margin-bottom: 10px;-->
<!--}-->

<!--.chat-bubble {-->
<!--    max-width: 85%;-->
<!--    width: fit-content;-->
<!--    margin: 5px 0;-->
<!--    padding: 10px 15px;-->
<!--    border-radius: 20px;-->
<!--    border: 1px solid #ddd;-->
<!--}-->

<!--.chat-bubble.user {-->
<!--    background-color: #e0e0e0;-->
<!--    margin-left: auto;-->
<!--    border-bottom-right-radius: 4px;-->
<!--}-->

<!--.chat-bubble.ai {-->
<!--    background-color: #00bcd4;-->
<!--    color: white;-->
<!--    margin-right: auto;-->
<!--    border-bottom-left-radius: 4px;-->
<!--}-->

<!--.reasoning-chain {-->
<!--    background: rgba(0, 0, 0, 0.1);-->
<!--    padding: 12px;-->
<!--    border-radius: 8px;-->
<!--    margin-bottom: 12px;-->
<!--}-->

<!--.reasoning-chain pre {-->
<!--    white-space: pre-wrap;-->
<!--    font-size: 0.9em;-->
<!--    margin: 0;-->
<!--}-->

<!--.response-content {-->
<!--    margin-top: 5px;-->
<!--}-->

<!--.response-line {-->
<!--    display: flex;-->
<!--    align-items: flex-start;-->
<!--    margin: 2px 0;-->
<!--}-->

<!--.decorator {-->
<!--    margin-right: 5px;-->
<!--    font-size: 0.9em;-->
<!--}-->

<!--.timestamp {-->
<!--    font-size: 0.8em;-->
<!--    color: #999;-->
<!--    margin-top: 5px;-->
<!--    text-align: right;-->
<!--}-->

<!--.chat-input {-->
<!--    margin-top: 10px;-->
<!--    border-radius: 16px;-->
<!--}-->

<!--.send-btn, .clear-btn, .reset-btn {-->
<!--    margin-top: 10px;-->
<!--    width: 100%;-->
<!--    border-radius: 16px;-->
<!--}-->

<!--.send-btn {-->
<!--    background-color: #8e24aa;-->
<!--    color: white;-->
<!--    font-weight: bold;-->
<!--}-->

<!--.custom-menu {-->
<!--    background-color: white !important;-->
<!--    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);-->
<!--}-->

<!--.clear-btn {-->
<!--    background-color: #ccc;-->
<!--    color: black;-->
<!--    font-weight: bold;-->
<!--}-->

<!--.reset-btn {-->
<!--    background-color: #ff9800;-->
<!--    color: white;-->
<!--    font-weight: bold;-->
<!--}-->

<!--.model-select {-->
<!--    min-width: 200px;-->
<!--    background-color: white;-->
<!--    color: black;-->
<!--    margin-bottom: 10px;-->
<!--    border-radius: 4px;-->
<!--}-->

<!--.model-select :deep(.v-list) {-->
<!--    border-radius: 4px;-->
<!--}-->

<!--.model-select :deep(.v-list-item) {-->
<!--    color: black;-->
<!--}-->

<!--.model-select :deep(.v-select__selection) {-->
<!--    color: black;-->
<!--}-->

<!--@media (max-width: 600px) {-->
<!--    .chatbot-card {-->
<!--        width: 100%;-->
<!--        padding: 15px;-->
<!--    }-->
<!--    .chat-container {-->
<!--        max-height: 70vh;-->
<!--    }-->
<!--    .chat-input, .send-btn, .clear-btn, .reset-btn {-->
<!--        width: 100%;-->
<!--    }-->
<!--}-->

<!--::v-deep(.v-field__outline) {-->
<!--    &#45;&#45;v-field-border-width: 0px;-->
<!--}-->

<!--@media only screen and (min-width: 700px) {-->
<!--    .d700-hide {-->
<!--        display: none;-->
<!--    }-->
<!--}-->

<!--@media only screen and (max-width: 700px) {-->
<!--    .e700-hide {-->
<!--        display: none;-->
<!--    }-->
<!--}-->
<!--</style>-->


<!--<template>-->
<!--    <v-app-bar floating name="app-bar" :elevation="0" color="#252632">-->
<!--        <v-app-bar-nav-icon icon="mdi-menu" @click="clickEvent(1, 1)"></v-app-bar-nav-icon>-->
<!--        <v-app-bar-title>BenGo视频平台</v-app-bar-title>-->

<!--        <div class="search-wrapper d-flex align-center">-->
<!--            <v-text-field class="e700-hide" hide-details :density="'comfortable'" prepend-inner-icon="mdi-magnify"-->
<!--                          single-line clearable @click="openSearch()" placeholder="搜索视频">-->
<!--            </v-text-field>-->
<!--            <v-btn color="primary" @click="openChatbot" icon class="ml-4" aria-label="Open Chatbot">-->
<!--                <v-icon>mdi-robot</v-icon>-->
<!--            </v-btn>-->
<!--        </div>-->

<!--        <v-toolbar-items variant="plain" class="pr-2">-->
<!--            <v-btn class="d700-hide" @click="openSearch()" :variant="'text'">-->
<!--                <v-icon>mdi-magnify</v-icon>-->
<!--                搜索-->
<!--            </v-btn>-->
<!--            <auth />-->
<!--        </v-toolbar-items>-->

<!--        <v-dialog v-model="showSearch" max-width="500px" :location="'top'">-->
<!--            <v-card>-->
<!--                <v-toolbar flat color="background">-->
<!--                    <v-text-field autofocus ref="searchInputRef" hide-details prepend-inner-icon="mdi-magnify"-->
<!--                                  single-line clearable v-model="searchKey" @click:clear="search()"-->
<!--                                  v-on:keyup.enter="search" placeholder="搜索视频">-->
<!--                    </v-text-field>-->
<!--                </v-toolbar>-->
<!--                <v-divider></v-divider>-->
<!--                <v-card-text>-->
<!--                    <h2 class="text-h6 mb-2" v-if="searchHistory.length > 0">搜索历史</h2>-->
<!--                    <v-chip-group v-model="searchKey" column @update:model-value="search">-->
<!--                        <v-chip filter :value="item" variant="outlined" v-for="item in searchHistory" :key="item">-->
<!--                            {{ item }}-->
<!--                        </v-chip>-->
<!--                    </v-chip-group>-->
<!--                </v-card-text>-->
<!--                <v-divider />-->
<!--                <v-card-text class="pt-0">-->
<!--                    <hotList elevation="0" />-->
<!--                </v-card-text>-->
<!--            </v-card>-->
<!--        </v-dialog>-->
<!--    </v-app-bar>-->

<!--    <v-dialog v-model="chatbotDialog" persistent max-width="80%" max-height="90%">-->
<!--        <v-card class="chatbot-card">-->
<!--            <v-card-title class="headline d-flex justify-space-between chatbot-header">-->
<!--                <span>DeepSeek-V1</span>-->
<!--                <v-btn icon @click="closeChatbot" class="close-btn">-->
<!--                    <v-icon>mdi-close</v-icon>-->
<!--                </v-btn>-->
<!--            </v-card-title>-->
<!--            <v-card-text class="chatbot-body">-->
<!--                <v-select-->
<!--                    v-model="selectedModel"-->
<!--                    :items="modelOptions"-->
<!--                    item-title="text"-->
<!--                    item-value="value"-->
<!--                    label="选择模型"-->
<!--                    :menu-props="{ contentClass: 'custom-menu', minHeight: '200px' }"-->
<!--                    variant="outlined"-->
<!--                    hide-details-->
<!--                >-->
<!--                </v-select>-->
<!--                <div class="chat-container" ref="chatContainer">-->
<!--                    <div v-for="(msg, index) in chatHistory" :key="index" :class="['chat-bubble', msg.type]">-->
<!--                        <div v-if="msg.type === 'user'">-->
<!--                            <strong>用户：</strong>-->
<!--                            <div class="response-content">-->
<!--                                <template v-for="(line, idx) in msg.content.split('\n')" :key="idx">-->
<!--                                    <div class="response-line">-->
<!--                                        <span v-if="line.match(/^(\d+\. | - | \*)/)">🔹</span>-->
<!--                                        {{ line.replace(/^(\d+\. | - | *)\s*/, '') }}-->
<!--                                    </div>-->
<!--                                </template>-->
<!--                            </div>-->
<!--                        </div>-->
<!--                        <div v-else-if="msg.type === 'ai'" class="chat-bubble ai">-->
<!--                            <strong>助理：</strong>-->
<!--                            <div v-if="msg.reasoning_content && msg.reasoning_content.trim()" class="reasoning-chain">-->
<!--                                🧠 推理路径：-->
<!--                                <pre>{{ msg.reasoning_content }}</pre>-->
<!--                            </div>-->
<!--                            <div class="response-content">-->
<!--                                <template v-for="(line, idx) in msg.content.split('\n')" :key="'line-' + idx">-->
<!--                                    <div class="response-line">-->
<!--                                        <span v-if="line.match(/^(\d+\. | - | \*|▶)/)" class="decorator">-->
<!--                                            {{ getDecorator(line) }}-->
<!--                                        </span>-->
<!--                                        {{ formatContent(line) }}-->
<!--                                    </div>-->
<!--                                </template>-->
<!--                            </div>-->
<!--                            <div class="timestamp">{{ formatTime(msg.timestamp) }}</div>-->
<!--                        </div>-->
<!--                    </div>-->
<!--                </div>-->
<!--                <v-textarea v-model="userMessage" label="输入你的问题" rows="2" outlined @keyup.enter="sendMessage"-->
<!--                            class="chat-input" :disabled="isLoading"></v-textarea>-->
<!--                <v-btn color="primary" @click="sendMessage" class="send-btn" :loading="isLoading">发送</v-btn>-->
<!--                <v-btn color="secondary" @click="clearChatHistory" class="clear-btn mt-2">清空聊天记录</v-btn>-->
<!--                <v-btn color="warning" @click="resetConversation" class="reset-btn mt-2">重置对话</v-btn>-->
<!--            </v-card-text>-->
<!--        </v-card>-->
<!--    </v-dialog>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref, onMounted, nextTick, watch } from 'vue';-->
<!--import { useRouter } from 'vue-router';-->
<!--import { apiGetUserSearchHistory } from '../../apis/user/user';-->
<!--import hotList from '../../components/hotList.vue';-->
<!--import auth from '../auth/index.vue';-->
<!--import instance from "../../apis/request.js";-->

<!--const chatbotDialog = ref(false);-->
<!--const userMessage = ref("");-->
<!--const chatHistory = ref([]);-->
<!--const chatContainer = ref(null);-->
<!--const isLoading = ref(false);-->
<!--const selectedModel = ref("deepseek-reasoner");-->
<!--const modelOptions = ref([-->
<!--    { text: "带思维链 (deepseek-reasoner)", value: "deepseek-reasoner" },-->
<!--    { text: "不带思维链 (deepseek-chat)", value: "deepseek-chat" }-->
<!--]);-->

<!--watch(selectedModel, (newVal) => {-->
<!--    console.log('Selected model changed to:', newVal);-->
<!--});-->

<!--const getDecorator = (line) => {-->
<!--    if (line.startsWith('1.')) return '❶';-->
<!--    if (line.startsWith('-')) return '➖';-->
<!--    if (line.startsWith('*')) return '⭐';-->
<!--    return '🔹';-->
<!--};-->

<!--const formatContent = (line) => {-->
<!--    return line.replace(/^(\d+\. | - | *)\s*/, '');-->
<!--};-->

<!--const formatTime = (timestamp) => {-->
<!--    return new Date(timestamp).toLocaleTimeString('zh-CN', {-->
<!--        hour: '2-digit',-->
<!--        minute: '2-digit'-->
<!--    });-->
<!--};-->

<!--const openChatbot = () => {-->
<!--    chatbotDialog.value = true;-->
<!--};-->

<!--const closeChatbot = () => {-->
<!--    chatbotDialog.value = false;-->
<!--};-->

<!--const sendMessage = async () => {-->
<!--    if (!userMessage.value.trim()) return;-->

<!--    isLoading.value = true;-->

<!--    // 添加用户消息-->
<!--    chatHistory.value.push({-->
<!--        content: userMessage.value,-->
<!--        type: 'user',-->
<!--        reasoning_content: '',-->
<!--        timestamp: Date.now()-->
<!--    });-->

<!--    // 初始化 AI 消息-->
<!--    const aiMessageIndex = chatHistory.value.length;-->
<!--    chatHistory.value.push({-->
<!--        content: '',-->
<!--        type: 'ai',-->
<!--        reasoning_content: '',-->
<!--        timestamp: Date.now()-->
<!--    });-->

<!--    console.log('Initial chatHistory:', JSON.stringify(chatHistory.value));-->

<!--    try {-->
<!--        const response = await fetch('/api/bengo/chat', {-->
<!--            method: 'POST',-->
<!--            headers: {-->
<!--                'Content-Type': 'application/json',-->
<!--                'Accept': 'text/event-stream'-->
<!--            },-->
<!--            body: JSON.stringify({-->
<!--                message: userMessage.value,-->
<!--                model: selectedModel.value-->
<!--            })-->
<!--        });-->

<!--        if (!response.ok) {-->
<!--            throw new Error(`HTTP error! status: ${response.status}`);-->
<!--        }-->

<!--        const reader = response.body.getReader();-->
<!--        const decoder = new TextDecoder('utf-8');-->

<!--        const processStream = async () => {-->
<!--            while (true) {-->
<!--                const { done, value } = await reader.read();-->
<!--                if (done) {-->
<!--                    console.log('Stream finished');-->
<!--                    break;-->
<!--                }-->

<!--                const chunk = decoder.decode(value, { stream: true });-->
<!--                console.log('Received chunk:', chunk);-->

<!--                const lines = chunk.split('\n');-->
<!--                for (const line of lines) {-->
<!--                    if (line.startsWith('data: ')) {-->
<!--                        const data = line.substring(6);-->
<!--                        if (data === '[DONE]') continue;-->

<!--                        try {-->
<!--                            const json = JSON.parse(data);-->
<!--                            if (json.content) {-->
<!--                                // 使用 push 和 splice 确保响应式更新-->
<!--                                const currentMessage = chatHistory.value[aiMessageIndex];-->
<!--                                currentMessage.content += json.content;-->
<!--                                chatHistory.value.splice(aiMessageIndex, 1, { ...currentMessage });-->
<!--                                console.log('Updated chatHistory:', JSON.stringify(chatHistory.value));-->
<!--                                await nextTick();-->
<!--                                scrollToBottom();-->
<!--                            }-->
<!--                        } catch (e) {-->
<!--                            console.error('解析 JSON 失败:', e, 'Raw data:', data);-->
<!--                        }-->
<!--                    }-->
<!--                }-->
<!--            }-->
<!--        };-->

<!--        await processStream();-->
<!--    } catch (error) {-->
<!--        console.error("请求失败:", error);-->
<!--        const currentMessage = chatHistory.value[aiMessageIndex];-->
<!--        currentMessage.content += `\n请求失败，请稍后重试：${error.message}`;-->
<!--        chatHistory.value.splice(aiMessageIndex, 1, { ...currentMessage });-->
<!--        await nextTick();-->
<!--        scrollToBottom();-->
<!--    } finally {-->
<!--        isLoading.value = false;-->
<!--        userMessage.value = "";-->
<!--    }-->
<!--};-->

<!--const scrollToBottom = async () => {-->
<!--    await nextTick();-->
<!--    const chatBody = chatContainer.value;-->
<!--    if (chatBody) {-->
<!--        chatBody.scrollTop = chatBody.scrollHeight;-->
<!--        console.log('Scrolled to bottom, scrollHeight:', chatBody.scrollHeight);-->
<!--    } else {-->
<!--        console.warn('chatContainer is null');-->
<!--    }-->
<!--};-->

<!--const clearChatHistory = () => {-->
<!--    chatHistory.value = [];-->
<!--};-->

<!--const resetConversation = async () => {-->
<!--    try {-->
<!--        await instance.get('/bengo/reset');-->
<!--        chatHistory.value = [];-->
<!--        chatHistory.value.push({-->
<!--            content: "对话已重置",-->
<!--            type: 'ai',-->
<!--            reasoning_content: '',-->
<!--            timestamp: Date.now()-->
<!--        });-->
<!--    } catch (error) {-->
<!--        console.error("重置对话失败:", error);-->
<!--        chatHistory.value.push({-->
<!--            content: "重置对话失败，请稍后重试。",-->
<!--            type: 'ai',-->
<!--            reasoning_content: '',-->
<!--            timestamp: Date.now()-->
<!--        });-->
<!--    }-->
<!--};-->

<!--// 搜索相关逻辑-->
<!--const { clickEvent } = defineProps({ clickEvent: { type: Function, default: (type, data) => {} } });-->
<!--const searchInputRef = ref();-->
<!--const showSearch = ref(false);-->
<!--const searchKey = ref("");-->
<!--const router = useRouter();-->
<!--const searchHistory = ref([]);-->

<!--const openSearch = () => {-->
<!--    showSearch.value = !showSearch.value;-->
<!--};-->

<!--const search = () => {-->
<!--    showSearch.value = false;-->
<!--    if (!searchKey.value || searchKey.value.length === 0) {-->
<!--        router.push({ path: "/" });-->
<!--        return;-->
<!--    }-->
<!--    router.push({ path: "/video/search/" + searchKey.value });-->
<!--    apiGetUserSearchHistory().then(({ data }) => {-->
<!--        searchHistory.value = data.data;-->
<!--    });-->
<!--    searchKey.value = "";-->
<!--};-->

<!--onMounted(() => {-->
<!--    apiGetUserSearchHistory().then(({ data }) => {-->
<!--        searchHistory.value = data.data;-->
<!--    });-->
<!--});-->
<!--</script>-->

<!--<style scoped>-->
<!--.search-wrapper {-->
<!--    display: flex;-->
<!--    justify-content: center;-->
<!--    align-items: center;-->
<!--    width: 100%;-->
<!--}-->

<!--.ml-4 {-->
<!--    margin-left: 16px;-->
<!--}-->

<!--.d-flex {-->
<!--    display: flex;-->
<!--}-->

<!--.chatbot-card {-->
<!--    background: linear-gradient(135deg, #6a1b9a, #8e24aa);-->
<!--    border-radius: 16px;-->
<!--    padding: 20px;-->
<!--}-->

<!--.chatbot-header {-->
<!--    font-size: 20px;-->
<!--    font-weight: bold;-->
<!--    color: white;-->
<!--    padding: 10px 0;-->
<!--}-->

<!--.close-btn {-->
<!--    margin-left: auto;-->
<!--}-->

<!--.chatbot-body {-->
<!--    padding: 10px;-->
<!--}-->

<!--.chat-container {-->
<!--    max-height: 300px;-->
<!--    overflow-y: auto;-->
<!--    margin-bottom: 10px;-->
<!--}-->

<!--.chat-bubble {-->
<!--    max-width: 85%;-->
<!--    width: fit-content;-->
<!--    margin: 5px 0;-->
<!--    padding: 10px 15px;-->
<!--    border-radius: 20px;-->
<!--    border: 1px solid #ddd;-->
<!--}-->

<!--.chat-bubble.user {-->
<!--    background-color: #e0e0e0;-->
<!--    margin-left: auto;-->
<!--    border-bottom-right-radius: 4px;-->
<!--}-->

<!--.chat-bubble.ai {-->
<!--    background-color: #00bcd4;-->
<!--    color: white;-->
<!--    margin-right: auto;-->
<!--    border-bottom-left-radius: 4px;-->
<!--}-->

<!--.reasoning-chain {-->
<!--    background: rgba(0, 0, 0, 0.1);-->
<!--    padding: 12px;-->
<!--    border-radius: 8px;-->
<!--    margin-bottom: 12px;-->
<!--}-->

<!--.reasoning-chain pre {-->
<!--    white-space: pre-wrap;-->
<!--    font-size: 0.9em;-->
<!--    margin: 0;-->
<!--}-->

<!--.response-content {-->
<!--    margin-top: 5px;-->
<!--}-->

<!--.response-line {-->
<!--    display: flex;-->
<!--    align-items: flex-start;-->
<!--    margin: 2px 0;-->
<!--}-->

<!--.decorator {-->
<!--    margin-right: 5px;-->
<!--    font-size: 0.9em;-->
<!--}-->

<!--.timestamp {-->
<!--    font-size: 0.8em;-->
<!--    color: #999;-->
<!--    margin-top: 5px;-->
<!--    text-align: right;-->
<!--}-->

<!--.chat-input {-->
<!--    margin-top: 10px;-->
<!--    border-radius: 16px;-->
<!--}-->

<!--.send-btn, .clear-btn, .reset-btn {-->
<!--    margin-top: 10px;-->
<!--    width: 100%;-->
<!--    border-radius: 16px;-->
<!--}-->

<!--.send-btn {-->
<!--    background-color: #8e24aa;-->
<!--    color: white;-->
<!--    font-weight: bold;-->
<!--}-->

<!--.custom-menu {-->
<!--    background-color: white !important;-->
<!--    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);-->
<!--}-->

<!--.clear-btn {-->
<!--    background-color: #ccc;-->
<!--    color: black;-->
<!--    font-weight: bold;-->
<!--}-->

<!--.reset-btn {-->
<!--    background-color: #ff9800;-->
<!--    color: white;-->
<!--    font-weight: bold;-->
<!--}-->

<!--.model-select {-->
<!--    min-width: 200px;-->
<!--    background-color: white;-->
<!--    color: black;-->
<!--    margin-bottom: 10px;-->
<!--    border-radius: 4px;-->
<!--}-->

<!--.model-select :deep(.v-list) {-->
<!--    border-radius: 4px;-->
<!--}-->

<!--.model-select :deep(.v-list-item) {-->
<!--    color: black;-->
<!--}-->

<!--.model-select :deep(.v-select__selection) {-->
<!--    color: black;-->
<!--}-->

<!--@media (max-width: 600px) {-->
<!--    .chatbot-card {-->
<!--        width: 100%;-->
<!--        padding: 15px;-->
<!--    }-->
<!--    .chat-container {-->
<!--        max-height: 70vh;-->
<!--    }-->
<!--    .chat-input, .send-btn, .clear-btn, .reset-btn {-->
<!--        width: 100%;-->
<!--    }-->
<!--}-->

<!--::v-deep(.v-field__outline) {-->
<!--    &#45;&#45;v-field-border-width: 0px;-->
<!--}-->

<!--@media only screen and (min-width: 700px) {-->
<!--    .d700-hide {-->
<!--        display: none;-->
<!--    }-->
<!--}-->

<!--@media only screen and (max-width: 700px) {-->
<!--    .e700-hide {-->
<!--        display: none;-->
<!--    }-->
<!--}-->
<!--</style>-->


<template>
    <v-app-bar floating name="app-bar" :elevation="0" color="#252632">
        <v-app-bar-nav-icon icon="mdi-menu" @click="handleClick"></v-app-bar-nav-icon>
        <v-app-bar-title>BenGo视频平台</v-app-bar-title>
        <div class="search-wrapper d-flex align-center">
            <v-text-field class="e700-hide" hide-details :density="'comfortable'" prepend-inner-icon="mdi-magnify"
                          single-line clearable @click="openSearch()" placeholder="搜索视频"></v-text-field>
            <v-btn color="primary" @click="openChatbot" icon class="ml-4" aria-label="Open Chatbot">
                <v-icon>mdi-robot</v-icon>
            </v-btn>
        </div>
        <v-toolbar-items variant="plain" class="pr-2">
            <v-btn class="d700-hide" @click="openSearch()" :variant="'text'">
                <v-icon>mdi-magnify</v-icon>
                搜索
            </v-btn>
            <auth/>
        </v-toolbar-items>
        <v-dialog v-model="showSearch" max-width="500px" :location="'top'">
            <v-card>
                <v-toolbar flat color="background">
                    <v-text-field autofocus ref="searchInputRef" hide-details prepend-inner-icon="mdi-magnify"
                                  single-line clearable v-model="searchKey" @click:clear="search()"
                                  v-on:keyup.enter="search" placeholder="搜索视频"></v-text-field>
                </v-toolbar>
                <v-divider></v-divider>
                <v-card-text>
                    <h2 class="text-h6 mb-2" v-if="searchHistory.length > 0">搜索历史</h2>
                    <v-chip-group v-model="searchKey" column @update:model-value="search">
                        <v-chip filter :value="item" variant="outlined" v-for="item in searchHistory" :key="item">
                            {{ item }}
                        </v-chip>
                    </v-chip-group>
                </v-card-text>
                <v-divider/>
                <v-card-text class="pt-0">
                    <hotList elevation="0"/>
                </v-card-text>
            </v-card>
        </v-dialog>
    </v-app-bar>
    <v-dialog v-model="chatbotDialog" persistent max-width="80%" max-height="90%">
        <v-card class="chatbot-card">
            <v-card-title class="headline d-flex justify-space-between chatbot-header">
                <span>DeepSeek-V1</span>
                <v-btn icon @click="closeChatbot" class="close-btn">
                    <v-icon>mdi-close</v-icon>
                </v-btn>
            </v-card-title>
            <v-card-text class="chatbot-body">
                <v-select v-model="selectedModel" :items="modelOptions" item-title="text" item-value="value"
                          label="选择模型" :menu-props="{ contentClass: 'custom-menu', minHeight: '200px' }"
                          variant="outlined" hide-details></v-select>
                <div class="chat-container" ref="chatContainer">
                    <div v-for="(msg, index) in chatHistory" :key="index" :class="['chat-bubble', msg.type]">
                        <div v-if="msg.type === 'user'">
                            <strong>用户：</strong>
                            <div class="response-content">
                                <template v-for="(line, idx) in msg.content.split('\n')" :key="idx">
                                    <div class="response-line">
                                        <span v-if="line.match(/^(\d+\. | - | \*)/)">🔹</span>
                                        {{ line.replace(/^(\d+\. | - | *)\s*/, '') }}
                                    </div>
                                </template>
                            </div>
                        </div>
                        <div v-else-if="msg.type === 'ai'" class="chat-bubble ai">
                            <strong>助理：</strong>
                            <div v-if="msg.reasoning_content && msg.reasoning_content.trim()" class="reasoning-chain">
                                🧠 推理路径：
                                <pre>{{ msg.reasoning_content }}</pre>
                            </div>
                            <div class="response-content">
                                <template v-for="(line, idx) in msg.content.split('\n')" :key="'line-' + idx">
                                    <div class="response-line">
                                        <span v-if="line.match(/^(\d+\. | - | \*|▶)/)"
                                              class="decorator">{{ getDecorator(line) }}</span>
                                        {{ formatContent(line) }}
                                    </div>
                                </template>
                            </div>
                            <div class="timestamp">{{ formatTime(msg.timestamp) }}</div>
                        </div>
                    </div>
                </div>
                <v-textarea v-model="userMessage" label="输入你的问题" rows="2" outlined @keyup.enter="sendMessage"
                            class="chat-input" :disabled="isLoading"></v-textarea>
                <v-btn color="primary" @click="sendMessage" class="send-btn" :loading="isLoading">发送</v-btn>
                <v-btn color="secondary" @click="clearChatHistory" class="clear-btn mt-2">清空聊天记录</v-btn>
                <v-btn color="warning" @click="resetConversation" class="reset-btn mt-2">重置对话</v-btn>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>


<script setup>
import {ref, onMounted, nextTick, watch} from 'vue';
import {useRouter} from 'vue-router';
import {apiGetUserSearchHistory} from '../../apis/user/user';
import hotList from '../../components/hotList.vue';
import auth from '../auth/index.vue';
import instance from "../../apis/request.js";

// 定义 props（必须放在顶部）
const {clickEvent} = defineProps({
    clickEvent: {
        type: Function,
        default: (type, data) => {
            // console.log('Default clickEvent');
        }
    }
});

// 点击事件处理
const handleClick = () => {
    // console.log('Nav icon clicked');
    // console.log('clickEvent is:', clickEvent);
    clickEvent(1, 1);
};

// 聊天机器人相关
const chatbotDialog = ref(false);
const userMessage = ref("");
const chatHistory = ref([]);
const chatContainer = ref(null);
const isLoading = ref(false);
const selectedModel = ref("deepseek-reasoner");
const modelOptions = ref([
    {text: "带思维链 (deepseek-reasoner)", value: "deepseek-reasoner"},
    {text: "不带思维链 (deepseek-chat)", value: "deepseek-chat"}
]);

watch(selectedModel, (newVal) => {
});

const getDecorator = (line) => {
    if (line.startsWith('1.')) return '❶';
    if (line.startsWith('-')) return '➖';
    if (line.startsWith('*')) return '⭐';
    return '🔹';
};

const formatContent = (line) => {
    return line.replace(/^(\d+\. | - | *)\s*/, '');
};

const formatTime = (timestamp) => {
    return new Date(timestamp).toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
};

const openChatbot = () => {
    chatbotDialog.value = true;
};
const closeChatbot = () => {
    chatbotDialog.value = false;
};

const sendMessage = async () => {
    if (!userMessage.value.trim()) return;
    isLoading.value = true;
    chatHistory.value.push({content: userMessage.value, type: 'user', reasoning_content: '', timestamp: Date.now()});
    const aiMessageIndex = chatHistory.value.length;
    chatHistory.value.push({content: '', type: 'ai', reasoning_content: '', timestamp: Date.now()});
    try {
        const response = await fetch('/api/bengo/chat', {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Accept': 'text/event-stream'},
            body: JSON.stringify({message: userMessage.value, model: selectedModel.value})
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const reader = response.body.getReader();
        const decoder = new TextDecoder('utf-8');
        const processStream = async () => {
            while (true) {
                const {done, value} = await reader.read();
                if (done) {
                    console.log('流式输出完成');
                    break;
                }
                const chunk = decoder.decode(value, {stream: true});
                const lines = chunk.split('\n').filter(line => line.trim() !== '');
                for (const line of lines) {
                    let data = line.trim();
                    if (data.startsWith('data:')) data = data.replace(/^data:\s*/, '');
                    if (data === '[DONE]' || !data || data === 'data') continue;
                    try {
                        const json = JSON.parse(data);
                        const currentMessage = chatHistory.value[aiMessageIndex];
                        if ('content' in json && typeof json.content === 'string') currentMessage.content += json.content;
                        if ('reasoning_content' in json && typeof json.reasoning_content === 'string') currentMessage.reasoning_content += json.reasoning_content;
                        if (!('content' in json) && !('reasoning_content' in json)) continue;
                        chatHistory.value.splice(aiMessageIndex, 1, {...currentMessage});
                        await nextTick();
                        scrollToBottom();
                    } catch (e) {
                        console.error('Failed to parse JSON:', e, 'Raw data:', data);
                    }
                }
            }
        };
        await processStream();
    } catch (error) {
        console.error("Request failed:", error);
        const currentMessage = chatHistory.value[aiMessageIndex];
        currentMessage.content += `\n请求失败，请稍后重试：${error.message}`;
        chatHistory.value.splice(aiMessageIndex, 1, {...currentMessage});
        await nextTick();
        scrollToBottom();
    } finally {
        isLoading.value = false;
        userMessage.value = "";
    }
};

const scrollToBottom = async () => {
    await nextTick();
    const chatBody = chatContainer.value;
    if (chatBody) chatBody.scrollTop = chatBody.scrollHeight;
};

const clearChatHistory = () => {
    chatHistory.value = [];
};

const resetConversation = async () => {
    try {
        await instance.get('/bengo/reset');
        chatHistory.value = [];
        chatHistory.value.push({content: "对话已重置", type: 'ai', reasoning_content: '', timestamp: Date.now()});
    } catch (error) {
        console.error("Reset conversation failed:", error);
        chatHistory.value.push({
            content: "重置对话失败，请稍后重试。",
            type: 'ai',
            reasoning_content: '',
            timestamp: Date.now()
        });
    }
};

// 搜索相关
const searchInputRef = ref();
const showSearch = ref(false);
const searchKey = ref("");
const router = useRouter();
const searchHistory = ref([]); // 修正拼写错误

const openSearch = () => {
    showSearch.value = !showSearch.value;
};

const search = () => {
    showSearch.value = false;
    if (!searchKey.value || searchKey.value.length === 0) {
        router.push({path: "/"});
        return;
    }
    router.push({path: "/video/search/" + searchKey.value});
    apiGetUserSearchHistory().then(({data}) => {
        searchHistory.value = data.data;
    });
    searchKey.value = "";
};

// 初始化
onMounted(() => {
    // console.log('header.vue mounted, clickEvent:', clickEvent);
    apiGetUserSearchHistory().then(({data}) => {
        searchHistory.value = data.data;
    });
});
</script>

<style scoped>
/* 样式保持不变 */
.search-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
}

.ml-4 {
    margin-left: 16px;
}

.d-flex {
    display: flex;
}

.chatbot-card {
    background: linear-gradient(135deg, #6a1b9a, #8e24aa);
    border-radius: 16px;
    padding: 20px;
}

.chatbot-header {
    font-size: 20px;
    font-weight: bold;
    color: white;
    padding: 10px 0;
}

.close-btn {
    margin-left: auto;
}

.chatbot-body {
    padding: 10px;
}

.chat-container {
    max-height: 300px;
    overflow-y: auto;
    margin-bottom: 10px;
}

.chat-bubble {
    max-width: 85%;
    width: fit-content;
    margin: 5px 0;
    padding: 10px 15px;
    border-radius: 20px;
    border: 1px solid #ddd;
}

.chat-bubble.user {
    background-color: #e0e0e0;
    margin-left: auto;
    border-bottom-right-radius: 4px;
}

.chat-bubble.ai {
    background-color: #00bcd4;
    color: white;
    margin-right: auto;
    border-bottom-left-radius: 4px;
}

.reasoning-chain {
    background: rgba(0, 0, 0, 0.1);
    padding: 12px;
    border-radius: 8px;
    margin-bottom: 12px;
}

.reasoning-chain pre {
    white-space: pre-wrap;
    font-size: 0.9em;
    margin: 0;
}

.response-content {
    margin-top: 5px;
}

.response-line {
    display: flex;
    align-items: flex-start;
    margin: 2px 0;
}

.decorator {
    margin-right: 5px;
    font-size: 0.9em;
}

.timestamp {
    font-size: 0.8em;
    color: #999;
    margin-top: 5px;
    text-align: right;
}

.chat-input {
    margin-top: 10px;
    border-radius: 16px;
}

.send-btn, .clear-btn, .reset-btn {
    margin-top: 10px;
    width: 100%;
    border-radius: 16px;
}

.send-btn {
    background-color: #8e24aa;
    color: white;
    font-weight: bold;
}

.custom-menu {
    background-color: white !important;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.clear-btn {
    background-color: #ccc;
    color: black;
    font-weight: bold;
}

.reset-btn {
    background-color: #ff9800;
    color: white;
    font-weight: bold;
}

@media (max-width: 600px) {
    .chatbot-card {
        width: 100%;
        padding: 15px;
    }

    .chat-container {
        max-height: 70vh;
    }

    .chat-input, .send-btn, .clear-btn, .reset-btn {
        width: 100%;
    }
}

::v-deep(.v-field__outline) {
    --v-field-border-width: 0px;
}

@media only screen and (min-width: 700px) {
    .d700-hide {
        display: none;
    }
}

@media only screen and (max-width: 700px) {
    .e700-hide {
        display: none;
    }
}
</style>