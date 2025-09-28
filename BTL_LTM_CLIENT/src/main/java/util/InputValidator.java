package util;

public class InputValidator {
    
    private static final int MIN_USERNAME_LENGTH = 1;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 1;
    private static final int MAX_PASSWORD_LENGTH = 50;
    
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Username không được để trống");
        }
        
        String trimmed = username.trim();
        if (trimmed.length() < MIN_USERNAME_LENGTH) {
            return new ValidationResult(false, "Username phải có ít nhất " + MIN_USERNAME_LENGTH + " ký tự");
        }
        
        if (trimmed.length() > MAX_USERNAME_LENGTH) {
            return new ValidationResult(false, "Username không được quá " + MAX_USERNAME_LENGTH + " ký tự");
        }
        
        if (!trimmed.matches("^[a-zA-Z0-9_]+$")) {
            return new ValidationResult(false, "Username chỉ được chứa chữ cái, số và dấu gạch dưới");
        }
        
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Password không được để trống");
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return new ValidationResult(false, "Password phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return new ValidationResult(false, "Password không được quá " + MAX_PASSWORD_LENGTH + " ký tự");
        }
        
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult validateLogin(String username, String password) {
        ValidationResult usernameResult = validateUsername(username);
        if (!usernameResult.isValid()) {
            return usernameResult;
        }
        
        ValidationResult passwordResult = validatePassword(password);
        if (!passwordResult.isValid()) {
            return passwordResult;
        }
        
        return new ValidationResult(true, "");
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
