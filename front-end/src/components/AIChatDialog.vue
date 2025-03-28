<template>
    <v-dialog v-model="showDialog" max-width="600">
        <v-card>
            <v-card-title class="d-flex justify-space-between">
                <span>AI助手</span>
                <v-btn icon @click="showDialog = false">
                    <v-icon>mdi-close</v-icon>
                </v-btn>
            </v-card-title>

            <v-card-text>
                <!-- 聊天消息展示区 -->
                <div v-for="(msg, i) in messages" :key="i">
                    <div :class="`message-bubble ${msg.role}`">
                        {{ msg.content  }}
                    </div>
                </div>

                <!-- 输入框 -->
                <v-text-field
                        v-model="inputMsg"
                        placeholder="输入问题..."
                        @keyup.enter="sendMessage"
                        :loading="isLoading">
                </v-text-field>
            </v-card-text>
        </v-card>
    </v-dialog>

<!--    <div>-->
<!--        <v-app-bar @show-ai-chat="showAIChat = true"/>-->
<!--        <AIChatDialog v-model="showAIChat"/>-->
<!--    </div>-->
    <div >
                <Header @show-ai-chat="handleAIChatOpen"/>
    </div>
    <!--                <AIChatDialog v-model="showDialog"/>-->
</template>

<script setup>
import Header from "../views/layout/header.vue";
import { ref, defineEmits } from 'vue'
import { chatWithAI } from '@/apis/AIChat.js'


const showDialog = ref(false)
const inputMsg = ref('')
const messages = ref([])
const isLoading = ref(false)
console.log(Header)
const handleAIChatOpen =(isShow) => {
    console.log(' 接收到子组件事件:', isShow)
    // 显示AI聊天窗口的逻辑
    showDialog.value  = true
}
// function handleAIChatOpen(isShow){
//     console.log(' 接收到子组件事件:', isShow)
//     console.log(' 接收到子组件事件:')
// }
const sendMessage = async () => {
    try {
        isLoading.value  = true
        const response = await chatWithAI(inputMsg.value)
        messages.value.push(
            { role: 'user', content: inputMsg.value  },
            { role: 'ai', content: response.data  }
        )
        inputMsg.value  = ''
    } finally {
        isLoading.value  = false
    }
}
</script>