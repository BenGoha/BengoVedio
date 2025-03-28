//package org.bengo.controller;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Deep Seek 智能聊天机器人接入
// */
//@RestController
//@RequestMapping("/bengo")
//public class AIController {
//    @Value("${deepseek.api.key}")
//    private String API_KEY; // 替换为您的 DeepSeek API 密钥
//    private static final String BASE_URL = "https://api.deepseek.com";
//
//    // 使用默认配置的 HttpClient，未设置超时
//    private final HttpClient client = HttpClients.createDefault();
//
//    // 存储对话历史，支持多轮对话
//    private final List<JSONObject> conversationHistory = new ArrayList<>();
//
//    @PostMapping("/chat")
//    public JSONObject chat(@RequestBody JSONObject request) {
//        try {
//            String userMessage = request.getString("message");
//            String model = request.getString("model"); // 获取前端传递的模型选择
//
//            // 输入校验
//            if (userMessage == null || userMessage.trim().isEmpty()) {
//                JSONObject errorResponse = new JSONObject();
//                errorResponse.put("reasoning_content", "");
//                errorResponse.put("content", "输入的消息不能为空");
//                return errorResponse;
//            }
//
//            // 默认模型为 deepseek-reasoner
//            if (model == null || (!model.equals("deepseek-reasoner") && !model.equals("deepseek-chat"))) {
//                model = "deepseek-reasoner";
//            }
//
//            // 添加用户消息到对话历史
//            JSONObject userMsg = new JSONObject();
//            userMsg.put("role", "user");
//            userMsg.put("content", userMessage);
//            conversationHistory.add(userMsg);
//
//            // 构建对话历史
//            JSONArray messages = new JSONArray();
//            messages.addAll(conversationHistory);
//
//            // 调用 DeepSeek API
//            JSONObject assistantResponse = sendMessage(messages, model);
//
//            // 提取响应中的 reasoning_content 和 content
//            String reasoningContent = assistantResponse.getString("reasoning_content");
//            String content = assistantResponse.getString("content");
//
//            // 如果是 deepseek-chat 模型，reasoning_content 为空
//            if (reasoningContent == null) {
//                reasoningContent = "";
//            }
//
//            // 添加助手回复到对话历史（仅 content）
//            JSONObject assistantMsg = new JSONObject();
//            assistantMsg.put("role", "assistant");
//            assistantMsg.put("content", content);
//            conversationHistory.add(assistantMsg);
//
//            // 返回结果给前端
//            JSONObject response = new JSONObject();
//            response.put("reasoning_content", reasoningContent);
//            response.put("content", content);
//            return response;
//
//        } catch (Exception e) {
//            JSONObject errorResponse = new JSONObject();
//            errorResponse.put("reasoning_content", "");
//            errorResponse.put("content", "请求失败，请稍后重试：" + e.getMessage());
//            return errorResponse;
//        }
//    }
//
//    private JSONObject sendMessage(JSONArray messages, String model) throws IOException {
//        String url = BASE_URL + "/chat/completions";
//
//        // 构建请求数据
//        JSONObject payload = new JSONObject();
//        payload.put("model", model);
//        payload.put("messages", messages);
//        payload.put("stream", true);
//
//        // 创建 HTTP 请求
//        HttpPost postMethod = new HttpPost(url);
//        postMethod.setHeader("Content-Type", "application/json");
//        postMethod.setHeader("Authorization", "Bearer " + API_KEY);
//        postMethod.setEntity(new StringEntity(payload.toJSONString(), "UTF-8"));
//
//        // 执行请求
//        HttpResponse response = client.execute(postMethod);
//        int statusCode = response.getStatusLine().getStatusCode();
//        if (statusCode != 200) {
//            throw new IOException("DeepSeek API 请求失败，状态码: " + statusCode);
//        }
//
//        // 解析响应
//        String responseString = EntityUtils.toString(response.getEntity());
//        JSONObject responseJson = JSONObject.parseObject(responseString);
//        return responseJson.getJSONArray("choices")
//                .getJSONObject(0)
//                .getJSONObject("message");
//    }
//
//    // 清空对话历史
//    @GetMapping("/reset")
//    public JSONObject resetConversation() {
//        conversationHistory.clear();
//        JSONObject response = new JSONObject();
//        response.put("reasoning_content", "");
//        response.put("content", "对话历史已清空");
//        return response;
//    }
//}



