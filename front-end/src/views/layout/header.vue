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
                            class="chat-input" :disabled="isLoading || isRecording"></v-textarea>
                <v-btn color="primary" @click="startRecording" :disabled="isRecording || isLoading" class="mr-2">
                    开始录音
                </v-btn>
                <v-btn color="error" @click="stopRecording" :disabled="!isRecording || isLoading" class="mr-2">
                    停止录音
                </v-btn>
                <v-btn color="primary" @click="sendMessage" class="send-btn" :loading="isLoading" :disabled="isRecording">
                    发送
                </v-btn>
                <v-btn color="secondary" @click="clearChatHistory" class="clear-btn mt-2">清空聊天记录</v-btn>
                <v-btn color="warning" @click="resetConversation" class="reset-btn mt-2">重置对话</v-btn>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue';
import { useRouter } from 'vue-router';
import { apiGetUserSearchHistory } from '../../apis/user/user';
import hotList from '../../components/hotList.vue';
import auth from '../auth/index.vue';
import instance from "../../apis/request.js";

const { clickEvent } = defineProps({
    clickEvent: { type: Function, default: (type, data) => {} }
});

const handleClick = () => clickEvent(1, 1);

// 聊天机器人相关
const chatbotDialog = ref(false);
const userMessage = ref("");
const chatHistory = ref([]);
const chatContainer = ref(null);
const isLoading = ref(false);
const isRecording = ref(false);
const selectedModel = ref("deepseek-reasoner");
const modelOptions = ref([
    { text: "带思维链 (deepseek-reasoner)", value: "deepseek-reasoner" },
    { text: "不带思维链 (deepseek-chat)", value: "deepseek-chat" }
]);

watch(selectedModel, (newVal) => {});

const getDecorator = (line) => {
    if (line.startsWith('1.')) return '❶';
    if (line.startsWith('-')) return '➖';
    if (line.startsWith('*')) return '⭐';
    return '🔹';
};

const formatContent = (line) => line.replace(/^(\d+\. | - | *)\s*/, '');
const formatTime = (timestamp) => new Date(timestamp).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });

const openChatbot = () => chatbotDialog.value = true;
const closeChatbot = () => chatbotDialog.value = false;

const startRecording = async () => {
    try {
        const response = await instance.post('/bengo/start-recording');
        if (response.data.status === 'recording') {
            isRecording.value = true;
            chatHistory.value.push({
                content: '开始录音...',
                type: 'ai',
                reasoning_content: '',
                timestamp: Date.now()
            });
        } else {
            chatHistory.value.push({
                content: '录音启动失败: ' + (response.data.error || '请登录后再用此功能'),
                type: 'ai',
                reasoning_content: '',
                timestamp: Date.now()
            });
        }
    } catch (error) {
        chatHistory.value.push({
            content: '录音启动失败: ' + error.message,
            type: 'ai',
            reasoning_content: '',
            timestamp: Date.now()
        });
    }
    scrollToBottom();
};

const stopRecording = async () => {
    try {
        const response = await instance.post('/bengo/stop-recording');
        isRecording.value = false;
        if (response.data.text) {
            userMessage.value = response.data.text;
            await sendMessage();
        } else {
            chatHistory.value.push({
                content: '语音识别失败: ' + (response.data.error || '未知错误'),
                type: 'ai',
                reasoning_content: '',
                timestamp: Date.now()
            });
        }
    } catch (error) {
        isRecording.value = false;
        chatHistory.value.push({
            content: '停止录音失败: ' + error.message,
            type: 'ai',
            reasoning_content: '',
            timestamp: Date.now()
        });
    }
    scrollToBottom();
};

