# 前后端交互流程深度剖析 (Deep Dive Technical Analysis)

这份文档是对系统请求处理流程的深度技术剖析。为了同时展示**核心业务流程**与**Spring Validation 参数校验机制**，我们选取最具代表性的 **“用户登录” (User Login)** 功能作为案例。

---

## 流程概览 (Flow Overview)
1.  **View (Vue)**: 收集用户名密码 -> 2. **Network (Axios)**: 发送 HTTP POST -> 3. **Controller (Spring MVC)**: **参数校验**与接收 -> 4. **Service**: 业务逻辑处理 -> 5. **Repository (JDBC)**: 数据库查询 -> 6. **Database (MySQL)**: 能够匹配记录 -> 7. **Result**: 返回 Token 或用户信息。

---

## 1. 前端视图层 (View Layer)
**组件**: `frontend/src/views/Login.vue`

当用户在登录界面输入工号和密码并点击“登录”按钮时，前端会组装数据对象。

**数据快照 (Data Snapshot):**
```typescript
{
    username: "1001",       // 用户输入的工号
    password: ""            // 假设用户忘记输入密码（用于演示校验）
}
```

## 2. 网络传输层 (Network Layer)
前端 Axios 将数据序列化为 JSON 字符串，并发起网络请求。

*   **URL**: `http://localhost:8080/api/users/login`
*   **Method**: `POST`
*   **Body**: `{"username": "1001", "password": ""}`

## 3. 后端控制层 (Controller Layer) - 核心校验点
**类**: `com.example.equipment.controller.UserController`

Spring Boot 接收请求。在进入业务逻辑前，**Hibernate Validator** 会首先介入工作。

**代码解析:**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 关键点：@Valid 注解触发验证机制
    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody LoginRequest request) throws Exception {
        // 只有当 request 对象通过了所有的校验规则，才会执行这一行
        User user = userService.login(request);
        return Result.success(user);
    }
}
```

**DTO 定义 (`LoginRequest.java`):**
```java
public class LoginRequest {
    @NotBlank(message = "Username cannot be empty") // 规则 1
    private String username;

    @NotBlank(message = "Password cannot be empty") // 规则 2
    private String password;
}
```

**验证逻辑**:
*   系统检测到 `password` 字段为空字符串。
*   触发 `@NotBlank(message = "Password cannot be empty")` 规则。
*   **Controller 直接抛出 `MethodArgumentNotValidException` 异常，中断流程。**
*   全局异常处理器捕获该异常，并返回 400 Bad Request 给前端，提示 "Password cannot be empty"。

*(若数据正常，则继续向下执行)*

## 4. 业务逻辑层 (Service Layer)
**类**: `com.example.equipment.service.UserService`

假设数据通过了校验，Service 层负责核心业务判断。

**代码解析:**
```java
public User login(LoginRequest req) throws Exception {
    // 1. 调用 Repository 查询数据库是否存在该用户
    User user = userRepository.findByUsername(req.getUsername());
    
    // 2. 业务判断：用户不存在？
    if (user == null) {
        throw new Exception("User not found");
    }
    
    // 3. 业务判断：密码错误？
    if (!user.getPassword().equals(req.getPassword())) {
        throw new Exception("Invalid password");
    }
    
    return user; // 登录成功
}
```

## 5. 数据持久层 (Repository Layer)
**类**: `com.example.equipment.repository.UserRepository`

Repository 层负责与数据库的直接对话。

**代码解析:**
```java
public User findByUsername(String username) throws SQLException {
    String sql = "SELECT * FROM users WHERE username = ?";
    try (Connection conn = jdbcUtils.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, username); // 安全设置参数
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                // 将数据库结果集行映射为 Java User 对象
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                // ...
                return u;
            }
        }
    }
    return null;
}
```

## 6. 数据库层 (Database Layer)
MySQL 执行查询：
```sql
SELECT * FROM users WHERE username = '1001';
```

## 7. 响应返回 (Return Trip)
验证通过且密码正确后：
1.  **Repository** 返回 User 对象。
2.  **Service** 确认无误，返回 User 对象。
3.  **Controller** 包装为 `Result.success(user)`。
4.  **前端** 接收到 JSON 响应，将用户信息存入 `localStorage`，并跳转到首页。

---
**总结**：
*   **Request Flow**: View -> Network -> Controller -> Service -> Repository -> Database.
*   **Validation**: 也就是在 Controller 层接收数据时，利用 `@Valid` 和 DTO 上的注解（如 `@NotBlank`）进行拦截。这是 Spring 生态中最标准的参数校验方式，能有效将脏数据阻挡在业务逻辑之外。
