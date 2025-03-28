package org.bengo.exception;

import lombok.Data;

/**
 * @description:
 * @author wuGuanRen
 */
@Data
public class BaseException extends RuntimeException {

    String msg;

    public BaseException(String msg){
        this.msg = msg;
    }

}
