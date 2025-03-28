package org.bengo.service.user;

import org.bengo.entity.user.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import org.bengo.entity.user.Tree;
import org.bengo.entity.vo.AssignRoleVO;
import org.bengo.entity.vo.AuthorityVO;
import org.bengo.util.R;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuGuanRen
 */
public interface RoleService extends IService<Role> {

    List<Tree> tree();

    R removeRole(String id);

    R gavePermission(AuthorityVO authorityVO);

    R gaveRole(AssignRoleVO assignRoleVO);

}
