package org.bengo.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .headers()
//                .frameOptions()
//                .sameOrigin() // 开发环境下允许同源 iframe
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/**", "/bengo/**", "/authorize/**","/page/login.html","/page/**",).permitAll() // 放开所有代理路径
//                .anyRequest().authenticated()
//                .and()
//                .csrf().disable(); // 禁用 CSRF，便于开发
//    }
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .authorizeRequests()
                .anyRequest().permitAll() // 临时放开所有请求
                .and()
                .csrf().disable();
    }


}