📝 Smart Attendance System

A modern, web-based **Smart Attendance System** designed to streamline student attendance management for teachers, students, and parents—enhancing transparency, accountability, and communication.

---

## 🌟 Features

### 👩‍🏫 **Teacher Features**
- ✅ Mark attendance for each class session  
- 📊 View real-time student attendance  
- ✏️ Edit attendance records as needed  
- 📑 Generate detailed reports (individual or class-wide)

### 👨‍🎓 **Student Features**
- 👁️ View personal attendance records  
- 📈 Track attendance trends over time

### 👨‍👩‍👧 **Parent Features**
- 📧 Receive **automatic email notifications** when a student misses **more than 30%** of their classes

### 🔐 **General Features**
- 🔒 Secure login for teachers, students, and parents  
- 🛡️ Role-based access control  
- 📱 Fully responsive design (works on desktop, tablet, and mobile)  
- 📎 Support for uploading class-related documents

---

## ⚙️ Technologies Used

| Layer       | Technologies                                      |
|-------------|---------------------------------------------------|
| **Backend** | Java, Spring Boot, Spring MVC, Spring Data JPA   |
| **Frontend**| Thymeleaf, HTML, CSS, JavaScript                 |
| **Database**| PostgreSQL                                       |
| **Email**   | Spring Mail (via Gmail SMTP)                     |
| **Build**   | Maven                                            |
| **Extras**  | Multipart file upload support                     |

---

### 1. Clone the Repository

git clone https://github.com/your-username/smart-attendance-system.git
cd smart-attendance-system

## 🚀 Installation & Setup

git clone https://github.com/your-username/smart-attendance-system.git
cd smart-attendance-system

 Configure PostgreSQL
Create a database named smart_attendance
Update application.properties with your credentials:
properties

spring.datasource.url=jdbc:postgresql://localhost:5432/smart_attendance
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
3. Set Up Email Notifications
Enable App Passwords in your Google Account
Add your Gmail credentials to application.properties:
properties

spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
Build & Run
bash

mvn clean install
mvn spring-boot:run
🌐 The app will be available at: http://localhost:8082 

🧑‍💼 Usage
Teachers: Log in to mark/view attendance and generate reports
Students: Log in to monitor their own attendance history
Parents: Automatically receive alerts via email when absences exceed 30%
Admins: Manage user accounts (teachers, students, parents)
🚧 Future Enhancements
📲 Mobile app integration for on-the-go attendance marking
📊 Interactive dashboards with analytics and visual trends
💬 SMS notifications for absentee alerts (in addition to email)
🔗 LMS integration (e.g., Moodle, Google Classroom)
   



