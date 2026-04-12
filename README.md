# 网上图书系统 (Online Bookstore System)

一个基于Spring Boot和Vue.js的前后端分离网上图书系统，支持图书浏览、搜索、购买、评价以及大数据个性化推荐等功能。

## 项目简介

本项目是一个功能完善的电子商务图书销售平台，采用现代化的技术栈构建，提供用户友好的购物体验和完整的后台管理功能。

### 主要功能

- 用户模块：注册、登录、个人信息管理、收货地址管理
- 图书模块：图书浏览、搜索、分类、详情展示、评价
- 购物车模块：添加、修改、删除商品，购物车结算
- 订单模块：订单创建、订单管理、订单状态跟踪、评价
- 大数据推荐模块：基于协同过滤和内容的个性化推荐
- 后台管理模块：用户管理、图书管理、订单管理、数据统计

## 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 21 | Java开发环境 |
| Spring Boot | 3.2.2 | 核心框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis | 3.0.3 | 持久层框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 8.2.3 | 缓存数据库 |
| JWT | 0.12.3 | 身份认证 |
| Knife4j | 4.4.0 | API文档 |
| Maven | 3.9.x | 项目构建工具 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.4.15 | 前端框架 |
| Vue Router | 4.2.5 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| Element Plus | 2.5.3 | UI组件库 |
| Axios | 1.6.5 | HTTP客户端 |
| Vite | 5.0.11 | 构建工具 |
| Sass | 1.70.0 | CSS预处理器 |

## 项目结构

```
test_trae/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bookstore/
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── mapper/      # 数据访问层
│   │   │   │   ├── service/     # 业务逻辑层
│   │   │   │   └── BookstoreApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   ├── pom.xml
│   └── uploads/             # 上传文件目录
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API接口
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 组件
│   │   ├── router/          # 路由
│   │   ├── store/           # 状态管理
│   │   ├── views/           # 页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── database/                # 数据库脚本
│   ├── init.sql             # 数据库初始化脚本
│   └── bookstore_complete.sql
├── docs/                    # 项目文档
├── images/                  # 项目图片资源
└── README.md
```

## 快速开始

### 环境要求

- JDK 21
- Node.js 18+
- MySQL 8.0+
- Redis 7+
- Maven 3.9+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p bookstore < database/init.sql
```

3. 默认管理员账号：
- 用户名：admin
- 密码：123456

### 后端启动

1. 进入后端目录：
```bash
cd backend
```

2. 修改配置文件 `src/main/resources/application.yml` 中的数据库和Redis连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bookstore
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

3. 启动后端服务：
```bash
mvn spring-boot:run
```

后端服务将在 http://localhost:8080/api 启动

API文档地址：http://localhost:8080/api/doc.html

### 前端启动

1. 进入前端目录：
```bash
cd frontend
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

前端服务将在 http://localhost:5173 启动

4. 构建生产版本：
```bash
npm run build
```

## 主要功能模块

### 用户模块
- 用户注册/登录（JWT认证）
- 个人信息管理
- 头像上传
- 密码修改
- 收货地址管理

### 图书模块
- 图书列表展示
- 图书详情页
- 分类浏览
- 关键词搜索
- 图书评价和评分

### 购物车模块
- 添加商品到购物车
- 修改商品数量
- 删除购物车商品
- 购物车结算

### 订单模块
- 创建订单
- 订单列表查看
- 订单状态跟踪
- 订单取消
- 订单评价

### 推荐模块
- 基于用户的协同过滤推荐
- 基于物品的协同过滤推荐
- 基于内容的推荐
- 热门图书推荐

### 后台管理模块
- 用户管理
- 图书管理（添加、编辑、删除、上下架）
- 分类管理
- 订单管理
- 评价管理
- 销售数据统计

## 配置说明

### 后端配置（application.yml）

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| server.port | 后端服务端口 | 8080 |
| spring.datasource.url | 数据库连接URL | jdbc:mysql://localhost:3306/bookstore |
| spring.data.redis.host | Redis主机地址 | localhost |
| spring.data.redis.port | Redis端口 | 6379 |
| app.upload.path | 文件上传路径 | d:/TRAE/test_trae/backend/uploads/ |
| jwt.secret | JWT密钥 | bookstoreSecretKey2024VeryLongSecretKeyForJWTTokenGeneration |
| jwt.expiration | JWT过期时间（毫秒） | 86400000 |

### 前端配置

前端API请求地址配置在 `frontend/src/api/request.js` 中。

## 开发指南

### 后端开发

- 控制器层：`backend/src/main/java/com/bookstore/controller/`
- 服务层：`backend/src/main/java/com/bookstore/service/`
- 数据访问层：`backend/src/main/java/com/bookstore/mapper/`
- 实体类：`backend/src/main/java/com/bookstore/entity/`

### 前端开发

- 页面组件：`frontend/src/views/`
- 公共组件：`frontend/src/components/`
- API接口：`frontend/src/api/`
- 状态管理：`frontend/src/store/`
- 路由配置：`frontend/src/router/`

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交Issue
- 发送邮件

## 更新日志

### v1.0.0
- 完成基础功能开发
- 实现用户、图书、购物车、订单核心模块
- 实现大数据推荐功能
- 实现后台管理系统
- 优化评价管理模块，增加数据库级分页查询
