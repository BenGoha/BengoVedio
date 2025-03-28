package org.bengo.service.video;

import org.bengo.entity.video.Type;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuGuanRen
 */
public interface TypeService extends IService<Type> {

    /**
     * 获取分类下的标签
     * @param typeId
     * @return
     */
    List<String> getLabels(Long typeId);

    /**
     * 随机获取标签
     * @return
     */
    List<String> random10Labels();
}
