package org.bengo.entity.response;

import lombok.Data;
import lombok.ToString;
import org.bengo.entity.task.VideoTask;
import org.bengo.entity.video.Video;

/**
 * @description: 封装视频审核返回结果
 * @author wuGuanRen
 */
@Data
@ToString
public class VideoAuditResponse {

    private AuditResponse videoAuditResponse;

    private AuditResponse imageAuditResponse;

    private AuditResponse textAuditResponse;

    private VideoTask videoTask;
}
