package org.bengo;

import org.bengo.authority.AuthorityUtils;
import org.bengo.authority.BaseAuthority;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "org.bengo.mapper")
@EnableScheduling
public class BenGoApplication {

    public static void main(String[] args) {
        AuthorityUtils.setGlobalVerify(true,new BaseAuthority());
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BenGoApplication.class, args);
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
//        String path = env.getProperty("server.servlet.context-path");
        System.out.println("\n\t------------------------------------------------------\n\t" +
                "短视频个性化推荐系统启动成功！！！  欢迎使用\n\t" +
                "Local: \t\thttp://124.243.181.49:" + port + "/\n\t" +
                "------------------------------------------------------"
        );

    }


}
