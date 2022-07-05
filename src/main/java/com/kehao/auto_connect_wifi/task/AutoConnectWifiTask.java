package com.kehao.auto_connect_wifi.task;

import com.kehao.auto_connect_wifi.pojo.WifiHotspotEntity;
import com.kehao.auto_connect_wifi.tool.WifiTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        List<WifiHotspotEntity> wifiHotspotEntityList = wifiTool.getWifiNearBy().stream()
                .filter(wifiHotspotEntity -> Strings.isNotBlank(wifiHotspotEntity.getName()))
                .filter(wifiHotspotEntity -> wifiHotspotEntity.getSignal() >= 50).collect(Collectors.toList());
        List<String> profiles = wifiTool.getProfiles();
        for (WifiHotspotEntity wifiHotspotEntity : wifiHotspotEntityList) {
            String wifiName = wifiHotspotEntity.getName();
            log.info("尝试连接wifi：{}",wifiName);
            if (profiles.contains(wifiName)) {
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
        }
        log.info("已尝试所有wifi,但均未成功联网");
    }
}
