package org.bengo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bengo.constant.RedisConstant;
import org.bengo.entity.Captcha;
import org.bengo.entity.user.User;
import org.bengo.entity.vo.FindPWVO;
import org.bengo.entity.vo.RegisterVO;
import org.bengo.service.CaptchaService;
import org.bengo.service.EmailService;
import org.bengo.service.LoginService;
import org.bengo.service.user.UserService;
import org.bengo.util.JwtUtils;
import org.bengo.util.R;
import org.bengo.util.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;



@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping
    public R login(@RequestBody @Validated User user){
        user = loginService.login(user);
        // 登录成功，生成token
        String token = JwtUtils.getJwtToken(user.getId(), user.getNickName());
        final HashMap<Object, Object> map = new HashMap<>();
        map.put("token",token);
        map.put("name",user.getNickName());
        map.put("user",user);
        return R.ok().data(map);
    }

    /**
     * 获取图形验证码
     * @param response
     * @param uuId
     * @throws IOException
     */
    @GetMapping("/captcha.jpg/{uuId}")
    public void captcha(HttpServletResponse response, @PathVariable String uuId) throws IOException {
        loginService.captcha(uuId,response);
    }


    /**
     * 获取验证码
     * @param captcha
     * @return
     * @throws Exception
     */
    @PostMapping("/getCode")
    public R getCode(@RequestBody @Validated Captcha captcha) throws Exception {
        if (!loginService.getCode(captcha)) {
            return R.error().message("验证码错误");
        }
        return R.ok().message("发送成功,请耐心等待");
    }


    /**
     * 检测邮箱验证码
     * @param email
     * @param code
     * @return
     */
    @PostMapping("/check")
    public R check(String email,Integer code){
        loginService.checkCode(email,code);
        return R.ok().message("验证成功");
    }

    /**
     * 注册
     * @param registerVO
     * @return
     * @throws Exception
     */
    @PostMapping("/register")
    public R register(@RequestBody @Validated RegisterVO registerVO) throws Exception {
        if (!loginService.register(registerVO)) {
            return R.error().message("注册失败,验证码错误");
        }
        return R.ok().message("注册成功");
    }

    /**
     * 找回密码
     * @param findPWVO
     * @return
     */
    @PostMapping("/findPassword")
    public R findPassword(@RequestBody @Validated FindPWVO findPWVO,HttpServletResponse response){
        final Boolean b = loginService.findPassword(findPWVO);
        return R.ok().message(b ? "修改成功" : "修改失败,验证码不正确");
    }

}
