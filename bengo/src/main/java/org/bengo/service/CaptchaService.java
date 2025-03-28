package org.bengo.service;

import org.bengo.entity.Captcha;
import com.baomidou.mybatisplus.extension.service.IService;

import java.awt.image.BufferedImage;

/**
 * <p>
 * 系统验证码 服务类
 * </p>
 */
public interface CaptchaService extends IService<Captcha> {


    BufferedImage getCaptcha(String uuId);

    boolean validate(Captcha captcha) throws Exception;
}
