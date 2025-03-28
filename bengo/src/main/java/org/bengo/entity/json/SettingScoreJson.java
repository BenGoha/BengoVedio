package org.bengo.entity.json;

import lombok.Data;

/**
 * @description: 系统表解析
 * @author wuGuanRen
 */
@Data
public class SettingScoreJson {
    // 通过
    ScoreJson successScore;
    // 人工审核
    ScoreJson manualScore;
    // PASS
    ScoreJson passScore;
}
