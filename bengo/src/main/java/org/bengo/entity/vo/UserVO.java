package org.bengo.entity.vo;

import lombok.Data;
import org.bengo.entity.user.User;

/**
 * @description:
 * @author wuGuanRen
 */
@Data
public class UserVO{

    private Long id;

    private String nickName;

    private Long avatar;

    private Boolean sex;

    private String description;

    private Long follow;

    private Long fans;
}
