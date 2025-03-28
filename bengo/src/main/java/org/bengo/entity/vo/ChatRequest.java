package org.bengo.entity.vo;

public class ChatRequest {
    private String sessionId; // 会话ID（用于上下文关联）
    private String message;   // 用户输入内容
    private Long videoId;     // 关联的视频ID（可选）
}