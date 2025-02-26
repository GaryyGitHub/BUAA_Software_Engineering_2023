# BiliMili视频平台

2024年春季学期**软件工程基础**课程 & 2024年夏季**软件工程基础实践**课程大作业——BiliMili视频平台

在春季学期，我们完成了BiliMili视频平台的开发；在夏季学期使用Docker+K8S部署并拆分微服务，最终实现了系统的高性能上线运行。

## 项目简介

本项目是一个类似于Bilibili的视频平台，支持用户上传、播放、评论视频等功能。项目采用Spring Boot微服务架构，结合Nacos、Elasticsearch、MySQL等技术，具备弹性扩展、服务降级等云原生特性。

## 技术栈

- **Spring Boot**: 2.7.15
- **Spring Cloud**: 2021.0.5
- **Spring Cloud Alibaba**: 2021.0.5.0
- **Nacos**: 2.4.1 (服务发现与配置中心)
- **MySQL**: 8.0.31 (数据库)
- **Elasticsearch**: 8.15.0 (全文检索)
- **MyBatis Plus**: 3.5.3 (ORM框架)
- **Docker**: 容器化部署
- **Kubernetes (K8S)**: 用于服务的部署和管理
- **Sentinel**: 服务限流与熔断
- **Nginx**: 前端服务
- **GitHub Actions**: CI/CD流水线

## 部署与启动

### 1. 克隆项目代码

#### 单体系统

```sh
# 后端
git clone https://gitee.com/Gary_BUAA/bilimili_backend_gary.git
# 前端
git clone https://gitee.com/Gary_BUAA/bilimili_frontend_gary.git
# 管理员端
git clone https://gitee.com/Gary_BUAA/bilimili_admin_gary.git
```

#### 微服务

```bash
# 后端
git clone https://github.com/RoRoily/BiliMili_backend_V3.git
# 前端
git clone https://github.com/PengXinyang/hhjt-frontend-client-master.git
# 管理员端
git clone https://github.com/PengXinyang/hhjt-frontend-admin-master.git
```

### 2. 配置环境变量

#### 单体系统

**前端、管理员端**：若要用Docker启动，`vue.config.js`中的`target`、`nginx.conf`中的`location`url需要更换为后端容器名；若要用K8S启动，则需要更换为后端服务名。

**后端**：将`application.yml`中对应的数据库url更改为自己的远程或本地数据库，注意还要设置`username`、`account`、`password`（创建数据库的sql文件为database/bilimili.sql）

#### 微服务

**前端、管理员端**：同单体系统，需要注意后端提供服务的端口由7070变为9090

**后端**：根据项目需求，修改各微服务的`bootstrap.yml`文件，确保MySQL、Elasticsearch和Nacos的连接信息正确。Nacos上需要编写yaml给出对应服务的配置。给出示例yaml如下：

