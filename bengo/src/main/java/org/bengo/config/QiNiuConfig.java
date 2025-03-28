package org.bengo.config;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.Data;
import org.bengo.holder.UserHolder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author wuGuanRen
 **/
@Data
@Component
@ConfigurationProperties(prefix = "qiniu.kodo")
public class QiNiuConfig {
    /**
     * 账号
     */
    private String accessKey;
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * bucketName
     */
    private String bucketName;

//    public static final String CNAME = "http://skq8lw41t.hn-bkt.clouddn.com";
    public static final String CNAME = "http://stilmo8ro.hn-bkt.clouddn.com";
    public static final String VIDEO_URL = "http://ai.qiniuapi.com/v3/video/censor";
    public static final String IMAGE_URL = "http://ai.qiniuapi.com/v3/image/censor";

    public static final String fops = "avthumb/mp4";

    public Auth buildAuth() {
        String accessKey = this.getAccessKey();
        String secretKey = this.getSecretKey();
        return Auth.create(accessKey, secretKey);
    }


    public String uploadToken(String type){
        // 创建一个Auth对象
        final Auth auth = buildAuth();
        // 返回一个上传凭证，该凭证用于上传文件到指定的存储桶中，有效期为300秒，并且限制上传的文件类型为视频和图片
        return auth.uploadToken(bucketName, null, 300, new
                StringMap().put("mimeLimit", "video/*;image/*"));
    }

    public String videoUploadToken() {
        // 构建认证对象
        final Auth auth = buildAuth();
        // 返回上传凭证
        return auth.uploadToken(bucketName, null, 300, new
                StringMap().put("mimeLimit", "video/*").putNotEmpty("persistentOps", fops));
    }

    public String imageUploadToken() {
        final Auth auth = buildAuth();
        return auth.uploadToken(bucketName, null, 300, new
                StringMap().put("mimeLimit", "image/*"));
    }

    public String getToken(String url, String method, String body, String contentType) {

        final Auth auth = buildAuth();
        String qiniuToken = "Qiniu " + auth.signQiniuAuthorization(url, method, body == null ? null : body.getBytes(), contentType);
        return qiniuToken;
    }


}