//
//package org.bengo.controller;
//
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Deep Seek 智能聊天机器人接入
// */
//@RestController
//@RequestMapping("/bengo")
//public class AIController {
//    @Value("${deepseek.api.key}")
//    private String API_KEY; // 替换为您的 DeepSeek API 密钥
//    private static final String BASE_URL = "https://api.deepseek.com";
//
//    private final HttpClient client = HttpClients.createDefault();
//    private final List<JSONObject> conversationHistory = new ArrayList<>();
//
//    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter chat(@RequestBody JSONObject request) {
//        SseEmitter emitter = new SseEmitter(0L);
//
//        try {
//            String userMessage = request.getString("message");
//            String model = request.getString("model");
//
//            if (userMessage == null || userMessage.trim().isEmpty()) {
//                emitter.send(SseEmitter.event().data(new JSONObject()
//                        .fluentPut("reasoning_content", "")
//                        .fluentPut("content", "输入的消息不能为空")
//                        .toJSONString()));
//                emitter.complete();
//                return emitter;
//            }
//
//            final String effectiveModel = (model == null || (!model.equals("deepseek-reasoner") && !model.equals("deepseek-chat")))
//                    ? "deepseek-reasoner"
//                    : model;
//
//            JSONObject userMsg = new JSONObject();
//            userMsg.put("role", "user");
//            userMsg.put("content", userMessage);
//            conversationHistory.add(userMsg);
//
//            JSONArray messages = new JSONArray();
//            messages.addAll(conversationHistory);
//
//            new Thread(() -> {
//                try {
//                    streamMessage(messages, effectiveModel, emitter);
//                    JSONObject assistantMsg = new JSONObject();
//                    assistantMsg.put("role", "assistant");
//                    assistantMsg.put("content", accumulatedContent);
//                    conversationHistory.add(assistantMsg);
//                    emitter.complete();
//                } catch (Exception e) {
//                    try {
//                        emitter.send(SseEmitter.event().data(new JSONObject()
//                                .fluentPut("reasoning_content", "")
//                                .fluentPut("content", "请求失败，请稍后重试：" + e.getMessage())
//                                .toJSONString()));
//                        emitter.completeWithError(e);
//                    } catch (IOException ex) {
//                        emitter.completeWithError(ex);
//                    }
//                }
//            }).start();
//
//        } catch (Exception e) {
//            try {
//                emitter.send(SseEmitter.event().data(new JSONObject()
//                        .fluentPut("reasoning_content", "")
//                        .fluentPut("content", "请求失败，请稍后重试：" + e.getMessage())
//                        .toJSONString()));
//                emitter.complete();
//            } catch (IOException ex) {
//                emitter.completeWithError(ex);
//            }
//        }
//        return emitter;
//    }
//
//    private String accumulatedContent = ""; // 用于存储完整的助手回复
//
//    private void streamMessage(JSONArray messages, String model, SseEmitter emitter) throws IOException {
//        String url = BASE_URL + "/chat/completions";
//
//        JSONObject payload = new JSONObject();
//        payload.put("model", model);
//        payload.put("messages", messages);
//        payload.put("stream", true);
//
//        HttpPost postMethod = new HttpPost(url);
//        postMethod.setHeader("Content-Type", "application/json");
//        postMethod.setHeader("Authorization", "Bearer " + API_KEY);
//        postMethod.setEntity(new StringEntity(payload.toJSONString(), "UTF-8"));
//
//        HttpResponse response = client.execute(postMethod);
//        int statusCode = response.getStatusLine().getStatusCode();
//        if (statusCode != 200) {
//            throw new IOException("DeepSeek API 请求失败，状态码: " + statusCode);
//        }
//
//        // 流式读取响应
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"))) {
//            String line;
//            accumulatedContent = ""; // 重置积累内容
//            while ((line = reader.readLine()) != null) {
//                if (line.startsWith("data: ")) {
//                    String data = line.substring(6);
//                    if ("[DONE]".equals(data)) {
//                        break; // 流结束
//                    }
//                    JSONObject json = JSONObject.parseObject(data);
//                    JSONArray choices = json.getJSONArray("choices");
//                    if (choices != null && !choices.isEmpty()) {
//                        JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
//                        String content = delta.getString("content");
//                        if (content != null && !content.isEmpty()) {
//                            accumulatedContent += content; // 积累完整内容
//                            JSONObject chunk = new JSONObject();
//                            chunk.put("reasoning_content", ""); // deepseek-reasoner 的 reasoning_content 在流中不可用，设为空
//                            chunk.put("content", content);
//                            emitter.send(SseEmitter.event().data(chunk.toJSONString()));
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @GetMapping("/reset")
//    public JSONObject resetConversation() {
//        conversationHistory.clear();
//        JSONObject response = new JSONObject();
//        response.put("reasoning_content", "");
//        response.put("content", "对话历史已清空");
//        return response;
//    }
//}

