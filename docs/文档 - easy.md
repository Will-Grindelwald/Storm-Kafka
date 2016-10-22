# 文档

## 0 简介

### 0.1 项目目标

**实时** **工业** **大数据** **分析**

### 0.2 系统简介

系统版本:

    OS:        CentOS 7 1511 版
    Python:    2.7.5 (CentOS 7 自带)
    Java:      1.8.0_65 (CentOS 7 自带)
    Zookeeper: 3.4.9
    Storm:     1.0.2
    Scala:     2.11.8
    Kafka:     0.9.0.1
    Maven:     3.3.9

IP 及 端口 分配:

    Zookeeper: 192.168.1.1 192.168.1.2 192.168.1.3 开放端口: 2181 2888 3888
    Storm: Nimbus: 192.168.1.4 192.168.1.5
           Supervisor: 192.168.1.6 192.168.1.7 192.168.1.8 192.168.1.9 开放端口: 6700 6701 6702 6703
    Kafka: Broker: 192.168.1.10 192.168.1.11 192.168.1.12

## 1 Zookeeper 搭建

### 1.1 单机模式

此模式主要用于 **开发人员本地环境下测试代码**

#### 1.1.1 解压 Zookeeper 并进入其根目录

```bash
tar -xzf zookeeper-3.4.9.tar.gz -C /usr/local/
cd /usr/local/zookeeper-3.4.9
```

#### 1.1.2 创建一个文件 conf/zoo.cfg

```bash
cp conf/zoo_sample.cfg conf/zoo.cfg
```

#### 1.1.3 修改内容如下:

```bash
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/var/lib/zookeeper/data
dataLogDir=/var/lib/zookeeper/log
clientPort=2181
```

* tickTime: 是 zookeeper 的最小时间单元的长度(以毫秒为单位), 它被用来设置心跳检测和会话最小超时时间(tickTime 的两倍)
* initLimit: 初始化连接时能容忍的最长 tickTime 个数
* syncLimit: follower 用于同步的最长 tickTime 个数
* dataDir: 服务器存储 **数据快照** 的目录
* dataLogDir: 服务器存储 **事务日志** 的目录
* clientPort: 用于 client 连接的 server 的端口.

其中需要注意的是`dataDir`和`dataLogDir`, 分别是 zookeeper 运行时的数据目录和日志目录, 要保证 **这两个目录已创建** 且 **运行 zookeeper 的用户拥有这两个目录的所有权**

#### 1.1.4 测试

启动zookeeper:

```bash
bin/zkServer.sh start
```

使用 java 客户端连接 ZooKeeper

```bash
./bin/zkCli.sh -server 127.0.0.1:2181
```

然后就可以使用各种命令了, 跟文件操作命令很类似, 输入help可以看到所有命令.

关闭 Zookeeper:

```bash
./bin/zkServer.sh stop
```

### 1.2 集群模式

此模式是 **生产环境中实际使用的模式**

因为 zookeeper 保证 2n + 1 台机器最大允许 n 台机器挂掉, 所以配置集群模式最好是奇数台机器: 3, 5, 7...

最少 3 台构成集群

#### 1.2.1 hosts 映射(可选)

```bash
echo "192.168.1.1 zoo1" >> /etc/hosts
echo "192.168.1.2 zoo2" >> /etc/hosts
echo "192.168.1.3 zoo3" >> /etc/hosts
```

#### 1.2.2 修改 zookeeper-3.4.9/conf/zoo.cfg 文件

```bash
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/var/lib/zookeeper/data
dataLogDir=/var/lib/zookeeper/log
clientPort=2181
server.1=192.168.1.1:2888:3888
server.2=192.168.1.2:2888:3888
server.3=192.168.1.3:2888:3888
```

与单机模式的不同就是最后三条: `server.X=host:portA:portB`

```bash
server.1=192.168.1.1:2888:3888
server.2=192.168.1.2:2888:3888
server.3=192.168.1.3:2888:3888
```

