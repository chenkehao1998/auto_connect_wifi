package com.kehao.auto_connect_wifi.tool;

import cn.hutool.core.util.RuntimeUtil;
import com.kehao.auto_connect_wifi.pojo.WifiHotspotEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WifiTool {
    /**
     * 获取附近的WiFi
     * @return
     */
    public List<WifiHotspotEntity> getWifiNearBy(){
        String cmd = "netsh wlan show networks mode=bssid";
        String cmdResult = RuntimeUtil.execForStr(cmd);
        String[] cmdResultArray = cmdResult.split("\r\n\r\n");
        List<WifiHotspotEntity> wifiHotspotEntityList = new ArrayList<>();
        if(cmdResultArray.length>=1){
            for (int i = 1; i < cmdResultArray.length; i++) {

                List<String[]> originDataList = Arrays.stream(cmdResultArray[i].trim().split("\r\n"))
                        .filter(str -> str.contains(":"))
                        .map(str -> str.split(":")).collect(Collectors.toList());

                Map<String, String> wifiMap = originDataList.stream().filter(arr->!arr[0].startsWith("         "))
                            .collect(Collectors.toMap(arr -> arr[0].trim(), arr -> arr[1].trim(),(v1,v2)->v1));

                String wifiName = wifiMap.get("SSID "+i);
                int signalStrength = originDataList.stream().filter(arr->arr[0].contains("Signal")||arr[0].contains("信号"))
                        .map(arr->Integer.parseInt(arr[1].trim().replace("%",""))).max((v1,v2)->v1-v2).get();

                wifiHotspotEntityList.add(WifiHotspotEntity.builder()
                        .name(wifiName)
                        .signal(signalStrength)
                        .build()
                );
            }
        }
        return wifiHotspotEntityList;
    }

    /**
     * 获取配置文件（连接过的WiFi名称）
     * @return
     */
    public List<String> getProfiles(){
        String cmd = "netsh wlan show profiles";
        String cmdResult = RuntimeUtil.execForStr(cmd);
        List<String> profiles = Arrays.stream(cmdResult.split("\n"))
                .filter(str -> str.contains(":"))
                .map(str -> str.split(":")[1].trim())
                .filter(str->Strings.isNotBlank(str))
                .collect(Collectors.toList());
        return profiles;
    }

    /**
     * 查看网络联通状态
     * @return
     */
    public boolean networkConnected() {
        String cmd = "ping www.baidu.com";
        String cmdResult = RuntimeUtil.execForStr(cmd);
        List<String> list = Arrays.stream(cmdResult.split("\n")).filter(str -> str.contains("回复")||str.contains("Reply")).collect(Collectors.toList());
        return list.size()>0;
    }

    /**
     * 连接到WiFi热点
     * @param wifiName wifi名
     */
    public boolean connectToHotspot(String wifiName)  {
        String cmd = String.format("netsh wlan connect name=\"%s\"", wifiName);
        String connectResult = RuntimeUtil.execForStr(cmd);
        if(connectResult.contains("已成功完成连接请求")||connectResult.contains("Connection request was completed successfully.")){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("连接WiFi：{} 发生错误",wifiName);
            }
            return true;
        }
        return false;
    }
}
