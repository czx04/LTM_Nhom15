package util;

public class Constants {
    // Server Commands
    public static final String CMD_REGISTER = "REGISTER";
    public static final String CMD_LOGIN = "LOGIN";
    public static final String CMD_LOGOUT = "LOGOUT";
    public static final String CMD_GET_USERS_ONLINE = "GET_USERS_ONLINE";
    
    // Server Responses
    public static final String RESPONSE_LOGGEDIN = "LOGGEDIN";
    public static final String RESPONSE_REGISTED = "REGISTED";
    public static final String RESPONSE_EXIST = "EXIST";
    public static final String RESPONSE_LOGOUT = "LOGOUT";
    public static final String RESPONSE_FAIL = "fail";
    
    // UI Messages
    public static final String MSG_LOGIN_SUCCESS = "Đăng nhập thành công!";
    public static final String MSG_LOGIN_FAILED = "Sai username hoặc password!";
    public static final String MSG_REGISTER_SUCCESS = "Đăng ký thành công!";
    public static final String MSG_USER_EXISTS = "Username đã tồn tại";
    public static final String MSG_INVALID_FORMAT = "Sai format username hoặc password!";
    public static final String MSG_CONNECTION_ERROR = "Lỗi kết nối máy chủ";
    public static final String MSG_LOGOUT_FAILED = "Đăng xuất thất bại";
    public static final String MSG_LOAD_USERS_ERROR = "Lỗi tải danh sách người dùng";
    
    // UI Titles
    public static final String TITLE_WARNING = "Cảnh báo";
    public static final String TITLE_ERROR = "Lỗi";
    public static final String TITLE_GAME = "Game tính nhanh";
    
    // UI Dimensions
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 600;
    public static final int TEXT_FIELD_COLUMNS = 15;
    public static final int SPLIT_PANE_WEIGHT = 80; // percentage
    
    // Fonts
    public static final String FONT_FAMILY = "Arial";
    public static final int FONT_SIZE_TITLE = 22;
    public static final int FONT_SIZE_HEADER = 18;
    public static final int FONT_SIZE_BODY = 16;

}
