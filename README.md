## 说明
这个项目构建是用于自动连接wifi为目的的,目前仅仅可用于windows

创建这个项目的原因是因为实验室的网络不稳定，经常发生断网现象

而我有远程连接实验室电脑的使用习惯，经常需要麻烦同学帮我重新联网

虽然windows的WiFi有设置自动连接的功能，但是不知道为什么有时候会失效

所以诞生了这样一个简单的项目，仅仅为了解决我简单的需求

## 实现思路
目前的实现思路是利用windows的命令行调用，来实现WiFi的自动连接
### wifi相关的dos命令
```dos
//获取附近WiFi
netsh wlan show networks mode=bssid

//连接其中一个WiFi
netsh wlan connect name="SSID"

//断开连接
netsh wlan disconnect

//列出WiFi配置文件（成功连接的WiFi自动生成）
netsh wlan show profiles

//导出SSID配置文件到当前工作目录
netsh wlan export profile key=clear

//添加和删除SSID配置文件
netsh wlan add profile filename="配置文件路径"
netsh wlan delete profile name="SSID"

//列出无线接口
netsh wlan show interface

//开启无线接口
netsh interface set interface "接口名称" enabled
```

### 执行过程
1. 定时(暂定5分钟)检测网络是否正常（通过ping www.baidu.com）
2. 若网络不正常，则获取附近所有的wifi列表
3. 遍历wifi列表
4. 如果存在配置文件的，则连接之（只要连接过的都会有配置文件）

## 使用方法
### 注册为系统服务
1. 项目打包：`mvn package -DskipTests`
2. 将jar包移动到windows_service文件夹下
3. 注册为服务：`auto_connect_wifi.exe install`
4. 启动服务：进入windows服务管理页面，手动开启服务 或者 `auto_connect_wifi.exe start`

### 其他命令
1. 停止服务 `auto_connect_wifi.exe stop`
2. 重启服务 `auto_connect_wifi.exe restart`
3. 查看服务状态 `auto_connect_wifi.exe status`
4. 卸载服务 `auto_connect_wifi.exe uninstall`
