#  

setup.sh 是 在开发人员本地机器上 部署 单机测试环境 的一键脚本

## usage

用你的账户执行 `./setup.sh`, 将安装到 /home/$USER/work 下, 不要用 `sudo` 或 root 用户, 那将安装到 /home/root/work, 且必须是 root 权限才能执行

本目录 目录结构

```
.
|-- boot
|   |-- boot-all.sh              # 一键启动 kafka storm zookeeper `./boot-all.sh`
|   |-- kafka-start.sh           # 启动 kafka 的脚本 `./kafka-start.sh`
|   |-- kafka-stop.sh            # 关闭 kafka 的脚本 `./kafka-start.sh`
|   |-- storm-start.sh           # 启动 storm 的脚本 `./storm-start.sh`
|   `-- zserver.sh               # zookeeper server 命令脚本 `./zserver.sh start` `./zserver.sh stop` `./zserver.sh status`
|-- config
|   |-- server.properties
|   |-- storm.yaml
|   `-- zoo.cfg
|-- kafka
|   `-- put_kafka_tgz_here       # kafka 安装包存放位置
|-- README.md
|-- setup.sh                     # 部署环境脚本
|-- storm
|   `-- put_storm_tar_gz_here    # storm 安装包存放位置
`-- zookeeper
    `-- zookeeper-3.4.9.tar.gz   # zookeeper 安装包存放位置
```

/home/$USER/work 目录结构

```
.
|-- apache-storm-1.0.2           # storm 安装目录
|   `...
|   `...
|   `...
|-- boot-all.sh                  # 一键启动 kafka storm zookeeper `./boot-all.sh`
|-- kafka_2.11-0.9.0.1           # kafka 安装目录
|   `...
|   `...
|   `...
|-- kafka-logs                   # kafka 日志目录
|-- kafka-start.sh               # 启动 kafka 的脚本 `./kafka-start.sh`
|-- kafka-stop.sh                # 关闭 kafka 的脚本 `./kafka-start.sh`
|-- storm-start.sh               # 启动 storm 的脚本 `./storm-start.sh`
|-- storm-workdir                # storm 工作目录
|-- zookeeper
|   |-- data                     # zookeeper 工作目录
|   `-- logs                     # zookeeper 日志目录
|-- zookeeper-3.4.9              # zookeeper 安装目录
|   `...
|   `...
|   `...
`-- zserver.sh                   # zookeeper server 命令脚本 `./zserver.sh start` `./zserver.sh stop` `./zserver.sh status`
```

