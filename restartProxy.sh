#!/bin/bash
if [ ! -e restart.info ]
then
 touch member.info
 echo "----------create member information file success----------" > restart.info
 echo $(date +%Y"."%m"."%d" "%k":"%M":"%S) >> restart.info 
fi
echo $(date +%Y"."%m"."%d" "%k":"%M":"%S) >> restart.info 
echo "Restart Proxy">>restart.info
pid=`ps aux | grep -v grep| grep  proxy.py| awk {'print($2)'}`
if [ -n "$pid"  ];then
    kill -9 $pid
else
    echo "already killed~"
fi
nohup python ~/Goagent/local/proxy.py >nohup.out &
