package com.kehao.auto_connect_wifi.task;

import com.kehao.auto_connect_wifi.pojo.WifiHotspotEntity;
import com.kehao.auto_connect_wifi.tool.WifiTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AutoConnectWifiTask {

    @Autowired
    private WifiTool wifiTool;

    private final long delayTime = 1000 * 60 * 5;

    /**
     * 自动连接WiFi任务
     */
    @Scheduled(fixedDelay = delayTime)
    public void doAutoConnectWifiTask() {
        if (wifiTool.networkConnected()) {
            //网络连接正常 不操作
            return;
        }
        log.info("当前网络异常,执行连接wifi命令！");
        List<WifiHotspotEntity> wifiHotspotEntityList = wifiTool.getWifiNearBy().stream()
                .filter(wifiHotspotEntity -> Strings.isNotBlank(wifiHotspotEntity.getName()))
                .filter(wifiHotspotEntity -> wifiHotspotEntity.getSignal() >= 50)
                .sorted(Comparator.comparingInt(WifiHotspotEntity::getSignal))
                .collect(Collectors.toList());
        List<String> profiles = wifiTool.getProfiles();
        log.info("获取到{}个WiFi信号，过滤没有记录过密码的WiFi",wifiHotspotEntityList.size());
        List<WifiHotspotEntity> wifiHotspotCanConnectEntityList = wifiHotspotEntityList.stream()
                .filter(wifiHotspotEntity -> profiles.contains(wifiHotspotEntity.getName())).collect(Collectors.toList());
        if(wifiHotspotCanConnectEntityList.isEmpty()){
            log.info("没有可以连接的WiFi");
            return;
        }
        log.info("共有{}个wifi可以被连接,开始执行连接",wifiHotspotCanConnectEntityList.size());

        for (WifiHotspotEntity wifiHotspotEntity : wifiHotspotCanConnectEntityList) {
            String wifiName = wifiHotspotEntity.getName();
            log.info("开始连接wifi：{}",wifiName);
            if (wifiTool.connectToHotspot(wifiName)) {
                //网络连接成功
                if(wifiTool.networkConnected()){
                    log.info("wifi连接成功：{}",wifiName);
                    return;
                }else {
                    log.info("wifi连接成功：{},但是没有网络",wifiName);
                }
            }else {
                log.info("连接{}失败",wifiName);
            }
        }

        log.info("已尝试所有wifi,但均未成功联网");
    }
}
