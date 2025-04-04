package org.bengo.controller;

import cn.xfyun.api.IatClient;
import cn.xfyun.api.TtsClient;

import cn.xfyun.model.response.iat.IatResponse;
import cn.xfyun.model.response.iat.Text;
import cn.xfyun.model.response.TtsResponse;
import cn.xfyun.service.iat.AbstractIatWebSocketListener;

import cn.xfyun.service.tts.AbstractTtsWebSocketListener;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.Response;
import okhttp3.WebSocket;
import org.bengo.config.PropertiesConfig;
import org.bengo.util.MicrophoneRecorderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.LineUnavailableException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/bengo")
public class SpeechController {

    private static final Logger logger = LoggerFactory.getLogger(SpeechController.class);
    private final PropertiesConfig config;
    private final IatClient iatClient;
    private final TtsClient ttsClient;
    private MicrophoneRecorderUtil recorder;
    private CountDownLatch latch;
    private List<Text> resultSegments;
    private volatile boolean isRecording;

    @Autowired
    public SpeechController(PropertiesConfig config) {
        this.config = config;
        this.iatClient = new IatClient.Builder()
                .signature(config.getAppId(), config.getApiKey(), config.getApiSecret())
                .dwa("wpgs")
                .build();
        TtsClient tempTtsClient = null;
        try {
            tempTtsClient = new TtsClient.Builder()
                    .signature(config.getAppId(), config.getApiKey(), config.getApiSecret())
                    .build();
        } catch (Exception e) {
            logger.error("初始化 TtsClient 失败", e);
        }
        this.ttsClient = tempTtsClient;
        this.isRecording = false;
    }

    @PostMapping("/start-recording")
    public ResponseEntity<JSONObject> startRecording() {
        if (isRecording) {
            JSONObject error = new JSONObject();
            error.put("error", "已有录音任务进行中");
            return ResponseEntity.status(400).body(error);
        }

        try {
            PipedInputStream audioInputStream = new PipedInputStream(1280);
            PipedOutputStream audioOutputStream = new PipedOutputStream(audioInputStream);
            recorder = new MicrophoneRecorderUtil();
            resultSegments = new ArrayList<>();
            latch = new CountDownLatch(1);
            isRecording = true;

            recorder.startRecording(audioOutputStream);
            logger.info("开始录音，音频流已启动");

            iatClient.send(audioInputStream, new AbstractIatWebSocketListener() {
                @Override
                public void onSuccess(WebSocket webSocket, IatResponse iatResponse) {
                    logger.info("IAT onSuccess: code={}, sid={}", iatResponse.getCode(), iatResponse.getSid());
                    if (iatResponse.getCode() != 0) {
                        logger.warn("错误码：{}, 信息：{}, sid：{}", iatResponse.getCode(), iatResponse.getMessage(), iatResponse.getSid());
                        return;
                    }
                    if (iatResponse.getData() != null && iatResponse.getData().getResult() != null) {
                        handleResultText(iatResponse.getData().getResult().getText());
                        logger.info("中间识别结果：{}", getFinalResult());
                    }
                    if (iatResponse.getData() != null && iatResponse.getData().getStatus() == 2) {
                        logger.info("识别完成，关闭连接");
                        latch.countDown();
                    }
                }

                @Override
                public void onFail(WebSocket webSocket, Throwable t, Response response) {
                    logger.error("语音听写失败: {}", t.getMessage());
                    latch.countDown();
                    isRecording = false;
                }
            });

            JSONObject response = new JSONObject();
            response.put("status", "recording");
            return ResponseEntity.ok(response);
        } catch (LineUnavailableException e) {
            logger.error("麦克风不可用", e);
            JSONObject error = new JSONObject();
            error.put("error", "麦克风不可用，请检查权限");
            return ResponseEntity.status(500).body(error);
        } catch (Exception e) {
            logger.error("录音启动失败", e);
            JSONObject error = new JSONObject();
            error.put("error", "录音启动失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/stop-recording")
    public ResponseEntity<JSONObject> stopRecording() {
        if (!isRecording) {
            JSONObject error = new JSONObject();
            error.put("error", "当前没有正在进行的录音任务");
            return ResponseEntity.status(400).body(error);
        }

        try {
            if (recorder != null) {
                recorder.stopRecording();
                logger.info("停止录音，关闭麦克风");
                iatClient.closeWebsocket();
                logger.info("关闭 IAT WebSocket 连接");
                latch.await(5, TimeUnit.SECONDS);
            }
            isRecording = false;
            JSONObject response = new JSONObject();
            String finalText = getFinalResult();
            response.put("text", finalText.isEmpty() ? "未识别到有效语音" : finalText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            isRecording = false;
            logger.error("停止录音失败", e);
            JSONObject error = new JSONObject();
            error.put("error", "停止录音失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping(value = "/text-to-speech", produces = "audio/mpeg")
    public ResponseEntity<byte[]> textToSpeech(@RequestBody JSONObject request) {
        if (ttsClient == null) {
            logger.error("TTS 服务未初始化");
            return ResponseEntity.status(500).body(null);
        }

        try {
            String text = request.getString("text");
            if (text == null || text.trim().isEmpty()) {
                logger.warn("TTS 输入文本为空");
                return ResponseEntity.badRequest().body(null);
            }

            CountDownLatch ttsLatch = new CountDownLatch(1);
            final byte[][] audioDataHolder = new byte[1][]; // 用于存储音频数据

            ttsClient.send(text, new AbstractTtsWebSocketListener() { // 使用无参构造
                @Override
                public void onSuccess(byte[] bytes) {
                    logger.info("TTS 合成完成，收到音频数据: {} 字节", bytes.length);
                    audioDataHolder[0] = bytes; // 保存音频数据
                    ttsLatch.countDown(); // 释放锁
                }

                @Override
                public void onFail(WebSocket webSocket, Throwable t, Response response) {
                    logger.error("TTS 连接失败: {}", t.getMessage());
                    ttsLatch.countDown();
                }

                @Override
                public void onBusinessFail(WebSocket webSocket, TtsResponse ttsResponse) {
                    logger.error("TTS 业务异常: code={}, message={}, sid={}",
                            ttsResponse.getCode(), ttsResponse.getMessage(), ttsResponse.getSid());
                    ttsLatch.countDown();
                }
            });

            boolean completed = ttsLatch.await(10, TimeUnit.SECONDS);
            if (!completed) {
                logger.warn("TTS 合成超时");
                return ResponseEntity.status(500).body(null);
            }

            byte[] audioData = audioDataHolder[0];
            if (audioData == null || audioData.length == 0) {
                logger.warn("语音合成返回空数据");
                return ResponseEntity.status(500).body(null);
            }

            logger.info("TTS 成功生成音频，长度: {} 字节", audioData.length);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(audioData);
        } catch (Exception e) {
            logger.error("语音合成失败", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    private void handleResultText(Text textObject) {
        if ("rpl".equals(textObject.getPgs()) && textObject.getRg() != null && textObject.getRg().length == 2) {
            int start = textObject.getRg()[0] - 1;
            int end = textObject.getRg()[1] - 1;
            for (int i = start; i <= end && i < resultSegments.size(); i++) {
                resultSegments.get(i).setDeleted(true);
            }
        }
        resultSegments.add(textObject);
    }

    private String getFinalResult() {
        StringBuilder finalResult = new StringBuilder();
        for (Text text : resultSegments) {
            if (text != null && !text.isDeleted()) {
                finalResult.append(text.getText());
            }
        }
        return finalResult.toString();
    }
}