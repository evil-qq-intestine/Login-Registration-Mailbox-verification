用户注册登录系统

## 快速开始

1. 克隆项目：`git clone ...`
2. 复制 `application.example.properties` 为 `application.properties`
3. 在 `application.properties` 中填写你的邮箱 SMTP 信息（QQ邮箱需要授权码）
4. 运行 `mvn clean spring-boot:run`
5. 访问 `http://localhost:8080/register`

## 邮件配置说明

- 如果你使用 QQ 邮箱，请开启 SMTP 服务并生成授权码（不是登录密码）
- 其他邮箱（如 Gmail）请修改 `spring.mail.host` 和端口

## 技术栈

- Spring Boot 4.0.6
- SQLite
- Thymeleaf
- Bootstrap (可后续添加)