或

```bash
server.1=zoo1:2888:3888
server.2=zoo2:2888:3888
server.3=zoo3:2888:3888
```

X 为标识为 X 的机器, host 为其 hostname 或 IP, portA 用于这台机器与集群中的 Leader 机器通信, portB 用于 server 选举 leader.

要配单机伪分布式的话, 可以修改这里为

```bash
server.1=localhost:2888:3888
server.2=localhost:2889:3889
server.3=localhost:2890:3890
```

然后每个 zookeeper 实例的 dataDir 和 dataLogDir 配置为不同的即可

#### 1.2.3 myid 文件

在标示为 X 的机器上, 将 X 写入 ${dataDir}/myid 文件, 如: 在 192.168.1.2 机器上的 /var/lib/zookeeper/data 目录下建立文件 myid, 写入 2

````bash
echo "2" > /var/lib/zookeeper/data/myid
````

#### 1.2.4 开放端口

CentOS 7 使用 firewalld 代替了原来的 iptables, 基本使用如下

```bash
systemctl start firewalld                                      # 启动防火墙
firewall-cmd --state                                           # 检查防火墙状态
firewall-cmd --zone=public --add-port=2888/tcp --permanent     # 永久开启 2888 端口
firewall-cmd --reload                                          # 重新加载防火墙规则
firewall-cmd --list-all                                        # 列出所有防火墙规则
```

把 Zookeeper 用到的端口开放出来

```bash
firewall-cmd --zone=public --add-port=2181/tcp --permanent     # 永久开启 2181 端口
firewall-cmd --zone=public --add-port=2888/tcp --permanent     # 永久开启 2888 端口
firewall-cmd --zone=public --add-port=3888/tcp --permanent     # 永久开启 3888 端口
firewall-cmd --reload                                          # 重新加载防火墙规则
```

#### 1.2.5 测试

在 **集群中所有机器上** 启动 zookeeper(尽量同时):

```bash
bin/zkServer.sh start
```

查看状态, 应该有一台机器显示`mode: leader`, 其余为`mode: follower`

```bash
bin/zkServer.sh status
```

使用 java 客户端连接 ZooKeeper

```bash
./bin/zkCli.sh -server 192.168.1.1:2181
```

然后就可以使用各种命令了, 跟文件操作命令很类似, 输入help可以看到所有命令.

关闭 zookeeper:

```bash
./bin/zkServer.sh stop
```

### 1.3 Zookeeper 常见问题

查看状态时, 应该有一台机器显示`mode: leader`, 其余为`mode: follower`

```bash
bin/zkServer.sh status
```

当显示`Error contacting service. It is probably not running.`时, 可以查看日志

```bash
cat zookeeper.out
```

查看 zookeeper.out 日志可以看到是那些机器连不上, 可能是 **网络, ip, 端口, 配置文件, myid 文件** 的问题.
正常应该是: 先是一些 java 异常, 这是因为 ZooKeeper 集群启动的时候, 每个结点都试图去连接集群中的其它结点, 先启动的肯定连不上后面还没启动的, 所以上面日志前面部分的异常是可以忽略的, 当集群所有的机器的 zookeeper 都启动起来, 就没有异常了, 并选举出来了 leader.

PS: 因为 zkServer.sh 脚本中是用 nohup 命令启动 zookeeper 的, 所以 zookeeper.out 文件是在调用 zkServer.sh 时的路径下, 如:用 `bin/zkServer.sh start` 启动则 zookeeper.out 文件在 `zookeeper-3.4.9/` 下; 用 `zkServer.sh start` 启动则 zookeeper.out 文件在 `zookeeper-3.4.9/bin/` 下.

## 2 Storm 搭建

### 2.1 单机模式

此模式主要用于 **开发人员本地环境下测试代码**

#### 2.1.1 搭建 Zookeeper (单机 or 集群)

