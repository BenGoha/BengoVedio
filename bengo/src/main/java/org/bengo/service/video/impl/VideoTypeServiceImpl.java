package org.bengo.service.video.impl;

import org.bengo.entity.video.VideoType;
import org.bengo.mapper.video.VideoTypeMapper;
import org.bengo.service.video.VideoTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuGuanRen
 */
@Service
public class VideoTypeServiceImpl extends ServiceImpl<VideoTypeMapper, VideoType> implements VideoTypeService {

}
