# storm-kafka-zookeeper cluster

setup.sh 是 storm-kafka-zookeeper 集群环境 的一键部署脚本

## usage

1. 编辑 config 文件, 填写 zookeeper/storm/kafka 集群共有几台机器, 每台的 IP.
2. 用你的账户执行 `./setup.sh {zoo_num} {storm_num} {kafka_num}`, 将安装到 /home/$USER/work 下, 不要用 `sudo` 或 root 用户, 那将安装到 /home/root/work, 且必须是 root 权限才能执行. 三个参数分别为本机在 zookeeper/storm/kafka 集群中是第几台机器, 填 0 即不在本机安装 zookeeper/storm/kafka.

> 即: 在 zookeeper 集群第 i 台机器上执行 `./setup.sh i 0 0`  
> 即: 在 storm 集群第 j 台机器上执行         `./setup.sh 0 j 0`  
> 即: 在 kafka 集群第 k 台机器上执行         `./setup.sh 0 0 k`  
> 若既是 zookeeper 集群第 i 台机器又是 storm 集群第 j 台机器, 则执行 `./setup.sh i j 0`

PS: 安装到最后的时候会要求 sudo 权限以开启必须的端口.

本目录 目录结构

```
.
|-- boot                          # 一些便捷脚本
|   |-- ...
|   `-- ...
|-- conf
|   |-- server.properties
|   `-- zoo.cfg
|-- config                        # 自定义配置文件
|-- kafka
|   `-- put_kafka_tgz_here        # kafka 安装包存放位置
|-- setup.sh                      # 部署环境脚本
|-- storm
|   `-- put_storm_tar_gz_here     # storm 安装包存放位置
`-- zookeeper
    `-- put_zookeeper_tar_gz_here # zookeeper 安装包存放位置
```

安装完成后 /home/$USER/work 目录结构

```
.
|-- apache-storm-1.0.2            # storm 安装目录
|   |-- ...
|   `-- ...
|-- kafka_2.11-0.9.0.1            # kafka 安装目录
|   |-- ...
|   `-- ...
|-- kafka-logs                    # kafka 日志目录
|-- storm-workdir                 # storm 工作目录
|-- zookeeper
|   |-- data                      # zookeeper 工作目录
|   `-- logs                      # zookeeper 日志目录
`-- zookeeper-3.4.9               # zookeeper 安装目录
    |-- ...
    `-- ...
```
