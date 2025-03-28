package org.bengo.entity.vo;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @description:
 * @author wuGuanRen
 */
@Data
public class Model {
    private String label;
    private Long videoId;
    /**
     * 暴漏的接口只有根据停留时长 {@link org.bengo.controller.CustomerController#updateUserModel}
     */

    private Double score;
}
