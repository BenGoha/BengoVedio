package org.bengo.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
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
public class RolePermission implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    private Integer permissionId;

    private Integer roleId;


}