见 [1.2.1 单机模式](# 1.2.1 单机模式) or 见 [1.2.2 集群模式](# 1.2.2 集群模式)

#### 2.1.2 安装 Storm 依赖库(Java、Python)

在集群中的所有机器上安装 Storm 必要的依赖组件: Java7(or 8), Python2.7.

使用 CentOS 7 自带的 Python 2.7.5 及 openjdk 1.8.0_65 即可

#### 2.1.3 解压 Storm 并启动 Storm 各个后台进程

不需额外配置, 即是单机模式

* **Nimbus**: 运行

```bash
nohup bin/storm nimbus > logs/nimbus-boot.log 2>&1 &
```

启动 Nimbus 后台程序, 并放到后台执行, 标准输出和错误输出定向到 `./logs/nimbus-boot.log`, 有问题时可以去看这个文件

* **Supervisor**: 运行

```bash
nohup bin/storm supervisor > logs/supervisor-boot.log 2>&1 &
```

启动 Supervisor 后台程序, 并放到后台执行, 标准输出和错误输出定向到 `./logs/supervisor-boot.log`, 有问题时可以去看这个文件

* **Storm UI**: 运行

```bash
nohup bin/storm ui > logs/ui-boot.log 2>&1 &
```

启动 Storm UI 后台程序, 并放到后台执行, 标准输出和错误输出定向到 `./logs/ui-boot.log`, 有问题时可以去看这个文件.

Storm UI 可以在浏览器中方便地监控集群与拓扑运行状况, 启动后可以通过 *http://{nimbus host}:8080* 观察集群的 Worker 资源使用情况、Topologies 的运行状态等信息.

PS: Storm 后台进程被启动后, 将在 Storm 安装部署目录下的 logs/ 子目录下生成各个进程的日志文件, 这是 Storm 的默认设置, 日志文件的路径与相关配置信息可以在 {STORM_HOME}/logback/cluster.xml 文件中修改.

### 2.2 集群模式

此模式是 **生产环境中实际使用的模式**

#### 2.2.1 hosts 映射(可选)

最好配置主机名, 配置文件 conf/storm.yaml 中若是填写 IP, 在 Storm UI 中显示不正常

```bash
echo "192.168.1.4 nim1" >> /etc/hosts
echo "192.168.1.5 nim2" >> /etc/hosts

echo "192.168.1.6 sup1" >> /etc/hosts
echo "192.168.1.7 sup2" >> /etc/hosts
echo "192.168.1.8 sup3" >> /etc/hosts
echo "192.168.1.9 sup4" >> /etc/hosts
```

#### 2.2.2 搭建 Zookeeper 集群

见 [1.2.2 集群模式](# 1.2.2 集群模式)

关于 ZooKeeper 部署的两点说明:

* ZooKeeper 必须在监控模式下运行. 因为 ZooKeeper 是个快速失败系统, 如果遇到了故障, ZooKeeper 服务会主动关闭. 更多详细信息请参考: http://zookeeper.apache.org/doc/r3.3.3/zookeeperAdmin.html#sc_supervision. 我们选择第一个方案: daemontools: http://cr.yp.to/daemontools.html
* 需要设置一个 cron 服务来定时压缩 ZooKeeper 的数据与事务日志. 因为 ZooKeeper 的后台进程不会处理这个问题, 如果不配置 cron, ZooKeeper 的日志会很快填满磁盘空间. 更多详细信息请参考: http://zookeeper.apache.org/doc/r3.3.3/zookeeperAdmin.html#sc_maintenance

#### 2.2.3 安装 Storm 依赖库(Java、Python)

在集群中的所有机器上安装 Storm 必要的依赖组件: Java7(or 8), Python2.7.

使用 CentOS 7 自带的 Python 2.7.5 及 openjdk 1.8.0_65 即可

#### 2.2.4 解压 Storm 并进入其根目录

```bash
tar -xzf apache-storm-1.0.2.tar.gz -C /usr/local/
cd /usr/local/apache-storm-1.0.2
```

#### 2.2.5 修改 conf/storm.yaml 配置文件

storm.yaml 会覆盖 defaults.yaml 中各个配置项的默认值, 以下几个是在安装集群时必须配置的选项

```bash
########### These MUST be filled in for a storm configuration
# yaml 文件的配置使用"-"来表示数据的层次结构, 配置项的:后必须有空格, 否则该配置项无法识别
# Storm 关联的 ZooKeeper 集群的地址列表
storm.zookeeper.servers:
    - "192.168.1.1"
    - "192.168.1.2"
    - "192.168.1.3"

# 如果使用的 ZooKeeper 集群的端口不是默认端口, 还需要配置 storm.zookeeper.port
# storm.zookeeper.port: 2181

# Storm 工作目录, 需要提前创建该目录并给以足够的访问权限
storm.local.dir: "/var/lib/storm-workdir"

# 用作 nimbus 的机器的 host list, 若 nimbus 是单机, 可以使用 nimbus.seeds: ["nim1"], 这里用的双机
# 若是填写 IP, 在 Storm UI 中显示不正常
nimbus.seeds: ["nim1", "nim2"]

# Supervisor工作节点上 worker 的端口, 每个 worker 占用一个单独的端口用于接收消息, 有几个端口就最多会有几个 worker 运行, 这里配置了 4 个
supervisor.slots.ports:
    - 6700
    - 6701
    - 6702
    - 6703
```

#### 2.2.6 开放端口

把 Storm 用到的端口开放出来

```bash
firewall-cmd --zone=public --add-port=3772/tcp --permanent     # drpc.port
firewall-cmd --zone=public --add-port=3773/tcp --permanent     # drpc.invocations.port
firewall-cmd --zone=public --add-port=3774/tcp --permanent     # drpc.http.port
firewall-cmd --zone=public --add-port=6627/tcp --permanent     # nimbus.thrift.port
firewall-cmd --zone=public --add-port=6699/tcp --permanent     # pacemaker.port
firewall-cmd --zone=public --add-port=8000/tcp --permanent     # logviewer.port
firewall-cmd --zone=public --add-port=8080/tcp --permanent     # storm.ui.port
firewall-cmd --zone=public --add-port=6700/tcp --permanent     # supervisor.slots.ports -- 取决于上面的配置
firewall-cmd --zone=public --add-port=6701/tcp --permanent     # 
firewall-cmd --zone=public --add-port=6702/tcp --permanent     # 
firewall-cmd --zone=public --add-port=6703/tcp --permanent     # 
firewall-cmd --reload                                          # 重新加载防火墙规则
```

#### 2.2.7 启动 Storm 各个后台进程

和 Zookeeper 一样, Storm 也是快速失败(fail-fast)的系统, 这样 Storm 才能在任意时刻被停止, 并且当进程重启后被正确地恢复执行. 这也是为什么 Storm 不在进程内保存状态的原因, 即使 Nimbus 或 Supervisors 被重启, 运行中的 Topologies 不会受到影响. 以下是启动 Storm 各个后台进程的方式:

* **Nimbus**: 在 Storm 主控(Master)节点上运行

```bash
nohup bin/storm nimbus > logs/nimbus-boot.log 2>&1 &
```

启动 Nimbus 后台程序, 并放到后台执行, 标准输出和错误输出定向到 `./logs/nimbus-boot.log`, 有问题时可以去看这个文件

* **Supervisor**: 在 Storm 各个工作节点(Worker)上运行

```bash
nohup bin/storm supervisor > logs/supervisor-boot.log 2>&1 &
```

启动 Supervisor 后台程序, 并放到后台执行, 标准输出和错误输出定向到 `./logs/supervisor-boot.log`, 有问题时可以去看这个文件

* **Storm UI**: 在 Storm 主控(Master)节点上运行

```bash
nohup bin/storm ui > logs/ui-boot.log 2>&1 &
```

启动 Storm UI 后台程序, 并放到后台执行, 标准输出和错误输出定向到 `./logs/ui-boot.log`, 有问题时可以去看这个文件.

Storm UI 可以在浏览器中方便地监控集群与拓扑运行状况, 启动后可以通过 *http://{nimbus host}:8080* 观察集群的 Worker 资源使用情况、Topologies 的运行状态等信息.

PS: Storm UI 必须在 Nimbus 机器(Nimbus 集群的话, 其中一台即可)上, 否则 UI 无法正常工作.

* **Logviewer**: 在需要查看 work.log 节点上运行

```bash
nohup bin/storm logviewer > logs/logviewer-boot.log 2>&1 &
```

启动 logviewer 后台程序, 并放到后台执行, 标准输出和错误输出定向到 `./logs/logviewer-boot.log`, 有问题时可以去看这个文件.

Logviewer 是 Storm UI 中用来查看 Nimbus/Supervisor 的 log 的工具.

**Storm 后台进程被启动后, 将在 Storm 安装部署目录下的 logs/ 子目录下生成各个进程的日志文件, 这是 Storm 的默认设置, 日志文件的路径与相关配置信息可以在 {STORM_HOME}/logback/cluster.xml 文件中修改.**

至此, Storm 集群已经部署、配置完毕, 可以向集群提交拓扑运行了.

#### 2.2.8 查看 Storm 状态

利用 Storm UI, 通过 http://{nimbus host}:8080 查看集群的各种状态.

#### 2.2.9 监控 Supervisor 的运行情况(可选)

Storm 提供了一种机制, 使 Supervisor 定期运行管理人员提供的脚本, 以确定节点是否正常.

管理人员可以让 Supervisor 执行位于 storm.health.check.dir 中的脚本来确定节点是否处于健康状态, 如果脚本检测到节点处于不正常状态, 则在标准输出中打印一行以 ERROR 开头的字符串.

Supervisor 将定期运行storm.health.check.dir 中的脚本并检查输出, 如果脚本的输出包含字符串 ERROR, Supervisor 将关闭所有工作线程并退出.

如果 Supervisor 正在运行, 可以调用"/bin/storm node-health-check"来确定节点是否正常.

在 conf/storm.yaml 配置 storm.health.check.dir:

```bash
storm.health.check.dir: " healthchecks"
```

配置执行 healthcheck 脚本的周期:

```bash
storm.health.check.timeout.ms: 5000
```

PS: **脚本必须具有执行权限.**

#### 2.2.10 配置外部库与环境变量(可选)

如果你需要使用某些外部库或者定制插件的功能, 你可以将相关 jar 包放入 extlib 与 extlib-daemon 目录下. 注意, extlib-daemon 目录仅用于存储后台进程(Nimbus, Supervisor, DRPC, UI, Logviewer)所需的 jar 包, 例如 HDFS 以及定制的调度库.

另外可以使用 STORM_EXT_CLASSPATH 和 STORM_EXT_CLASSPATH_DAEMON 两个环境变量来配置普通外部库与"仅用于后台进程"外部库的 classpath.

### 2.3 向 Storm 集群提交任务

* 启动 Storm Topology:

```bash
storm jar allmycode.jar org.me.MyTopology arg1 arg2 arg3
```

其中, allmycode.jar 是包含 Topology 实现代码的 jar 包, org.me.MyTopology 的 main 方法是 Topology 的入口, arg1、arg2 和 arg3 为 org.me.MyTopology 执行时需要传入的参数.

* 停止 Storm Topology:

```bash
storm kill {toponame}
```

其中, {toponame} 为 Topology 提交到 Storm 集群时指定的 Topology 任务名称.

### 2.4 Storm 常见问题

* 运行 storm 命令报错

出现语法错误:

```
File "/home/storm/apache-storm-0.9.3/bin/storm", line 61
   normclasspath = cygpath if sys.platform == 'cygwin' else identity
                            ^
SyntaxError: invalid syntax

```

这是由于系统中安装的低版本 Python 部分语法不支持, 需要重新安装高版本 Python(如2.7.x).

PS: 部分系统Python默认安装位置不是 `/usr/bin/python`, 必须在 Python 安装完成之后将安装版本Python关联到该位置. 参考操作方法: `cd /usr/bin` `mv python python.bk``ln -s /usr/local/Python-2.7.8/python python`

* Storm 在 ssh 断开后自动关闭

这是由于 Storm 是由默认的 Shell 机制打开运行, 在 ssh 或 telnet 断开后终端会将挂断信号发送到控制进程, 进而会关闭该 Shell 进程组中的所有进程. 因此需要在 Storm 后台启动时使用 `nohup` 命令和 `&` 标记可以使进程忽略挂断信号, 避免程序的异常退出:

```bash
nohup bin/storm nimbus > logs/nimbus-boot.log 2>&1 &
nohup bin/storm supervisor > logs/supervisor-boot.log 2>&1 &
nohup bin/storm ui > logs/ui-boot.log 2>&1 &
nohup bin/storm logviewer > logs/logviewer-boot.log 2>&1 &
```

* Storm UI 网页无法打开

检查 Storm 主机(nimbus 与 ui 所在运行服务器)的防火墙设置, 是否存在监控端口屏蔽(ui 的默认端口是 8080)

PS: 测试环境下可以不考虑安全问题直接关闭防火墙

* Strom UI 网页中没有 topology 信息

只有集群(Cluster)模式的 topology 才会在监控页面显示, 需要将提交到集群的 topology 的运行模式由本地模式(local mode)改为集群模式

* Storm UI 网页中无法打开各个端口的 worker.log

在需要查看 log 的机器上启动 logviewer 进程:

```bash
nohup bin/storm logviewer > logs/logviewer-boot.log 2>&1 &
```

* expected '<document start>', but found BlockMappingStart 错误

Storm 启动失败, 在 nohup.out 中有如下错误信息

```
Exception in thread "main" expected '<document start>', but found BlockMappingStart
```

一般在这类信息后会有相关错误位置说明信息, 如

```
in 'reader', line 23, column 2:
     nimbus.host: "hd124"
     ^
```

或者

```
in 'reader', line 7, column 1:
     storm.zookeeper.port: 2181
     ^
```

这类错误主要是storm.yaml文件的配置格式错误造成的, 一般是配置项的空格遗漏问题. 如上面两例分别表示nimbus.host与storm.zookeeper.port两个配置项开头缺少空格, 或者":"后缺少空格. 正确添加空格后重新启动Storm即可.

* Storm worker 数量与配置数量不一致

在 topology 中设置 worker 数量:

`conf.setNumWorkers(6);`

但是, 集群中实际的 worker 数量却不到6.

这是由于每个 supervisor 中有 worker 数量的上限, 这个上限值除了要满足系统允许的最大 slot 上限值 `8` 之外, 还需要小于 Storm 配置文件中的端口数量:

```
supervisor.slots.ports:
    - 6700
    - 6701
    - 6702
    - 6703
```

例如这里 supervisor 只配置了 4 个端口, 那么在这个 supervisor 上最多只能运行 4 个 worker 进程. 因此, 如果需要更多的 worker 就需要配置更多的端口.

* 日志无法记录到程序中配置的路径

Storm 默认将日志统一记录到 `$STORM_HOME/logs` 目录中, 不支持在程序中自定义的路径. 但是, 集群的日志记录目录是可以修改的, 0.9 以上版本的 Storm 可以在 `$STORM_HOME/logback/cluster.xml` 配置文件中修改, 其他早期版本可以在`log4j/*.properties` 配置文件中修改.

## 3 Kafka 搭建

#### 3.1 hosts 映射(可选, 建议)

```bash
echo "192.168.1.10 kfk1" >> /etc/hosts
echo "192.168.1.11 kfk2" >> /etc/hosts
echo "192.168.1.12 kfk3" >> /etc/hosts
```

#### 3.2 搭建 Zookeeper (单机 or 集群)

Broker, Producer, Consumer 的运行都需要 ZooKeeper

见 [1.2.1 单机模式](# 1.2.1 单机模式) or 见 [1.2.2 集群模式](# 1.2.2 集群模式)

#### 3.3 Broker 的配置

```bash
tar -xzf kafka_2.11-0.9.0.1.tgz -C /usr/local/
cd /usr/local/kafka_2.11-0.9.0.1
```

config 文件夹下是各个组件的配置文件, server.properties 是 Broker 的配置文件, 需要修改和注意的有

```bash
broker.id=0                    # 本 Broker 的 id, 只要非负数且各 Broker 的 id 不同即可, 一般依次加 1
listeners=PLAINTEXT://:9092    # Broker 监听的端口, Producer, Consumer 会连接这个端口
port=9092                      # 同上
log.dirs=/var/lib/kafka        # log 目录, 此目录要存在且有足够权限
host.name=kfk1                 # 本 Broker 的 hostname
zookeeper.connect=zoo1:2181,zoo2:2181,zoo3:2181 # Zookeeper 的连接信息
```

**注意: broker.id 和 host.name 在每台机器上是不一样的, 要按实际填写**

即在 kfk2, kfk3 上

```bash
broker.id=1
host.name=kfk2
```

```bash
broker.id=2
host.name=kfk3
```

#### 3.4 开放端口

把 Kafka 用到的端口开放出来

```bash
firewall-cmd --zone=public --add-port=9092/tcp --permanent     # 永久开启 9092 端口
firewall-cmd --reload                                          # 重新加载防火墙规则
```

#### 3.5 Broker 运行与终止

Broker 运行命令如下, 将 Broker 放到后台执行, 且不受终端关闭的影响, 标准输出和错误输出定向到 `./logs/kafka-server-boot.log`, 有问题时可以去看这个文件

```bash
nohup bin/kafka-server-start.sh config/server.properties > logs/kafka-server-boot.log 2>&1 &
```

终止 Broker

```bash
bin/kafka-server-stop.sh config/server.properties
```

#### 3.6 测试

我们使用 Kafka 自带的基于 Console 的 Producer 和 Consumer 脚本做测试.

先只启动一台机器上的 Broker. 在 kfk1 上运行

```bash
> bin/kafka-server-start.sh config/server.properties > logs/kafka-server-boot.log 2>&1 &
```

##### 3.6.1 创建 Topic

现在我们开始创建一个名为"TestCase"的单分区单副本的 Topic.

```
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

现在我们应该可以通过运行`list topic`命令查看到这个 Topic:

```
> bin/kafka-topics.sh --list --zookeeper localhost:2181
test
```

另外, 除去手工创建 Topic 以外, 你也可以将你的 Brokers 配置成当消息发布到一个不存在的 Topic 自动创建此 Topics.

##### 3.6.2 启动 生产者

Kafka 附带一个 **终端生产者** 可以从文件或者标准输入中读取输入然后发送这个消息到 Kafka 集群. 默认情况下每行信息被当做一个消息发送.

运行生产者脚本然后在终端中输入一些消息, 即可发送到服务器.

```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
This is a message
This is another message
```

PS: 通过键入 **Ctrl-C** 来终止终端生产者.

##### 3.6.3 启动 消费者

Kafka 也附带了一个 **终端生产者** 可以导出这些消息到标准输出.

```
> bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning
This is a message
This is another message
```

如果你在不同的终端运行生产者和消费者这两个命令, 那么现在你就应该能再生产者的终端中键入消息同时在消费者的终端中看到.

所有的命令行工具都有很多可选的参数; 不添加参数直接执行这些命令将会显示它们的使用方法, 更多内容可以参考他们的手册.

PS: 通过键入 **Ctrl-C** 来终止终端消费者.

##### 3.6.4 配置一个多节点集群

我们已经成功的以单 Broker 的模式运行起来了, 但这并没有意思. 对于 Kafka 来说, 一个单独的 Broker 就是一个大小为 1 的集群, 所以集群模式就是多启动几个 Broker 实例.

我们将我们的集群扩展到3个节点. 在另外两台机器 kfk2, kfk3 上运行

```bash
> bin/kafka-server-start.sh config/server.properties > logs/kafka-server-boot.log 2>&1 &
```

现在我们可以创建一个新的 Topic 并制定副本数量为 3:

```
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic my-replicated-topic
```

运行`describe topics`命令, 可以知道每个 Broker 具体的工作:

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic
Topic:my-replicated-topic    PartitionCount:1    ReplicationFactor:3    Configs:
    Topic: my-replicated-topic    Partition: 0    Leader: 1    Replicas: 1,2,0    Isr: 1,2,0
```

解释一下输出的内容. 第一行给出了所有 partition 的一个摘要, 每行给出一个 partition 的信息. 因为我们这个 topic 只有一个 partition 所以只有一行信息.

* "leader" 负责所有 partition 的读和写请求的响应. "leader" 是随机选定的.
* "replicas" 是备份节点列表, 包含所有复制了此 partition log 的节点, 不管这个节点是否为 leader 也不管这个节点当前是否存活, 只是显示.
* "isr" 是当前处于同步状态的备份节点列表. 即 "replicas" 列表中处于存活状态并且与 leader 一致的节点.

注意本例中 Broker 1 是这个有一个 partition 的 topic 的 leader.

我们可以对我们原来创建的单分区单副本 topic 运行相同的命令, 来观察它保存在哪里:

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic test
Topic:test    PartitionCount:1    ReplicationFactor:1    Configs:
    Topic: test    Partition: 0    Leader: 0    Replicas: 0    Isr: 0
```

可以发现原来的那个 topic 没有副本而且它在 [我们创建它时集群仅有的一个节点] Broker 0 上.

现在我们发布几个消息到我们的新 topic 上:

```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic my-replicated-topic
...
my test message 1
my test message 2
```

现在让我们消费这几个消息:

```
> bin/kafka-console-consumer.sh --zookeeper localhost:2181 --from-beginning --topic my-replicated-topic
...
my test message 1
my test message 2
```

现在让我们测试一下集群容错. Broker 1 正在作为 leader, 所以我们杀掉它:

```
> ps | grep server.properties
7564 ttys002    0:15.91 /System/Library/Frameworks/JavaVM.framework/Versions/1.8/Home/bin/java...
> kill -9 7564
```

或在 kfk1 机器上运行

```bash
bin/kafka-server-stop.sh config/server.properties
```

集群领导已经切换到一个从服务器上, Broker 1 节点也不在出现在同步副本列表中了:

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic
Topic:my-replicated-topic    PartitionCount:1    ReplicationFactor:3    Configs:
    Topic: my-replicated-topic    Partition: 0    Leader: 2    Replicas: 1,2,0    Isr: 2,0
```

而且现在消息的消费仍然能正常进行, 即使原来负责写的节点已经失效了.

```
> bin/kafka-console-consumer.sh --zookeeper localhost:2181 --from-beginning --topic my-replicated-topic
...
my test message 1
my test message 2
```

## 4 最后

* 因为上面这三个框架都是 Java 的, 所以可以调整 Java 堆大小以优化上面这些程序的运行. Java 堆太小会导致程序难以运行; Java 堆太大(超出物理内存)会导致程序被交换到磁盘, 性能急剧降低. 例如: 4 G 内存的专用服务器可以分配 3 G 的 Java 堆, 最好的建议是运行负载测试, 然后确保远低于会导致系统交换的堆大小.
