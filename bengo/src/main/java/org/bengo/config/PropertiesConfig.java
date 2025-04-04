package org.bengo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {
    @Value("${xfyun.appid}")
    private String appId;
    @Value("${xfyun.apiKey}")
    private String apiKey;
    @Value("${xfyun.apiSecret}")
    private String apiSecret;

    public String getAppId() { return appId; }
    public String getApiKey() { return apiKey; }
    public String getApiSecret() { return apiSecret; }
}