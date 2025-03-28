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
public class DetailsJson implements Serializable {
    Double score;
    String suggestion;
    String label;
    String group;
}
