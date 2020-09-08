package com.aero.ops;

import com.aero.common.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.UnknownHostException;

/**
 * @author 罗涛
 * @title BchdOpsApplication
 * @date 2020/7/3 16:36
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {"com.aero.ops.entity.dao"})
public class AeroOpsApplication {
    public static void main(String[] args) {
        String localIp = SystemUtil.getRealIP();
        System.setProperty("local-ip", "192.168.0.32");
        SpringApplication.run(AeroOpsApplication.class);
    }
}
