# Flow Đăng Nhập, Đăng Ký, Đăng Xuất và Get Users Online

## 1. Flow Đăng Ký (Register)

### Client Side:

1. **UI/Register.java** - User nhập username/password và click "Đăng ký"
2. **InputValidator.java** - Validate input (kiểm tra format username/password)
3. **AuthController.java** - `handleRegister()`:
   - Gửi message `REGISTER|username|password` đến server
   - Trả về "SENT" nếu thành công
4. **Client.java** - Nhận response từ server
5. **EventHandler.java** - `handleRegisterResponse()`:
   - `REGISTER|REGISTED|username` → Chuyển đến màn hình Home
   - `REGISTER|EXIST|username` → Hiển thị lỗi user đã tồn tại
   - `REGISTER|INVALID|username` → Hiển thị lỗi format không hợp lệ

### Server Side:

1. **ClientHandler.java** - Nhận message `REGISTER|username|password`
2. **AuthHandler.java** - `handleRegister()`:
   - Kiểm tra format message (phải có ít nhất 3 phần)
   - Gọi `userDao.createUser(username, password)`
   - Trả về `REGISTER|REGISTED|username` nếu thành công
   - Trả về `REGISTER|EXIST|username` nếu user đã tồn tại
3. **ClientHandler.java** - Nếu đăng ký thành công:
   - Gọi `homeHandler.broadcastNewUserRegistered(username)`
   - Broadcast `NEW_USER_REGISTERED|username` cho tất cả client khác

### Broadcast khi có user mới đăng ký:

1. **HomeHandler.java** - `broadcastNewUserRegistered()`:
   - Lấy danh sách tất cả client đang online từ `SocketController.getUsersClients()`
   - Gửi event `NEW_USER_REGISTERED|username` cho từng client
2. **Client.java** - Nhận event `NEW_USER_REGISTERED`
3. **EventHandler.java** - `handleNewUserRegistered()`:
   - Tự động gọi `getUsersOnline()` để refresh danh sách users
4. **HomeController.java** - `getUsersOnline()`:
   - Gửi request `GET_USERS_ONLINE` đến server
5. **Server** - Trả về danh sách users mới cập nhật
6. **UI** - Tự động refresh danh sách users

## 2. Flow Đăng Nhập (Login)

### Client Side:

1. **UI/Login.java** - User nhập username/password và click "Đăng nhập"
2. **InputValidator.java** - Validate input (kiểm tra format username/password)
3. **AuthController.java** - `handleLogin()`:
   - Gửi message `LOGIN|username|password` đến server
   - Trả về "SENT" nếu thành công
4. **Client.java** - Nhận response từ server
5. **EventHandler.java** - `handleLoginResponse()`:
   - `LOGIN|LOGGEDIN|username` → Chuyển đến màn hình Home
   - `LOGIN|FAILLOGIN|username` → Hiển thị lỗi đăng nhập thất bại

### Server Side:

1. **ClientHandler.java** - Nhận message `LOGIN|username|password`
2. **AuthHandler.java** - `handleLogin()`:
   - Kiểm tra format message (phải có ít nhất 3 phần)
   - Gọi `userDao.verifyLogin(username, password)`
   - Nếu đúng: thêm user vào `SocketController.loggedInUsers`
   - Trả về `LOGIN|LOGGEDIN|username` nếu thành công
   - Trả về `LOGIN|FAILLOGIN|username` nếu thất bại
3. **ClientHandler.java** - Nếu đăng nhập thành công:
   - Broadcast `USER_STATUS|username|ONLINE` cho tất cả client khác

### Broadcast khi user đăng nhập:

1. **HomeHandler.java** - `broadcastUserStatus()`:
   - Gửi event `USER_STATUS|username|ONLINE` cho tất cả client khác
2. **Client.java** - Nhận event `USER_STATUS`
3. **EventHandler.java** - `handleUserStatus()`:
   - Tự động gọi `getUsersOnline()` để refresh danh sách users

## 3. Flow Đăng Xuất (Logout)

### Client Side:

1. **UI/Home.java** - User click nút "Đăng xuất"
2. **AuthController.java** - `handleLogout()`:
   - Gửi message `LOGOUT` đến server
   - Trả về "SENT" nếu thành công
3. **Client.java** - Nhận response từ server
4. **EventHandler.java** - `handleLogoutResponse()`:
   - `LOGOUT|username` → Chuyển về màn hình Login

### Server Side:

