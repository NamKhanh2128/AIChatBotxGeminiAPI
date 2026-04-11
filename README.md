# Internal Chat - Ứng dụng Chat Tích hợp AI

Internal Chat là một ứng dụng chat desktop hiện đại được xây dựng bằng JavaFX, tích hợp trí tuệ nhân tạo Gemini AI. Ứng dụng cung cấp môi trường chat nhóm real-time và chatbot cá nhân thông minh.

## 🌟 Tính năng chính

### 🔐 Xác thực người dùng
- **Đăng ký tài khoản**: Tạo tài khoản mới với username, email và password
- **Đăng nhập bảo mật**: Hệ thống xác thực an toàn
- **Quản lý phiên đăng nhập**: Tự động duy trì phiên làm việc

### 💬 Chat nhóm real-time
- **Chat đa người dùng**: Tham gia chat room với nhiều người cùng lúc
- **Gửi/nhận tin nhắn tức thì**: Hệ thống socket real-time
- **Bảng emoji phong phú**: Hơn 35 emoji để thể hiện cảm xúc
- **Lịch sử chat**: Lưu trữ và xem lại các cuộc trò chuyện
- **Giao diện trực quan**: Phân biệt rõ ràng tin nhắn của bản thân và người khác

### 🤖 Chatbot AI thông minh
- **Tích hợp Gemini AI**: Chatbot được hỗ trợ bởi Google Gemini
- **Hiển thị HTML cao cấp**: Chuyển đổi Markdown thành HTML với CSS styling đẹp mắt
- **WebView renderer**: Hiển thị nội dung phong phú với format HTML chuyên nghiệp
- **Chat cá nhân**: Không gian riêng tư để tương tác với AI
- **Phản hồi thông minh**: AI hiểu ngữ cảnh và trả lời chuyên nghiệp

### 👤 Quản lý thông tin cá nhân
- **Profile cá nhân**: Cập nhật tên, email, trạng thái
- **Ảnh đại diện**: Upload và thay đổi ảnh profile
- **Tùy chỉnh trạng thái**: Hiển thị trạng thái hoạt động của bạn

## 🛠️ Công nghệ sử dụng

- **JavaFX 23.0.1**: Framework UI chính
- **JavaFX WebView**: Engine hiển thị HTML cho chatbot
- **Java 21**: Ngôn ngữ lập trình
- **SQLite**: Cơ sở dữ liệu nhẹ
- **Socket Programming**: Giao tiếp real-time
- **Google Gemini AI**: Trí tuệ nhân tạo
- **Maven**: Quản lý dependencies
- **HTML/CSS**: Rendering và styling cho nội dung chatbot

## 🚀 Cài đặt và chạy ứng dụng

### 1. Clone repository
```bash
git clone https://github.com/your-username/OOP_BTL20242.git
cd OOP_BTL20242
```

### 2. Cấu hình Gemini AI
Chỉnh sửa file `src/main/resources/config.properties`:
```properties
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
```

