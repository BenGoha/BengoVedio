package org.bengo.entity.json;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author wuGuanRen
 */
@Data
@ToString
public class ResultJson implements Serializable {
    Integer code;
    String message;
    ResultChildJson result;
}


