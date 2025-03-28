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
public class ResultChildJson implements Serializable {
    String suggestion;
    ScenesJson scenes;
}
