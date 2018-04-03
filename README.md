
# 道VPN Lite版

声明：该软件为GNU License开源软件，仅供学习研究用途。不得用于商业目的与非法用途。

道VPN Lite版可以在公用网络上建立专用网络，进行加密通讯，让用户进行远程访问。github 地址为

道VPN Lite版是用Java开发的，跨几乎所有平台的，非常非常非常简单易用的VPN软件。无需配置、一键部署。麻雀虽小，五脏俱全。

网络图如下：

![avatar](https://supermax197.github.io/img/TaoVPNLiteNet.png)

架构模型图如下：

![avatar](https://supermax197.github.io/img/TaoVPNLiteModel.png)

道VPN Lite版支持http与https协议。也就是说支持几乎所有网站、网络游戏及应用。

道VPN Lite版用Java语言开发，这意味着几乎所有平台都可以运行，如windows、linux、mac os、android等，并且客户端、服务端可以运行在不同平台上。

道VPN Lite版有自己的独特的通信加密系统，因此与传统VPN软件并不兼容，这也使得道VPN具有独特的网络安全性与穿透性。

道VPN Lite版的使用非常简单。下载源代码编译或者直接下载jar包，启动客户端的命令：
_java -cp tao-vpn-lite.jar org.tao.vnp.lite.client.Client xxx.xxx.xxx.xxx 8082 8081_
,其中xxx是vpn服务端的ip地址，8082是服务端的端口号，8081是vpn客户端的端口号。启动客户端的命令：
_java -cp tao-vpn-lite.jar org.tao.vpn.lite.Server 8082_
,其中8082是服务端的端口号

在linux服务器上，可以使用类似的守护shell来启动服务端，
_#! /bin/sh
while true ; do
NUM=`ps aux | grep -w tao-pry-1.0.jar | grep -v grep |wc -l`
if [ "${NUM}" -lt "1" ];then
echo "tao-pry-1.0.jar was killed"
nohup java -cp tao-vpn-lite.jar tao-vpn-lite.jar org.tao.vpn.lite.Server 8082 >/dev/null 2>%261 %26 fi
sleep 5s
done
exit 0_

如果你感兴趣或有更高需求，可以联系我 jeruen@gmail.com。

道VPN专业版提供多用户登录管理，自定义对称或非对称加密算法，最高可达10的240次方的加密强度，现如今的计算系统无法破解。更强更复杂的功能，满足企业的需求。

如果你支持道VPN Lite，可以通过扫描以下二维码或点击paypal按钮进行赞助。

![avatar](https://supermax197.github.io/img/weixin.png)

<div class="scale">

<form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top"><input type="hidden" name="cmd" value="_s-xclick"> <input type="hidden" name="hosted_button_id" value="X5ZAA9MDNDEBY"> <input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!"> ![](https://www.paypalobjects.com/zh_XC/i/scr/pixel.gif)</form>

</div>

![avatar](https://supermax197.github.io/img/zhifubao.jpg)

</article>
