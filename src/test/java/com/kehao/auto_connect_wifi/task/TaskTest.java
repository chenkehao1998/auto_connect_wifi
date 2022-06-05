package com.kehao.auto_connect_wifi.task;


import cn.hutool.core.util.RuntimeUtil;
import com.kehao.auto_connect_wifi.tool.WifiTool;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void sysTest(){
        WifiTool wifiTool = new WifiTool();
        wifiTool.connectToHotspot("ChinaNet-1603");
    }
}
