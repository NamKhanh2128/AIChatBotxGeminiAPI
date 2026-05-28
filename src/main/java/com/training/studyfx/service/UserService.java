package com.training.studyfx.service;

import com.training.studyfx.model.User;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    private static UserService instance;
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private User currentUser;

    private UserService() {
        // Tạo sẵn 1 tài khoản admin để test
        User admin = new User("admin", "123456");
        admin.setFullName("Administrator");
        admin.setEmail("admin@studyfx.com");
        admin.setStatus("Available");
        users.put("admin", admin);

        System.out.println("UserService initialized (in-memory)");
        System.out.println("Default account: admin / 123456");
    }

    public static UserService getInstance() {
        if (instance == null)
            instance = new UserService();
        return instance;
    }

    /**
     * Đăng nhập
     * - Nếu tài khoản tồn tại và đúng mật khẩu -> login thành công
     * - Nếu tài khoản KHÔNG tồn tại -> tự động tạo tài khoản mới và login
     */
    public boolean login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        User user = users.get(username);

        if (user != null) {
            // Tài khoản tồn tại -> kiểm tra mật khẩu
            if (user.getPassword().equals(password)) {
                currentUser = user;
                System.out.println("Login success: " + username);
                return true;
            } else {
                System.out.println("Wrong password for: " + username);
                return false;
            }
        } else {
            // Tài khoản chưa tồn tại -> tự động đăng ký
            User newUser = new User(username, password);
            newUser.setFullName(username);
            newUser.setStatus("Available");
            users.put(username, newUser);
            currentUser = newUser;
            System.out.println("Auto-registered & login: " + username);
            return true;
        }
    }

    /**
     * Đăng ký
     * - Nếu username đã tồn tại -> trả về false
     * - Nếu chưa tồn tại -> tạo mới
     */
    public boolean register(String username, String password, String email) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        if (users.containsKey(username)) {
            System.out.println("Username already exists: " + username);
            return false;
        }

        User newUser = new User(username, password);
        newUser.setEmail(email);
        newUser.setFullName(username);
        newUser.setStatus("Available");
        users.put(username, newUser);
        currentUser = newUser;
        System.out.println("Register success: " + username);
        return true;
    }

    /**
     * Cập nhật profile
     */
    public void updateUserProfile(String fullName, String status, String imagePath) {
        if (currentUser == null)
            return;
        currentUser.setFullName(fullName);
        currentUser.setStatus(status);
        if (imagePath != null)
            currentUser.setProfileImagePath(imagePath);
        System.out.println("Profile updated for: " + currentUser.getUsername());
    }

    /**
     * Cập nhật ảnh đại diện
     */
    public void updateProfileImage(String path) {
        if (currentUser == null)
            return;
        currentUser.setProfileImagePath(path);
    }

    /**
     * Lấy user hiện tại
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Đăng xuất
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("Logout: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    /**
     * Kiểm tra username tồn tại
     */
    public boolean isUsernameExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Lấy tổng số user
     */
    public int getUserCount() {
        return users.size();
    }

    /**
     * Không cần close vì không dùng database
     */
    public void closeConnection() {
        // Không làm gì
    }
}