1. **ClientHandler.java** - Nhận message `LOGOUT`
2. **AuthHandler.java** - `handleLogout()`:
   - Lấy username từ `SocketController.getUserByClient()`
   - Xóa user khỏi `SocketController.loggedInUsers`
   - Trả về response `LOGOUT|username`
3. **ClientHandler.java** - Sau khi xử lý:
   - Broadcast `USER_STATUS|username|OFFLINE` cho tất cả client khác

### Broadcast khi user đăng xuất:

1. **HomeHandler.java** - `broadcastUserStatus()`:
   - Gửi event `USER_STATUS|username|OFFLINE` cho tất cả client khác
2. **Client.java** - Nhận event `USER_STATUS`
3. **EventHandler.java** - `handleUserStatus()`:
   - Tự động gọi `getUsersOnline()` để refresh danh sách users

## 4. Flow Get Users Online

### Client Side:

1. **UI/Home.java** - Khi vào màn hình Home hoặc click "Làm mới"
2. **HomeController.java** - `getUsersOnline()`:
   - Gửi request `GET_USERS_ONLINE` đến server
3. **EventHandler.java** - `parseUsersOnline()`:
   - Nhận response `USER_ONLINE|onlineUsers|allUsers`
   - Parse danh sách users online và tất cả users
   - Gọi `homeController.onUsersOnlineReceived(users, allUsers)`
4. **UsersListPanel.java** - `setUsers()`:
   - Hiển thị danh sách users online (có nút "Solo")
   - Hiển thị danh sách tất cả users (chỉ hiển thị tên)

### Server Side:

1. **ClientHandler.java** - Nhận message `GET_USERS_ONLINE`
2. **HomeHandler.java** - `getUserOnl()`:
   - Lấy danh sách users đang online từ `SocketController.getLoggedInUsers()`
   - Lấy danh sách tất cả users từ database qua `userDao.getAllUsers()`
   - Loại bỏ user hiện tại khỏi cả hai danh sách
   - Trả về response `USER_ONLINE|onlineUsers|allUsers`

## 5. Luồng Broadcast Tự Động

### Khi có user mới đăng ký:

```
User A đăng ký → Server xử lý → Broadcast NEW_USER_REGISTERED
→ Tất cả client khác nhận event → Tự động gọi getUsersOnline()
→ Server trả về danh sách mới → UI refresh
```

### Khi user đăng nhập:

```
User A đăng nhập → Server xử lý → Broadcast USER_STATUS|A|ONLINE
→ Tất cả client khác nhận event → Tự động gọi getUsersOnline()
→ Server trả về danh sách mới → UI refresh
```

### Khi user đăng xuất:

```
User A đăng xuất → Server xử lý → Broadcast USER_STATUS|A|OFFLINE
→ Tất cả client khác nhận event → Tự động gọi getUsersOnline()
→ Server trả về danh sách mới → UI refresh
```

## 6. Các File Liên Quan

### Client Side:

- `UI/Login.java` - Màn hình đăng nhập
- `UI/Register.java` - Màn hình đăng ký
- `UI/Home.java` - Màn hình chính
- `UI/UsersListPanel.java` - Panel hiển thị danh sách users
- `controller/AuthController.java` - Controller xử lý auth
- `controller/HomeController.java` - Controller xử lý home
- `handler/EventHandler.java` - Xử lý events từ server (bao gồm `handleNewUserRegistered()`)
- `org/example/Client.java` - Client chính (có case "NEW_USER_REGISTERED")

### Server Side:

- `server/ClientHandler.java` - Xử lý từng client (có logic broadcast khi đăng ký thành công)
- `handler/AuthHandler.java` - Xử lý đăng nhập/đăng ký/đăng xuất
- `handler/HomeHandler.java` - Xử lý get users online và broadcast (có `broadcastNewUserRegistered()`)
- `util/SocketController.java` - Quản lý danh sách users đang online
- `db/UserDao.java` - Truy cập database

## 7. Các Message Protocol

### Client → Server:

- `REGISTER|username|password`
- `LOGIN|username|password`
- `LOGOUT`
- `GET_USERS_ONLINE`

### Server → Client:

- `REGISTER|REGISTED|username` / `REGISTER|EXIST|username`
- `LOGIN|LOGGEDIN|username` / `LOGIN|FAILLOGIN|username`
- `LOGOUT|username`
- `USER_ONLINE|onlineUsers|allUsers`
- `NEW_USER_REGISTERED|username` (broadcast khi có user mới đăng ký)
- `USER_STATUS|username|ONLINE|OFFLINE` (broadcast khi user đăng nhập/đăng xuất)