package org.bengo.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bengo")
public class AIController {
    @Value("${deepseek.api.key}")
    private String API_KEY;
    private static final String BASE_URL = "https://api.deepseek.com";

    private final HttpClient client = HttpClients.createDefault();
    private final List<JSONObject> conversationHistory = new ArrayList<>();

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody JSONObject request) {
        SseEmitter emitter = new SseEmitter(0L); // 0L 表示无超时

        try {
            String userMessage = request.getString("message");
            String model = request.getString("model");

            if (userMessage == null || userMessage.trim().isEmpty()) {
                emitter.send(SseEmitter.event().data(new JSONObject()
                        .fluentPut("reasoning_content", "")
                        .fluentPut("content", "输入的消息不能为空")
                        .toJSONString()));
                emitter.complete();
                return emitter;
            }

            final String effectiveModel = (model == null || (!model.equals("deepseek-reasoner") && !model.equals("deepseek-chat")))
                    ? "deepseek-reasoner"
                    : model;

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            conversationHistory.add(userMsg);

            JSONArray messages = new JSONArray();
            messages.addAll(conversationHistory);

            new Thread(() -> {
                try {
                    streamMessage(messages, effectiveModel, emitter);
                    JSONObject assistantMsg = new JSONObject();
                    assistantMsg.put("role", "assistant");
                    assistantMsg.put("content", accumulatedContent);
                    conversationHistory.add(assistantMsg);
                    emitter.send(SseEmitter.event().data("[DONE]"));
                    emitter.complete();
                } catch (Exception e) {
                    try {
                        emitter.send(SseEmitter.event().data(new JSONObject()
                                .fluentPut("reasoning_content", "")
                                .fluentPut("content", "请求失败，请稍后重试：" + e.getMessage())
                                .toJSONString()));
                        emitter.completeWithError(e);
                    } catch (IOException ex) {
                        emitter.completeWithError(ex);
                    }
                }
            }).start();

        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event().data(new JSONObject()
                        .fluentPut("reasoning_content", "")
                        .fluentPut("content", "请求失败，请稍后重试：" + e.getMessage())
                        .toJSONString()));
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
        return emitter;
    }

    private String accumulatedContent = "";

    private void streamMessage(JSONArray messages, String model, SseEmitter emitter) throws IOException {
        String url = BASE_URL + "/chat/completions";

        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("messages", messages);
        payload.put("stream", true);

        HttpPost postMethod = new HttpPost(url);
        postMethod.setHeader("Content-Type", "application/json");
        postMethod.setHeader("Authorization", "Bearer " + API_KEY);
        postMethod.setEntity(new StringEntity(payload.toJSONString(), "UTF-8"));

        HttpResponse response = client.execute(postMethod);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IOException("DeepSeek API 请求失败，状态码: " + statusCode);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"))) {
            String line;
            accumulatedContent = "";
            while ((line = reader.readLine()) != null) {
//                System.out.println("Raw line from DeepSeek: " + line); // 调试原始数据
                if (line.startsWith("data: ")) {
                    String data = line.substring(6).trim();
                    if ("[DONE]".equals(data)) {
//                        System.out.println("Received [DONE] from DeepSeek API");
                        emitter.send(SseEmitter.event().data("[DONE]"));
                        break;
                    }
                    if (data.isEmpty()) {
//                        System.out.println("Skipping empty data line");
                        continue; // 跳过空行
                    }
                    try {
                        JSONObject json = JSONObject.parseObject(data);
                        JSONArray choices = json.getJSONArray("choices");
                        if (choices != null && !choices.isEmpty()) {
                            JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
//                            System.out.println("Delta: " + delta.toJSONString());

                            String content = delta.getString("content");
                            String reasoningContent = delta.getString("reasoning_content");

                            if ((content != null && !content.isEmpty()) || (reasoningContent != null && !reasoningContent.isEmpty())) {
                                JSONObject chunk = new JSONObject();
                                chunk.put("reasoning_content", reasoningContent != null ? reasoningContent : "");
                                chunk.put("content", content != null ? content : "");
                                if (content != null) {
                                    accumulatedContent += content;
                                }
//                                System.out.println("Sending chunk: " + chunk.toJSONString());
                                emitter.send(SseEmitter.event().data(chunk.toJSONString()));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to parse line: " + data + ", Error: " + e.getMessage());
                    }
                }
            }
        }
    }

    @GetMapping("/reset")
    public JSONObject resetConversation() {
        conversationHistory.clear();
        JSONObject response = new JSONObject();
        response.put("reasoning_content", "");
        response.put("content", "对话历史已清空");
        return response;
    }
}