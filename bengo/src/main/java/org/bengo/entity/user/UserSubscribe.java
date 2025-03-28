package org.bengo.entity.user;

import lombok.Data;

/**
 * @description: 用户订阅表
 * @author wuGuanRen
 */
@Data
public class UserSubscribe {
    private Long id;
    private Long typeId;
    private Long userId;
}
