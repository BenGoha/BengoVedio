package org.bengo.util;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtUtils {
    public static final long EXPIRE = 10000000L * 60 * 60 * 24; // token 过期时间
    public static final String APP_SECRET = "EgCAdBsFBjHzKnuL2oBg9Lu4OcPtXUxQVLiSP5uT"; // 密钥

    // 生成 token 方法保持不变
    public static String getJwtToken(Long id, String nickname) {
        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("guli-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .claim("id", String.valueOf(id))
                .claim("nickname", nickname)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
        return JwtToken;
    }

    // 检查 token
    public static boolean checkToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
            System.out.println("Token 为空或格式错误");
            return false;
        }
        String jwtToken = authHeader.substring(7); // 去除 "Bearer " 前缀
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
            return true;
        } catch (Exception e) {
            System.out.println("JWT 校验失败: " + e.getMessage());
            return false;
        }
    }

    // 获取用户 ID
    public static Long getUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String jwtToken = authHeader.substring(7); // 去除 "Bearer " 前缀
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
            Claims claims = claimsJws.getBody();
            return Long.valueOf(claims.get("id").toString());
        } catch (Exception e) {
            System.out.println("JWT 解析失败: " + e.getMessage());
            return null;
        }
    }
}