# 项目综合指南：启动、流程与技术细节

## 1. 项目概况
**设备借用系统 (Equipment Loan System)** 是一套全栈 Web 应用，旨在高效管理设备库存、预约申请及借还流程。系统分为用户端和管理端，支持设备查询、预约、审批、借用、归还及维修等全生命周期管理。

## 2. 启动流程 (Startup Flow)

### 2.1 环境准备
- **JDK 17+**
- **Node.js 18+**
- **MySQL 8.0+**

### 2.2 数据库初始化
这是项目运行的第一步。我们提供了一键初始化脚本。
1.  找到根目录下的 `database_init.sql` 文件。
2.  在终端运行：
    ```bash
    mysql -u root -p < database_init.sql
    ```
    *注意：此操作会重建 `equipmentloansystem` 数据库并填充测试数据。*

### 2.3 后端启动 (Backend)
1.  进入 `backend` 目录。
2.  运行 Spring Boot 应用：
    ```bash
    mvn spring-boot:run
    ```
3.  服务将在端口 **8080** 启动。

### 2.4 前端启动 (Frontend)
1.  进入 `frontend` 目录。
2.  安装依赖（首次运行）：
    ```bash
    npm install
    ```
3.  启动开发服务器：
    ```bash
    npm run dev
    ```
4.  访问地址通常为 **http://localhost:5173**。

## 3. 核心业务处理流程 (Processing Flow)

### 3.1 用户登录流程
1.  **前端提交**：用户输入工号和密码。
2.  **后端校验**：Spring Validation (`@Valid`) 拦截请求，检查参数非空。
3.  **业务验证**：Service 层查询数据库验证用户名和密码。
4.  **结果返回**：验证通过返回用户信息，前端存储并跳转。

### 3.2 设备预约与借还流程
这是一个典型的状态流转过程：

1.  **提交预约 (Submit)**: 用户选择设备和时间段，提交预约申请 -> 状态：`待审核`。
2.  **审批 (Approval)**:
    *   管理员通过 -> 状态：`已批准` (生成批准记录)。
    *   管理员拒绝 -> 状态：`已拒绝` (流程结束)。
3.  **借出 (Checkout)**: 用户并在预约时间领取设备，管理员确认 -> 系统生成 `borrow_records`，更新设备状态为 `借出`。
4.  **归还 (Return)**:
    *   用户归还设备 -> 管理员检查无误 -> 更新借用记录为 `已归还`，设备状态回滚为 `可用`。
    *   即时损坏 -> 管理员登记 `damage_records` -> 设备状态变更为 `维护中`。

## 4. 技术细节 (Technical Details)

### 4.1 技术栈 (Tech Stack)

| 领域     | 技术/库               | 说明                                           |
| :------- | :-------------------- | :--------------------------------------------- |
| **前端** | **Vue 3**             | 使用 Composition API，灵活高效                 |
|          | **Vite**              | 极速构建工具                                   |
|          | **Naive UI**          | 企业级 UI 组件库，深度定制主题                 |
|          | **Tailwind CSS**      | 原子化 CSS，快速构建响应式界面                 |
|          | **Pinia**             | 状态管理（用户 Session、主题偏好）             |
| **后端** | **Spring Boot 3**     | 核心开发框架                                   |
|          | **JDBC (Raw)**        | 原生 JDBC 封装 (JDBCUtils)，追求极致性能与控制 |
|          | **Spring Validation** | 参数校验标准                                   |
|          | **MySQL**             | 关系型数据库                                   |

### 4.2 数据库设计 (Schema Design)
核心表结构设计如下：
- **users**: 存储用户信息及角色（管理员/师生）。
- **equipments**: 存储设备详情、状态及库存位置。
- **reservations**: 记录预约申请及审批状态。
- **borrow_records**: 实际的借出和归还时间记录。
- **damage/maintenance_records**: 追踪设备损坏与维修历史，形成设备生命周期闭环。

### 4.3 关键技术实现
- **全局异常处理**: 通过 `GlobalExceptionHandler` 统一捕获 `MethodArgumentNotValidException` 等异常，返回标准化的 JSON 错误响应。
- **国际化 (i18n)**: 前后端配合，前端使用 `vue-i18n`，后端返回状态码对应的前端多语言键值，实现中英文无缝切换。
- **深色模式**: 基于 Tailwind CSS 的 `dark:` 类和 Naive UI 的主题配置，实现了系统级的深色模式支持。
