package org.bengo.entity.vo;

import lombok.Data;
import org.bengo.entity.user.Follow;

/**
 * @description:
 * @author wuGuanRen
 */
@Data
public class FollowVO extends Follow {

    private String nickName;
}