Để lấy API key cho Gemini AI:
1. Truy cập [Google AI Studio](https://aistudio.google.com/)
2. Đăng nhập với tài khoản Google của bạn
3. Vào mục "API keys" trong menu
4. Tạo API key mới và sao chép
5. Dán API key vào file `config.properties`

**Lưu ý**: API key là thông tin nhạy cảm, không nên chia sẻ hoặc đưa lên GitHub. File `config.properties` đã được thêm vào `.gitignore` để tránh vô tình commit.

### 3. Compile và chạy
```bash
# Compile dự án
mvn clean compile

# Chạy ứng dụng
mvn javafx:run
```


## 📖 Hướng dẫn sử dụng

### Đăng ký và đăng nhập
1. Khởi chạy ứng dụng
2. Chọn "Đăng ký" nếu chưa có tài khoản
3. Điền thông tin: username, email, password
4. Đăng nhập với tài khoản đã tạo

### Sử dụng chat nhóm
1. Sau khi đăng nhập, chọn tab "Chat"
2. Ứng dụng tự động kết nối đến server
3. Gõ tin nhắn và nhấn Enter hoặc nút Send
4. Sử dụng nút emoji để thêm biểu cảm
5. Để gọi bot trong chat nhóm, sử dụng cú pháp `@bot [câu hỏi]`, ví dụ: `@bot Cho tôi biết thời tiết hôm nay`
6. Để chia sẻ hình ảnh, bạn có thể kéo và thả hình ảnh vào khung chat hoặc sử dụng nút tải lên

### Chat với AI Bot
1. Chọn tab "Chatbot"
2. Gõ câu hỏi hoặc tin nhắn
3. AI sẽ phản hồi và hiển thị dưới dạng HTML được format đẹp với CSS styling
4. Nội dung hỗ trợ headers, code blocks, lists, bold/italic text
5. Lịch sử chat được lưu trong phiên làm việc
6. Bot có thể trả lời nhiều loại câu hỏi, từ học thuật đến giải trí

> **Lưu ý**: Nếu gặp lỗi "API key không hợp lệ hoặc chưa được cấu hình", hãy kiểm tra file `src/main/resources/config.properties` và đảm bảo API key đã được cấu hình đúng.

### Cài đặt Profile
1. Chọn tab "Settings"
2. Cập nhật tên, trạng thái
3. Nhấn "Change Photo" để thay đổi ảnh đại diện
4. Nhấn "Save" để lưu thay đổi

## 📁 Cấu trúc dự án

```
OOP_BTL20242/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/training/studyfx/
│   │   │   │   ├── App.java                    # Entry point của ứng dụng
│   │   │   │   ├── ChatHistoryManager.java     # Quản lý lịch sử chat
│   │   │   │   ├── SocketManager.java          # Quản lý kết nối socket
│   │   │   │   ├── controller/                 # Controllers cho từng màn hình
│   │   │   │   │   ├── AboutController.java    # Controller cho màn hình About
│   │   │   │   │   ├── ChatbotViewController.java  # Controller cho chatbot AI
│   │   │   │   │   ├── ChatViewController.java    # Controller cho chat nhóm
│   │   │   │   │   ├── LoginController.java       # Controller đăng nhập
│   │   │   │   │   ├── ProfileSettingController.java  # Cài đặt profile
│   │   │   │   │   ├── RegisterController.java    # Controller đăng ký
│   │   │   │   │   └── UIController.java          # Controller chính
│   │   │   │   ├── exception/                  # Custom exceptions
│   │   │   │   │   ├── ChatException.java
│   │   │   │   │   ├── ConnectionException.java
│   │   │   │   │   └── MessageException.java
│   │   │   │   ├── model/                      # Data models
│   │   │   │   │   ├── ChatMessage.java        # Model tin nhắn chat
│   │   │   │   │   ├── Message.java            # Model tin nhắn cơ bản
│   │   │   │   │   └── User.java               # Model người dùng
│   │   │   │   ├── server/                     # Server components
│   │   │   │   │   ├── ClientHandler.java      # Xử lý client kết nối
│   │   │   │   │   └── Server.java             # Server socket
│   │   │   │   ├── service/                    # Business logic
│   │   │   │   │   ├── GeminiService.java      # Tích hợp Google Gemini AI, đọc API key từ config.properties
│   │   │   │   │   └── UserService.java        # Xử lý logic người dùng
│   │   │   │   └── util/                       # Utilities
│   │   │   │       └── MarkdownToHtml.java     # Chuyển đổi Markdown sang HTML
│   │   │   └── module-info.java                # Module definitions
│   │   └── resources/
│   │       ├── com/training/studyfx/           # FXML files
│   │       │   ├── AboutView.fxml              # Giao diện About
│   │       │   ├── ChatbotView.fxml            # Giao diện chatbot
│   │       │   ├── ChatView.fxml               # Giao diện chat nhóm
│   │       │   ├── ListView.fxml               # Giao diện danh sách
│   │       │   ├── LoginView.fxml              # Giao diện đăng nhập
│   │       │   ├── ProfileSettingView.fxml     # Giao diện cài đặt profile
│   │       │   ├── RegisterView.fxml           # Giao diện đăng ký
│   │       │   └── UI.fxml                     # Giao diện chính
│   │       ├── config.properties               # Cấu hình (API keys, etc.)
│   │       ├── images/                         # Icons và ảnh
│   │       │   ├── avt.jpg
│   │       │   ├── chat.png
│   │       │   ├── chatbot.png
│   │       │   ├── default_profile.png
│   │       │   ├── logo.png
│   │       │   ├── send.png
│   │       │   └── ... (các hình ảnh khác)
│   │       └── styles/
│   │           └── ui.css                      # Stylesheet chính
├── upload/                                     # User uploaded files
│   ├── images/                                 # Chat images
│   └── profile_images/                         # Profile pictures
├── chat_history.txt                            # Lưu trữ lịch sử chat
├── nbactions.xml                               # Cấu hình NetBeans
├── pom.xml                                     # Maven dependencies
└── studyfx.db                                  # SQLite database
```



## 📝 License

Dự án này được phát triển cho Project OOP tại HUST.


*Made with ❤️ by OOP BTL 2024-2 Team*