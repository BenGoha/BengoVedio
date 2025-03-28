package org.bengo.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bengo.entity.user.UserRole;
import org.bengo.entity.user.UserSubscribe;
import org.bengo.mapper.user.UserRoleMapper;
import org.bengo.mapper.user.UserSubscribeMapper;
import org.bengo.service.user.UserRoleService;
import org.bengo.service.user.UserSubscribeService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author wuGuanRen
 */
@Service
public class UserSubscribeServiceImpl  extends ServiceImpl<UserSubscribeMapper, UserSubscribe> implements UserSubscribeService {
}