const sendMessage = async () => {
    if (!userMessage.value.trim()) return;
    isLoading.value = true;
    chatHistory.value.push({ content: userMessage.value, type: 'user', reasoning_content: '', timestamp: Date.now() });
    const aiMessageIndex = chatHistory.value.length;
    chatHistory.value.push({ content: '', type: 'ai', reasoning_content: '', timestamp: Date.now() });

    try {
        const response = await fetch('/api/bengo/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Accept': 'text/event-stream' },
            body: JSON.stringify({ message: userMessage.value, model: selectedModel.value })
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const reader = response.body.getReader();
        const decoder = new TextDecoder('utf-8');
        let fullContent = '';

        const processStream = async () => {
            while (true) {
                const { done, value } = await reader.read();
                if (done) {
                    await playAudio(fullContent);
                    break;
                }
                const chunk = decoder.decode(value, { stream: true });
                const lines = chunk.split('\n').filter(line => line.trim() !== '');
                for (const line of lines) {
                    let data = line.trim();
                    if (data.startsWith('data:')) data = data.replace(/^data:\s*/, '');
                    if (data === '[DONE]' || !data || data === 'data') continue;
                    try {
                        const json = JSON.parse(data);
                        const currentMessage = chatHistory.value[aiMessageIndex];
                        if ('content' in json && typeof json.content === 'string') {
                            currentMessage.content += json.content;
                            fullContent += json.content;
                        }
                        if ('reasoning_content' in json && typeof json.reasoning_content === 'string') {
                            currentMessage.reasoning_content += json.reasoning_content;
                        }
                        if (!('content' in json) && !('reasoning_content' in json)) continue;
                        chatHistory.value.splice(aiMessageIndex, 1, { ...currentMessage });
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
        chatHistory.value.splice(aiMessageIndex, 1, { ...currentMessage });
        await nextTick();
        scrollToBottom();
    } finally {
        isLoading.value = false;
        userMessage.value = "";
    }
};

const playAudio = async (text) => {
    try {
        const response = await instance.post('/bengo/text-to-speech', { text }, { responseType: 'blob' });
        const audioBlob = new Blob([response.data], { type: 'audio/mpeg' });
        const audioUrl = URL.createObjectURL(audioBlob);
        const audio = new Audio(audioUrl);
        audio.play();
    } catch (error) {
        chatHistory.value.push({
            content: '语音合成失败，请重试',
            type: 'ai',
            reasoning_content: '',
            timestamp: Date.now()
        });
        scrollToBottom();
    }
};

const scrollToBottom = async () => {
    await nextTick();
    const chatBody = chatContainer.value;
    if (chatBody) chatBody.scrollTop = chatBody.scrollHeight;
};

const clearChatHistory = () => chatHistory.value = [];

const resetConversation = async () => {
    try {
        await instance.get('/bengo/reset');
        chatHistory.value = [];
        chatHistory.value.push({ content: "对话已重置", type: 'ai', reasoning_content: '', timestamp: Date.now() });
    } catch (error) {
        console.error("Reset conversation failed:", error);
        chatHistory.value.push({
            content: "重置对话失败，请稍后重试。",
            type: 'ai',
            reasoning_content: '',
            timestamp: Date.now()
        });
    }
    scrollToBottom();
};

// 搜索相关
const searchInputRef = ref();
const showSearch = ref(false);
const searchKey = ref("");
const router = useRouter();
const searchHistory = ref([]);

const openSearch = () => showSearch.value = !showSearch.value;

const search = () => {
    showSearch.value = false;
    if (!searchKey.value || searchKey.value.length === 0) {
        router.push({ path: "/" });
        return;
    }
    router.push({ path: "/video/search/" + searchKey.value });
    apiGetUserSearchHistory().then(({ data }) => searchHistory.value = data.data);
    searchKey.value = "";
};

onMounted(() => {
    apiGetUserSearchHistory().then(({ data }) => searchHistory.value = data.data);
});
</script>

<style scoped>
.search-wrapper { display: flex; justify-content: center; align-items: center; width: 100%; }
.ml-4 { margin-left: 16px; }
.d-flex { display: flex; }
.chatbot-card { background: linear-gradient(135deg, #6a1b9a, #8e24aa); border-radius: 16px; padding: 20px; }
.chatbot-header { font-size: 20px; font-weight: bold; color: white; padding: 10px 0; }
.close-btn { margin-left: auto; }
.chatbot-body { padding: 10px; }
.chat-container { max-height: 300px; overflow-y: auto; margin-bottom: 10px; }
.chat-bubble { max-width: 85%; width: fit-content; margin: 5px 0; padding: 10px 15px; border-radius: 20px; border: 1px solid #ddd; }
.chat-bubble.user { background-color: #e0e0e0; margin-left: auto; border-bottom-right-radius: 4px; }
.chat-bubble.ai { background-color: #00bcd4; color: white; margin-right: auto; border-bottom-left-radius: 4px; }
.reasoning-chain { background: rgba(0, 0, 0, 0.1); padding: 12px; border-radius: 8px; margin-bottom: 12px; }
.reasoning-chain pre { white-space: pre-wrap; font-size: 0.9em; margin: 0; }
.response-content { margin-top: 5px; }
.response-line { display: flex; align-items: flex-start; margin: 2px 0; }
.decorator { margin-right: 5px; font-size: 0.9em; }
.timestamp { font-size: 0.8em; color: #999; margin-top: 5px; text-align: right; }
.chat-input { margin-top: 10px; border-radius: 16px; }
.send-btn, .clear-btn, .reset-btn { margin-top: 10px; width: 100%; border-radius: 16px; }
.send-btn { background-color: #8e24aa; color: white; font-weight: bold; }
.custom-menu { background-color: white !important; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }
.clear-btn { background-color: #ccc; color: black; font-weight: bold; }
.reset-btn { background-color: #ff9800; color: white; font-weight: bold; }
.mr-2 { margin-right: 8px; }
@media (max-width: 600px) {
    .chatbot-card { width: 100%; padding: 15px; }
    .chat-container { max-height: 70vh; }
    .chat-input, .send-btn, .clear-btn, .reset-btn { width: 100%; }
}
::v-deep(.v-field__outline) { --v-field-border-width: 0px; }
@media only screen and (min-width: 700px) { .d700-hide { display: none; } }
@media only screen and (max-width: 700px) { .e700-hide { display: none; } }
</style>