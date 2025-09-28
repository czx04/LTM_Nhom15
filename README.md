# Game Đố Chữ - LTM

### 1. Chuẩn bị Database

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
