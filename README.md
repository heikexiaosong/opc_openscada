# opc_openscada

OPC Server 客户端DEMO



## 代码编译运行

​	本项目是基于Maven进行管理的项目.

​	为了方便使用，项目中依赖但是MAVEN远程库中没有的的jar包，在项目中会通过mvn指令把第三方的jar包打包到本地库中，然后和其他的jar库使用相同的步骤使用.

**NOTICE**: 首先应该确保机器上已经安装并配置好了 maven 和 jdk 环境(如果你要通过git命令来clone代码， 还需要安装git工具并开通github账号)。

步骤:

- `git clone https://github.com/heikexiaosong/opc_openscada`
- `cd opc_openscada`
- `mvn clean install -Dmaven.test.skip=true`



代码下载下来之后, 首先进入 项目根目录，执行命令(命令把项目依赖的独立第三方加入到maven本地库)

```shell
mvn clean install -Dmaven.test.skip=true
```

命令执行完成之后， 就可以用IDE导入项目，编辑并调试代码。




## OPC Server 测试环境搭建
测试环境OPC Server可以使用 [MatrikonOPC Simulation Server](https://www.matrikonopc.cn/products/opc-drivers/opc-simulation-server.aspx)
- MatrikonOPC模拟服务器是一个可以为您提供仿真OPC实时数据、OPC历史数据以及OPC报警事件信息的免费实用工具。
- MatrikonOPC模拟服务器可以生成随机值、线性值、阶梯值。同时, 该服务器还可提供了以“传递水桶式” 算法生成的数据项类, 可作为控制逻辑测试。





## OPC Server端的协议

OPC Server端目前常见的有以下几种协议:

- OPC DA: Data Access协议，是最基本的OPC协议。OPC DA服务器本身不存储数据，只负责显示数据收集点的当前值。客户端可以设置一个refresh interval，定期刷新这个值。目前常见的协议版本号为2.0和3.0，两个协议不完全兼容。也就是用OPC DA 2.0协议的客户端连不上OPC DA 3.0的Server
- OPC HDA: Historical Data Access协议。前面说过DA只显示当前状态值，不存储数据。而HDA协议是由数据库提供，提供了历史数据访问的能力。比如价格昂贵的Historian数据库，就是提供HDA协议接口访问OPC的历史数据。HDA的Java客户端目前我没找到免费的。
- OPC UA: Unified Architecture统一架构协议。诞生于2008年，摒弃了前面老的OPC协议繁杂，互不兼容等劣势，并且不再需要`COM`口访问，大大简化了编程的难度。基于OPC UA的开源客户端非常多。不过由于诞生时间较晚，目前在国内工业上未大规模应用，并且这个协议本身就跟旧的DA协议不兼容，客户端没法通用。

实际生产环境绝大多数都可能是OPC DA 2.0的Server，极个别可能有OPC DA 3.0



## OPC Server 访问库

[Utgard（开源）](http://openscada.org/projects/utgard/)

- OpenSCADA项目底下的子项目
- 纯Java编写，具有跨平台特性
- 全部基于`DCOM`实现
- 目前只支持DA 2.0协议，3.0协议的支持还在开发中

## Issue:
Access is denied error
When you get an

"Access is denied. [0x00000005]"
error, apply the following patch to the registry:

HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Policies\System
create or modify 32-bit DWORD: LocalAccountTokenFilterPolicy
set the value to: 1
Credit to Arturas Sirvinskas (comments below)