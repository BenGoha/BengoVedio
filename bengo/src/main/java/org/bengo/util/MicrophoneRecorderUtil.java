package org.bengo.util;

import javax.sound.sampled.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.PipedOutputStream;

public class MicrophoneRecorderUtil {
    private static final Logger logger = LoggerFactory.getLogger(MicrophoneRecorderUtil.class);
    private static final int SAMPLE_RATE = 16000;
    private static final int SAMPLE_SIZE_BITS = 16;
    private static final int CHANNELS = 1;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;

    private volatile TargetDataLine targetDataLine;
    private volatile boolean recording;
    private volatile PipedOutputStream outputStream;

    public synchronized void startRecording(PipedOutputStream outputStream) throws LineUnavailableException {
        if (outputStream == null) throw new IllegalArgumentException("输出流不能为空");
        if (recording || this.outputStream != null) throw new IllegalStateException("已有录音任务在执行中");
        this.outputStream = outputStream;

        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(format);
            targetDataLine.start();
            logger.info("麦克风已打开，采样率: {}", SAMPLE_RATE);
        } catch (LineUnavailableException e) {
            cleanupResources();
            throw e;
        }
        Thread captureThread = new Thread(this::captureAudio);
        captureThread.setUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("录音线程异常", throwable);
            cleanupResources();
        });
        captureThread.start();
    }

    private void captureAudio() {
        recording = true;
        byte[] buffer = new byte[1280];
        while (recording) {
            int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
            logger.debug("读取音频数据: {} 字节", bytesRead);
            if (bytesRead > 0 && outputStream != null) {
                try {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                } catch (IOException e) {
                    logger.error("输出流写入异常", e);
                    break;
                }
            }
        }
        cleanupResources();
    }

    public synchronized void stopRecording() {
        recording = false;
        logger.info("停止录音");
    }

    private synchronized void cleanupResources() {
        try {
            if (targetDataLine != null && targetDataLine.isOpen()) {
                targetDataLine.stop();
                targetDataLine.close();
                logger.info("麦克风已关闭");
            }
            if (outputStream != null) outputStream.close();
        } catch (Exception e) {
            logger.error("资源清理异常", e);
        } finally {
            targetDataLine = null;
            outputStream = null;
        }
    }
}