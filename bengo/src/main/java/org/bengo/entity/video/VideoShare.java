package org.bengo.entity.video;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bengo.entity.BaseEntity;

/**
 * <p>
 * 
 * </p>
 *
 * @author wuGuanRen
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VideoShare extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long videoId;

    private Long userId;

    private String ip;

}
