package com.kehao.auto_connect_wifi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AutoConnectWifiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoConnectWifiApplication.class, args);
    }

}
