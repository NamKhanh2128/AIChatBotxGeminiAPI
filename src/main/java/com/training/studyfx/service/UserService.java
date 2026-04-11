package com.training.studyfx.service;
import com.training.studyfx.model.Message;
import com.training.studyfx.model.User;
import java.io.*;
import java.sql.*;

public class UserService {
    private static UserService instance;
    private User currentUser;
    private Connection connection;
    private final String DB_URL = "jdbc:sqlite:studyfx.db";

    private UserService() {
        initializeDatabase();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            // Create a connection to the database
            connection = DriverManager.getConnection(DB_URL);

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean register(String username, String password, String email) {
        try {
            // Check if username already exists
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT username FROM users WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Username already exists
                rs.close();
                checkStmt.close();
                return false;
            }
            rs.close();
            checkStmt.close();

            // Insert new user
            PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.executeUpdate();
            insertStmt.close();

            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Create user object from database data
                currentUser = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
                currentUser.setFullName(rs.getString("full_name"));
                currentUser.setStatus(rs.getString("status"));
                currentUser.setProfileImagePath(rs.getString("profile_image_path"));

                // Load user messages
                //loadUserMessages();

                rs.close();
                stmt.close();
                return true;
            }

            rs.close();
            stmt.close();
            return false;
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void updateUserProfile(String fullName, String status, String profileImagePath) {
        if (currentUser == null) return;

        try {
            // Nếu profileImagePath là đường dẫn mới (không phải đã lưu trong database)
            // và không bắt đầu bằng "upload/" thì cần sao chép vào thư mục upload
            String finalImagePath = profileImagePath;

            if (profileImagePath != null && !profileImagePath.isEmpty()
                    && !profileImagePath.startsWith("upload/")) {
                // Sao chép tệp vào thư mục upload
                boolean imageUpdated = updateProfileImage(profileImagePath);
                if (imageUpdated) {
                    // Sử dụng đường dẫn mới đã được cập nhật trong currentUser
                    finalImagePath = currentUser.getProfileImagePath();
                } else {
                    // Giữ nguyên đường dẫn cũ nếu cập nhật thất bại
                    finalImagePath = currentUser.getProfileImagePath();
                }
            }

            // Cập nhật cơ sở dữ liệu
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET full_name = ?, status = ?, profile_image_path = ? WHERE username = ?");
            stmt.setString(1, fullName);
            stmt.setString(2, status);
            stmt.setString(3, finalImagePath);
            stmt.setString(4, currentUser.getUsername());

            stmt.executeUpdate();
            stmt.close();

            // Cập nhật đối tượng người dùng hiện tại
            currentUser.setFullName(fullName);
            currentUser.setStatus(status);
            // Không cần cập nhật đường dẫn lại vì đã được cập nhật trong updateProfileImage
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean updateProfileImage(String imagePath) {
        if (currentUser == null) return false;

        try {
            // Tạo thư mục upload nếu chưa tồn tại
            File uploadDir = new File("upload/profile_images");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lấy file nguồn (có thể là đường dẫn tuyệt đối từ máy người dùng)
            File sourceFile = new File(imagePath);
            if (!sourceFile.exists()) {
                System.err.println("Source file does not exist: " + imagePath);
                return false;
            }

            // Tạo tên file duy nhất để tránh trùng lặp
            String fileName = sourceFile.getName();
            String extension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex);
            }

            String targetFileName = currentUser.getUsername() + "_" + System.currentTimeMillis() + extension;
            File targetFile = new File(uploadDir, targetFileName);

            // Sao chép file từ vị trí gốc vào thư mục upload
            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(targetFile)) {

                byte[] buffer = new byte[8192];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }

            // Tạo và lưu đường dẫn tương đối
            String relativePath = "upload/profile_images/" + targetFileName;

            // Cập nhật cơ sở dữ liệu với đường dẫn tương đối
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET profile_image_path = ? WHERE username = ?");
            stmt.setString(1, relativePath);
            stmt.setString(2, currentUser.getUsername());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            if (rowsAffected > 0) {
                // Cập nhật đối tượng người dùng hiện tại
                currentUser.setProfileImagePath(relativePath);
                System.out.println("Profile image updated successfully: " + relativePath);
                return true;
            }

            System.out.println("No rows affected when updating profile image");
            return false;
        } catch (SQLException | IOException e) {
            System.err.println("Error updating profile image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}