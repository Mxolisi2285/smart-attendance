Smart Attendance System

Smart Attendance System is a web-based application designed to manage student attendance efficiently. The system allows teachers to mark and view attendance in real-time, while students and parents can track attendance records. It integrates email notifications for parents when a student’s absences exceed 30% of their classes.

Features
Teacher Features

Mark attendance for each class session.

View student attendance in real-time to monitor participation instantly.

Edit attendance records when necessary.

Generate detailed attendance reports for individual students and classes.

Student Features

View personal attendance records.

Track attendance trends over time.

Parent Features

Receive email notifications when a student misses more than 30% of classes.

General Features

Secure login for teachers, students, and parents.

Role-based access control.

Responsive design for desktops, tablets, and mobile devices.

Support for uploading class-related documents.


Technologies Used

Backend: Java, Spring Boot, Spring MVC, Spring Data JPA

Frontend: Thymeleaf, HTML, CSS, JavaScript

Database: PostgreSQL

Email Service: Spring Mail (Gmail SMTP)

Other: Maven for dependency management, Multipart file upload support


Installation & Setup

Clone the repository

git clone https://github.com/your-username/smart-attendance-system.git
cd smart-attendance-system

Configure PostgreSQL database

Create a database named smart_attendance.

Update application.properties with your PostgreSQL credentials:

spring.datasource.url=jdbc:postgresql://localhost:5432/smart_attendance
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

Email Configuration

Update your Gmail credentials in application.properties for notifications:

spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

Build and Run

mvn clean install
mvn spring-boot:run
The application will run on http://localhost:8082.


Usage

Teachers can log in to mark and view attendance in real-time.

Students can log in to view their attendance records.

Parents automatically receive email notifications if a student misses more than 30% of their classes.

Admin users can manage teacher, student, and parent accounts.

Future Enhancements

Mobile app integration for easier attendance marking.

Dashboard analytics to track student attendance trends.

SMS notifications for absentees.

Integration with external learning management systems (LMS).
