package com.vision;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 视光管理系统主启动类
 */
@SpringBootApplication
@MapperScan("com.vision.mapper")
@EnableTransactionManagement
@EnableScheduling
public class VisionApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisionApplication.class, args);
        System.out.println("========================================");
        System.out.println("  视光管理系统启动成功！");
        System.out.println("  访问地址: http://localhost:8080/vision");
        System.out.println("========================================");
    }
}