package org.bengo.service.AIChat;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class OpenAIService {

    private final String apiKey = "sk-cb1f91d7c6b44d94b06dc486cf070225";  // DeepSeek API Key
    private final String apiUrl = "https://api.deepseek.com/chat/completions";  // DeepSeek API URL

    // 存储每个用户的历史对话
    private Map<String, List<Message>> userConversations = new HashMap<>();

    // 获取聊天机器人的回复
    public String getChatbotResponse(String userMessage, String sessionId) {
        try {
            // 如果用户没有会话历史，则创建一个新的历史消息列表
            if (!userConversations.containsKey(sessionId)) {
                userConversations.put(sessionId, new ArrayList<>());
            }

            // 获取该用户的历史消息
            List<Message> messages = userConversations.get(sessionId);

            // 将用户消息加入历史消息
            messages.add(new Message("user", userMessage));

            // 构建请求体
            String requestBody = buildRequestBody(messages);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 发送 POST 请求到 DeepSeek API
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            // 解析返回的JSON数据，获取AI的回复
            String responseBody = response.getBody();
            JSONObject jsonResponse = new JSONObject(responseBody);
            String aiReply = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("delta")
                    .getString("content");
            if (responseBody != null) {
                // 提取思维链内容和最终回答内容
                int startReasoning = responseBody.indexOf("\"reasoning_content\":\"") + 21;
                int endReasoning = responseBody.indexOf("\"}", startReasoning);
                String reasoningContent = responseBody.substring(startReasoning, endReasoning);

                int startContent = responseBody.indexOf("\"content\":\"") + 11;
                int endContent = responseBody.indexOf("\"}", startContent);
                 aiReply = responseBody.substring(startContent, endContent);

                // 合并思维链内容和最终答案
                String fullReply = reasoningContent + "\n\n" + aiReply;

                // 将AI的回复加入历史消息
                messages.add(new Message("assistant", fullReply));

                return fullReply;
            } else {
                return "抱歉，我没有得到有效的回应。";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，出现了错误，请稍后再试。";
        }
    }

    // 构建请求体
    private String buildRequestBody(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"model\": \"deepseek-reasoner\", \"messages\": [");

        for (Message message : messages) {
            sb.append("{")
                    .append("\"role\": \"").append(message.getRole()).append("\", ")
                    .append("\"content\": \"").append(message.getContent()).append("\"}, ");
        }

        // 去掉最后的逗号
        sb.delete(sb.length() - 2, sb.length());

        sb.append("]}");

        return sb.toString();
    }

    // 定义消息对象
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
