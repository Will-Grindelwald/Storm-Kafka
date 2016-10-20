#!/bin/bash

# 关闭防火墙
#sudo firewall-cmd --zone=public --add-port=2888/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=3888/tcp --permanent
sudo firewall-cmd --zone=public --add-port=2889/tcp --permanent
sudo firewall-cmd --zone=public --add-port=3889/tcp --permanent
sudo firewall-cmd --reload

# hosts 映射
#echo "192.168.125.236 zoo1" >> /etc/hosts
#echo "192.168.125.237 zoo2" >> /etc/hosts
#echo "192.168.125.238 zoo3" >> /etc/hosts

# java 配置
#sudo echo 'export JAVA_HOME=/usr/local/java/jdk1.8.0_102' >> /etc/profile  # 要修改
#sudo echo 'export JRE_HOME=${JAVA_HOME}/jre' >> /etc/profile
#sudo echo 'export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib' >> /etc/profile
#sudo echo 'export PATH=${PATH}:${HOME}/bin:${JAVA_HOME}/bin' >> /etc/profile
#sudo update-alternatives --install /usr/bin/java java ${JAVA_HOME}/bin/java 300
#sudo update-alternatives --install /usr/bin/jar jar ${JAVA_HOME}/bin/jar 300
#sudo update-alternatives --install /usr/bin/javac javac ${JAVA_HOME}/bin/javac 300
#sudo update-alternatives --install /usr/bin/javah javah ${JAVA_HOME}/bin/javah 300
#sudo update-alternatives --install /usr/bin/javap javap ${JAVA_HOME}/bin/javap 300

# Zookeeper 配置
./setup-zookeeper.sh /home/will 3 1
