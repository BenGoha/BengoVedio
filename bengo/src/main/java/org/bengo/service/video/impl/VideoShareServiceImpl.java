package org.bengo.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.bengo.entity.video.VideoShare;
import org.bengo.mapper.video.VideoShareMapper;
import org.bengo.service.video.VideoShareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>

 */
@Service
public class VideoShareServiceImpl extends ServiceImpl<VideoShareMapper, VideoShare> implements VideoShareService {

    @Override
    public boolean share(VideoShare videoShare) {

        try {
            // 利用videoId和ip做为唯一索引,少一次查询
            this.save(videoShare);
        }catch (Exception e){
            // 不用删除
            return false;
        }
        return true;
    }

    @Override
    public List<Long> getShareUserId(Long videoId) {
        return this.list(new LambdaQueryWrapper<VideoShare>().eq(VideoShare::getVideoId,videoId)).stream().map(VideoShare::getUserId).collect(Collectors.toList());
    }


}
