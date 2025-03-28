package org.bengo.service.user.impl;

import org.bengo.entity.user.UserRole;
import org.bengo.mapper.user.UserRoleMapper;
import org.bengo.service.user.UserRoleService;
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
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
