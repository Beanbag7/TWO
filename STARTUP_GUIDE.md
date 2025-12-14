# 项目启动指南

## 前置要求

- **Java JDK 17+** (Spring Boot 后端需要)
- **Node.js 18+** (Vue 3 + Vite 前端需要)
- **MySQL 8.0+**

## 1. 数据库设置

1.  创建一个名为 `equipmentloansystem` 的 MySQL 数据库。
2.  导入位于项目根目录的 SQL 文件：`修复设备借用系统外键约束问题.sql`。
    *   **注意**：此文件包含数据插入和修复操作（`TRUNCATE` 和 `INSERT`），但**不包含 `CREATE TABLE` 建表语句**。
    *   请确保您的数据库中已经存在所需的表（如 `users`, `equipments`, `borrow_records` 等）。
    *   如果您是从零开始配置且没有表结构定义，您需要先根据 Java 实体类创建相应的数据库表。
3.  验证您的数据库凭据是否与 `backend/src/main/resources/application.yml` 中的后端配置匹配：
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/equipmentloansystem...
        username: root
        password: 123456
    ```
    *   如果您的密码不同，请修改此文件。

## 2. 后端启动 (Spring Boot)

1.  打开终端并进入 `backend` 目录。
2.  使用 Maven 运行应用程序：
    ```bash
    mvn spring-boot:run
    ```
3.  或者，将 `backend` 文件夹导入 IntelliJ IDEA 或 Eclipse 并运行主应用程序类。
4.  后端将在 **http://localhost:8080** 启动。

## 3. 前端启动 (Vue 3)

1.  打开一个新的终端并进入 `frontend` 目录。
2.  安装依赖（仅首次运行时需要）：
    ```bash
    npm install
    ```
3.  启动开发服务器：
    ```bash
    npm run dev
    ```
4.  前端通常将在 **http://localhost:5173** 启动（具体请查看终端输出）。

## 4. 访问应用程序

- 打开浏览器并访问前端 URL（例如 `http://localhost:5173`）。
- 确保后端正在运行以处理 API 请求。
