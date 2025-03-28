package org.bengo.entity.vo;

import lombok.Data;
import org.bengo.config.QiNiuConfig;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 修改用户信息
 * @author wuGuanRen
 */
@Data
public class UpdateUserVO {

    @NotBlank(message = "昵称不可为空")
    private String nickName;

    private Long userid;

    private Long avatar;

    private Boolean sex;

    private String description;

    private Long defaultFavoritesId;



}
