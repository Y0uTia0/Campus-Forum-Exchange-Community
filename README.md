# 我的项目

## 项目概述
这是一个基于Spring Boot和Vue.js的全栈Web应用程序。项目采用前后端分离的架构，包含后端API服务和前端单页应用。

## 技术栈

### 后端 (my-project-backend)
- **框架**: Spring Boot 3.4.3
- **语言**: Java 17
- **数据库**: MySQL
- **安全框架**: Spring Security
- **ORM/数据访问**: MyBatis-Plus
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **文件存储**: MinIO
- **其他技术**:
  - JWT认证
  - 邮件服务
  - FastJSON2
  - 数据验证

### 前端 (my-project-frontend)
- **框架**: Vue 3.5.13
- **构建工具**: Vite
- **UI库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **富文本编辑器**: Quill
- **样式处理**: Less

## 项目结构

### 后端结构
```
my-project-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── config/       # 配置类
│   │   │           ├── controller/   # 控制器
│   │   │           ├── entity/       # 实体类
│   │   │           ├── filter/       # 过滤器
│   │   │           ├── listener/     # 监听器
│   │   │           ├── mapper/       # MyBatis映射器
│   │   │           ├── service/      # 服务层
│   │   │           └── utils/        # 工具类
│   │   └── resources/                # 配置文件
│   └── test/                         # 测试代码
└── pom.xml                           # Maven配置
```

### 前端结构
```
my-project-frontend/
├── public/                           # 静态资源
├── src/
│   ├── assets/                       # 资源文件 
│   ├── components/                   # 公共组件
│   ├── net/                          # 网络请求
│   ├── router/                       # 路由配置
│   ├── store/                        # Pinia状态管理
│   ├── views/                        # 页面组件
│   ├── App.vue                       # 根组件
│   └── main.js                       # 入口文件
├── index.html                        # HTML模板
└── package.json                      # NPM配置
```

## 功能特点
- 用户认证与授权
- 数据持久化
- 缓存机制
- 消息队列集成
- 邮件服务
- 文件上传与存储
- 前端响应式界面
- 富文本编辑

## 开发指南

### 后端启动
```bash
cd my-project-backend
./mvnw spring-boot:run
```

### 前端启动
```bash
cd my-project-frontend
npm install
npm run dev
```

## 部署信息
前端构建：
```bash
cd my-project-frontend
npm run build
```

后端构建：
```bash
cd my-project-backend
./mvnw clean package
``` 