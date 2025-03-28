package org.bengo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author wuGuanRen
 */
@Data
@AllArgsConstructor
public class AuthorityException extends Exception{

    private int code;

    private String msg;

    public AuthorityException(String msg){
        super(msg);
    }
}
