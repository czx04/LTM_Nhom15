# Game Đố Chữ - LTM

### 1. Chuẩn bị Database
- Tạm thời mới có 1 bảng
```bash
CREATE TABLE users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL);
```

### 2. Chạy Server
```bash
cd BTL_LTM_SERVER
mvn -q -DskipTests clean package && mvn exec:java
```

### 3. Chạy Client
```bash
cd BTL_LTM_CLIENT
mvn -q -DskipTests clean package && mvn exec:java
```


### Cấu hình
- Server: localhost:8081