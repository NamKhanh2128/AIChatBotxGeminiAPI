# StudyFX Chat - Ứng dụng Chat Nhóm Real-time & Trợ lý AI Gemini

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-24.0.1-blue.svg?style=flat-square)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-Build-green.svg?style=flat-square&logo=apache-maven)](https://maven.apache.org/)
[![AI Integration](https://img.shields.io/badge/Gemini_AI-2.0--flash-blueviolet.svg?style=flat-square&logo=google-gemini)](https://aistudio.google.com/)

**StudyFX Chat** là một ứng dụng chat desktop hiện đại, trực quan được xây dựng trên nền tảng **JavaFX** và ngôn ngữ **Java 21**. Ứng dụng mang đến giải pháp trò chuyện nhóm thời gian thực (Real-time Group Chat) thông qua kết nối Socket ổn định, đồng thời tích hợp **Trợ lý Trí tuệ Nhân tạo Google Gemini AI** đột phá để hỗ trợ giải đáp trực tuyến.

---

## 🌟 Các Tính năng Nổi bật

### 1. 🤖 Tích hợp Trí tuệ Nhân tạo Gemini AI
*   **Chatbot Cá nhân (Tab Chatbot):** Không gian tương tác riêng tư 1-1 với trợ lý AI. Người dùng có thể hỏi đáp mọi lĩnh vực từ lập trình, dịch thuật đến hỗ trợ kiến thức học tập.
*   **Trợ lý AI trong Chat nhóm (Group Assistant):** Gọi trực tiếp AI vào cuộc trò chuyện chung bằng cú pháp `@bot [câu hỏi]`. Câu trả lời từ AI sẽ được gửi trực tiếp cho cả nhóm cùng theo dõi.
*   **Hoạt động Bất đồng bộ (Async execution):** Các truy vấn gửi lên AI được xử lý trên luồng phụ (`Thread`), đảm bảo giao diện đồ họa (UI Thread) luôn mượt mà và không bao giờ xảy ra hiện tượng đơ/treo ứng dụng.

### 2. 💬 Chat Nhóm Real-time (Socket TCP/IP)
*   **Kết nối Đa người dùng:** Hệ thống Socket mạnh mẽ cho phép nhiều client cùng kết nối và truyền tải tin nhắn tức thì.
*   **Nhận diện Trực quan:** Giao diện tự động phân biệt rõ ràng tin nhắn của bản thân (căn phải, bong bóng xanh lam), tin nhắn của thành viên khác (căn trái, bong bóng xám) và tin nhắn từ AI (căn trái, màu tím đặc trưng).
*   **Lịch sử Tin nhắn:** Tích hợp bộ quản lý lịch sử trò chuyện, giúp lưu trữ và hiển thị lại nội dung chat cũ mỗi khi đăng nhập.
*   **Thông báo Trạng thái:** Gửi thông báo hệ thống tự động khi có thành viên mới tham gia hoặc rời phòng chat.

### 3. 🎨 Trải nghiệm Giao diện Cao cấp & Hiện đại (UX/UI)
*   **Chế độ Sáng/Tối (Dark & Light Mode):** Thay đổi toàn bộ giao diện chỉ với 1 click. Trạng thái giao diện sẽ được lưu trữ tự động vào tệp cấu hình trên máy khách, tự động áp dụng lại ở lần mở sau.
*   **Tự động Co giãn Responsive (Scale Manager):** Tự động điều chỉnh kích cỡ font chữ gốc (root font size) từ 10px đến 18px dựa trên chiều rộng cửa sổ. Đảm bảo bố cục hiển thị luôn cân đối và sắc nét bất kể kích thước màn hình.
*   **Sidebar Thu gọn thông minh (Collapsible Sidebar):** Thanh menu điều hướng bên trái có khả năng co giãn linh hoạt với hiệu ứng làm mờ chữ và thay đổi chiều rộng mượt mà.
*   **Hiệu ứng Chuyển trang mượt (Fade Transition):** Áp dụng hiệu ứng mờ dần - hiện dần (Fade Out/In) khi chuyển đổi giữa các tab chức năng, giảm thiểu cảm giác giật lag màn hình.

### 4. 👤 Quản lý Hồ sơ Cá nhân (Profile Settings)
*   **Tùy chỉnh Thông tin:** Cập nhật họ tên đầy đủ, địa chỉ email và trạng thái hoạt động hiện tại.
*   **Ảnh đại diện cá nhân (Avatar):** Hỗ trợ đăng tải ảnh đại diện từ máy tính, tự động sao chép và lưu trữ hình ảnh vào thư mục dự án để duy trì lâu dài.

---

## 🛠️ Kiến trúc Hệ thống & Thiết kế Code

Dự án tuân thủ chặt chẽ mô hình kiến trúc **MVC (Model-View-Controller)** và thiết kế dạng Module giúp dễ dàng bảo trì và phát triển mở rộng:

*   **Lớp Giao diện (View):** Định nghĩa bằng định dạng FXML trực quan cùng bộ stylesheets CSS (`ui-light.css`, `ui-dark.css`) tối ưu hóa hiển thị.
*   **Lớp Điều khiển (Controller):** Xử lý luồng tương tác và kết nối dữ liệu từ giao diện đến nghiệp vụ.
*   **Lớp Dịch vụ (Service):** Nơi xử lý logic nghiệp vụ chính như kết nối socket, lưu lịch sử, và gọi API AI Gemini.
*   **Cơ chế Kích hoạt Server Thông minh:** Thiết kế Server chạy ngầm tự động trong ứng dụng client. Khi ứng dụng khởi chạy lần đầu, nó sẽ tự động mở cổng TCP Server ở background. Các client khởi chạy tiếp theo sẽ phát hiện cổng đã mở và tự kết nối vào dưới vai trò Client. Điều này cho phép chạy nhiều client để kiểm thử chat nhóm cục bộ vô cùng đơn giản mà không cần chạy thủ công file Server riêng.

---

## 📁 Cấu trúc Thư mục Dự án

```text
AIChatBotxGeminiAPI/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/training/studyfx/
│   │   │   │   ├── App.java                    # Điểm bắt đầu (khởi động UI và chạy Server ngầm)
│   │   │   │   ├── Launcher.java               # Lớp trung gian chạy Main (tránh lỗi JavaFX Module)
│   │   │   │   ├── controller/                 # Các lớp điều khiển giao diện (FXML Controllers)
│   │   │   │   │   ├── AboutController.java    # Màn hình Giới thiệu
│   │   │   │   │   ├── ChatViewController.java # Điều khiển Chat nhóm, xử lý cú pháp @bot
│   │   │   │   │   ├── ChatbotViewController.java # Điều khiển Chatbot AI cá nhân 1-1
│   │   │   │   │   ├── LoginController.java    # Quản lý Đăng nhập
│   │   │   │   │   ├── ProfileSettingController.java # Cập nhật thông tin Profile & Avatar
│   │   │   │   │   ├── RegisterController.java # Đăng ký tài khoản mới
│   │   │   │   │   └── UIController.java       # Bộ khung chính (chứa Sidebar, Navigation & Dark/Light mode)
│   │   │   │   ├── exception/                  # Các ngoại lệ tự định nghĩa
│   │   │   │   │   ├── ChatException.java
│   │   │   │   │   ├── ConnectionException.java
│   │   │   │   │   └── MessageException.java
│   │   │   │   ├── model/                      # Các đối tượng dữ liệu (Models)
│   │   │   │   │   ├── ChatMessage.java
│   │   │   │   │   ├── Message.java
│   │   │   │   │   └── User.java
│   │   │   │   ├── server/                     # Hệ thống Socket Server & Client Connection
│   │   │   │   │   ├── ClientHandler.java      # Quản lý gửi/nhận luồng dữ liệu cho mỗi Client
│   │   │   │   │   ├── Server.java             # Socket Server lắng nghe cổng 1235
│   │   │   │   │   └── SocketManager.java      # Quản lý Socket Client kết nối (Singleton)
│   │   │   │   ├── service/                    # Xử lý Logic & Dịch vụ nền tảng
│   │   │   │   │   ├── ChatHistoryManager.java # Ghi/Đọc lịch sử chat ra file vật lý
│   │   │   │   │   ├── GeminiService.java      # Gọi API Google Gemini AI
│   │   │   │   │   └── UserService.java        # Quản lý danh sách người dùng (In-memory)
│   │   │   │   └── util/                       # Tiện ích bổ trợ (Utilities)
│   │   │   │       ├── MarkdownToHtml.java     # Chuyển đổi Markdown sang HTML
│   │   │   │       ├── ScaleManager.java       # Tự động scale font-size theo chiều rộng ứng dụng
│   │   │   │       └── ThemeManager.java       # Quản lý giao diện Sáng/Tối và lưu cấu hình
│   │   │   └── module-info.java                # Khai báo các Module dependencies của JavaFX
│   │   └── resources/
│   │       ├── com/training/studyfx/           # File thiết kế giao diện XML (.fxml)
│   │       │   ├── AboutView.fxml
│   │       │   ├── ChatView.fxml
│   │       │   ├── ChatbotView.fxml
│   │       │   ├── ListView.fxml
│   │       │   ├── LoginView.fxml
│   │       │   ├── ProfileSettingView.fxml
│   │       │   ├── RegisterView.fxml
│   │       │   └── UI.fxml
│   │       ├── config.properties               # File lưu trữ cấu hình cục bộ (theme, api key)
│   │       ├── images/                         # Các biểu tượng, ảnh nền và avatar mặc định
│   │       │   ├── default_profile.png
│   │       │   ├── logo.png
│   │       │   ├── send.png
│   │       │   └── ... (các icon giao diện khác)
│   │       └── styles/                         # Style Sheets định dạng CSS giao diện
│   │           ├── ui-dark.css                 # Dark Mode stylesheet
│   │           └── ui-light.css                # Light Mode stylesheet
├── upload/                                     # Thư mục lưu trữ hình ảnh tải lên của người dùng
├── chat_history.txt                            # File văn bản lưu trữ lịch sử tin nhắn
├── pom.xml                                     # File quản lý thư viện Maven
└── studyfx.db                                  # sqlite database (dự phòng)
```

---

## 🚀 Hướng dẫn Cài đặt & Chạy ứng dụng

### 🔑 Yêu cầu hệ thống
*   **Java Development Kit (JDK):** Phiên bản **21** trở lên.
*   **Apache Maven:** Đã được cài đặt và cấu hình biến môi trường (`PATH`).

---

### Bước 1: Thiết lập Gemini API Key

Hiện tại, ứng dụng kết nối trực tiếp đến mô hình Gemini AI thông qua một API Key. Bạn có hai cách để cấu hình:

#### Cách 1: Cấu hình trực tiếp trong mã nguồn (Khuyên dùng)
1.  Truy cập và mở tệp tin [GeminiService.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/service/GeminiService.java).
2.  Tìm tới dòng khai báo hằng số `KEY` (khoảng dòng 11) và điền khóa API của bạn vào:
    ```java
    private static final String KEY = "API_KEY_CỦA_BẠN";
    ```

#### Cách 2: Cấu hình qua tệp thuộc tính
Bạn cũng có thể lưu trữ API Key trong tệp cấu hình [config.properties](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/resources/config.properties):
```properties
gemini.api.key=API_KEY_CỦA_BẠN
app.theme=dark
```

> [!TIP]
> **Cách lấy API Key miễn phí:**
> 1. Truy cập [Google AI Studio](https://aistudio.google.com/).
> 2. Đăng nhập bằng tài khoản Google.
> 3. Click vào nút **"Get API key"** -> Chọn **"Create API key"**.
> 4. Sao chép API Key vừa tạo và điền vào ứng dụng.

---

### Bước 2: Compile & Chạy ứng dụng

Mở Terminal hoặc Command Prompt tại thư mục gốc của dự án và chạy các lệnh sau:

1.  **Dọn dẹp và Biên dịch dự án:**
    ```bash
    mvn clean compile
    ```

2.  **Khởi chạy giao diện chính:**
    ```bash
    mvn javafx:run
    ```

---

### 👥 Hướng dẫn kiểm thử Chat Nhóm (Đa Client)

Để kiểm tra tính năng chat nhóm thời gian thực trực tiếp trên máy tính của bạn:

1.  Mở một cửa sổ Terminal mới và chạy ứng dụng lần thứ nhất:
    ```bash
    mvn javafx:run
    ```
    *Cửa sổ thứ nhất sẽ tự động đóng vai trò mở Socket Server ngầm ở background.*

2.  Mở thêm một cửa sổ Terminal khác và chạy ứng dụng lần thứ hai:
    ```bash
    mvn javafx:run
    ```
    *Cửa sổ thứ hai sẽ tự động kết nối vào Server đang chạy do cửa sổ đầu tiên tạo ra.*

3.  Đăng nhập bằng các tài khoản khác nhau trên cả hai cửa sổ (Ví dụ: `khanh` và `admin`).
4.  Vào tab **Chat** và bắt đầu gửi tin nhắn để thấy quá trình trao đổi thông tin thời gian thực!

---

## 📝 Tài liệu Tham chiếu Mã nguồn

Dưới đây là danh sách các tệp tin quan trọng trong dự án để bạn tiện tra cứu và tùy biến:

*   **Điểm khởi chạy ứng dụng:** [App.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/App.java) & [Launcher.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/Launcher.java)
*   **Controller Khung chính:** [UIController.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/controller/UIController.java)
*   **Xử lý Chat Nhóm:** [ChatViewController.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/controller/ChatViewController.java)
*   **Xử lý Chatbot AI:** [ChatbotViewController.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/controller/ChatbotViewController.java)
*   **Socket Server chính:** [Server.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/server/Server.java)
*   **Socket Client Manager:** [SocketManager.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/server/SocketManager.java)
*   **Dịch vụ Gemini AI:** [GeminiService.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/service/GeminiService.java)
*   **Quản lý giao diện (Theme):** [ThemeManager.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/util/ThemeManager.java)
*   **Quản lý Responsive (Scale):** [ScaleManager.java](file:///c:/Users/KHANH/Documents/GitHub/AIChatBotxGeminiAPI/src/main/java/com/training/studyfx/util/ScaleManager.java)