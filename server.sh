#! /bin/sh  
  
while true ; do  
     NUM=`ps aux | grep -w tao-vpn-lite-1.0.jar | grep -v grep |wc -l`  
      #echo $NUM  
      if [ "${NUM}" -lt "1" ];then  
         echo "tao-vpn-lite-1.0.jar was killed"  
         nohup java -cp ~/tao-vpn-lite-1.0.jar org.tao.vpn.lite.Server 8082 >/dev/null 2>&1 &
     fi
     sleep 5s  
 done  
   
 exit 0 