```yaml
# video-service-dev.yaml
server:
  port: 8092
  tomcat:
    uri-encoding: UTF-8
    connection-timeout: 20000
    keep-alive-timeout: 60000   # 不知道为什么connection: keep-alive没生效，一直是close
    threads:
      max: 400
      min-spare: 10
spring:
  datasource:
    # 使用druid数据库连接池
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 使用Unicode字符集、指定字符编码为UTF-8、禁用SSL连接、允许多个查询在一次请求中执行
    url: jdbc:mysql://localhost:3307/VIDEOSERVICE?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&&allowPublicKeyRetrieval=true
    username: root
    account: root
    password: BUAA13Soft
    # Spring Boot 默认是不注入这些属性值的，需要自己绑定
    # druid 数据源专有配置
    # 初始建立的连接数，因为连接远程数据库久不使用会失效，所以按回默认的初始0条就好了
    initialSize: 0
    # 连接池中的最小空闲连接数 默认是0 虽然建立连接会耗时，但是使用远程数据库的话，为了不保留失活连接，还是按回默认0条空闲好了
    minIdle: 0
    # 最大活动连接数
    maxActive: 20
    # 如果连接池中的所有连接都已被占用，请求新连接的线程将最多等待60秒，然后会抛出异常
    maxWait: 60000
    # 空闲连接的存活时间为1分钟，默认是30分钟，设1分钟是因为远程数据库3~4分钟就自动失效，如果连接不断开就会卡住20秒，严重影响查询体验
    minEvictableIdleTimeMillis: 60000
    # 每隔1分钟就检查一次，回收超过最小空闲时间的空闲连接
    timeBetweenEvictionRunsMillis: 30000
    # 验证连接的查询语句
    validationQuery: SELECT 1 FROM DUAL
    # 设置在连接空闲时是否执行验证查询语句，如果设置为 true，则连接池会在空闲时定期执行 validationQuery 验证连接的有效性。
    testWhileIdle: true
    # 设置在从连接池中获取连接时是否执行验证查询语句
    testOnBorrow: false
    # 设置在归还连接到连接池时是否执行验证查询语句
    testOnReturn: false
    # 设置是否缓存 PreparedStatement，默认为 true，即缓存 PreparedStatement，提高性能
    poolPreparedStatements: true
    # 配置监控统计拦截的filters，stat:监控统计、log4j：日志记录、wall：防御sql注入
    # 如果允许时报错  java.lang.ClassNotFoundException: org.apache.log4j.Priority
    # 则导入 log4j 依赖即可，Maven 地址：https://mvnrepository.com/artifact/log4j/log4j
    filters: stat,wall,log4j
    # 每个连接在缓存 PreparedStatement 的最大数量
    maxPoolPreparedStatementPerConnectionSize: 20
    # 是否启用全局统计。如果设置为 true，则 Druid 的监控统计功能将会全局启用
    useGlobalDataSourceStat: true
    # 连接属性配置，druid.stat.mergeSql=true 表示是否合并 SQL；druid.stat.slowSqlMillis=500 表示慢 SQL 的阈值，单位为毫秒
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500

  servlet:
    multipart:
      # 限制单个文件的上传大小
      max-file-size: 30MB
      max-request-size: 30MB
      # 设置懒解析，提升上传性能
      resolve-lazily: true
  redis:
    # Redis数据库索引（默认为0）
    database: 0
    # Redis服务器地址
    #host: bilimili-redis
    host: localhost
    # Redis服务器连接端口
    #port: 6379
    port: 6380
    password: BUAA13Soft
    jedis:
      # 连接池是为了避免频繁地创建和销毁Redis连接，以提高性能
      pool:
        # 连接池中的最小空闲连接
        min-idle: 10
        # 连接池中的最大空闲连接，较大的值可以支持更多的并发连接，但也会占用更多的资源
        max-idle: 10
        # 连接池最大连接数（使用负值表示没有限制）
        max-active: 100
        # 连接池最大阻塞等待时间（使用负值表示没有限制）
        max-wait: -1
        # 从池中借用连接时，验证连接是否仍然有效 默认false
        testOnBorrow: true
        # 定期对空闲连接进行验证，以确保它们仍然有效 默认false
        testWhileIdle: true
        # 连接在池中空闲的时间超过这个值，那么它可能会被连接池回收 默认60000 即60秒
        minEvictableIdleTimeMills: 60000
        # 定期运行空闲连接回收的时间间隔，查找并回收那些空闲时间超过 minEvictableIdleTimeMills 的连接 默认30000 即30秒
        timeBetweenEvictionRunsMillis: 30000
        # 每次运行连接回收时要测试的连接数，-1表示测试所有的空闲连接
        numTestsPerEvictionRun: -1
    # 连接超时时间（毫秒）
    timeout: 5000

directory:
  # 投稿视频存储目录
  video: public/video/
  # 分片存储目录
  chunk: public/chunk/
  # 投稿封面存储目录
  cover: public/img/cover/
  # 文章存储目录
  #article: public/article

oss:
  # 对象存储桶的名字
  bucket: bilimili
  # 外网访问的域名，记得最后面的"/"
  bucketUrl: https://bilimili.oss-cn-beijing.aliyuncs.com/
  # 地域节点
  endpoint: http://oss-cn-beijing.aliyuncs.com
  # 有访问权限的用户的 access-key-id
  keyId:
  # 有访问权限的用户的 access-key-Secret
  keySecret:
  # ossClient实例维持的空闲时间，单位毫秒，超过会自动关闭释放资源
  idleTimeout: 10000
  
elasticsearch:
  # elasticsearch服务地址
  #host: bilimili-elasticsearch
  host: localhost
  port: 9200
  username: elastic
  password: BUAA13Soft
```



### 3. 构建与启动项目

#### 3.1 使用Docker启动各服务
项目中的所有服务均已容器化，确保安装了Docker和Docker Compose后，使用以下命令构建并启动服务：

```bash
docker-compose up --build
```

#### 3.2 使用Kubernetes部署
项目已经支持Kubernetes部署，执行以下步骤：

1. 构建Docker镜像并推送到容器仓库（如Docker Hub）。
2. 编写Kubernetes部署文件，配置Deployment和Service资源。
3. 使用`kubectl`命令部署：
   ```bash
   kubectl apply -f k8s-deployment.yml
   ```

### 4. 服务验证
- 确保所有服务已经成功启动，通过Nacos控制台或`kubectl get pods`查看服务状态。
- 使用Postman或Apifox进行API接口测试。

## 云原生特性
项目通过Kubernetes的HPA（Horizontal Pod Autoscaler）实现自动扩缩容，并通过Sentinel进行服务限流和降级策略。使用JMeter进行性能测试，验证系统在高负载下的稳定性。

## 相关链接
- [Spring Cloud Alibaba](https://spring.io/projects/spring-cloud-alibaba)
- [Nacos](https://nacos.io/)
- [MyBatis Plus](https://mybatis.plus/)

