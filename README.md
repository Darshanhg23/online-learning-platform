#  LearnHub — Online Learning Platform

LearnHub is a full-stack web application designed for online education. It features a robust course management system for administrators and an engaging learning experience for students, complete with video-based lessons, progress tracking, and certificate generation.

![Admin Panel Mockup](https://images.unsplash.com/photo-1501504905252-473c47e087f8?auto=format&fit=crop&q=80&w=1000)

##  Key Features

### For Students
- **Dynamic Course Catalog**: Browse and search through various categories like Web Development, Programming, and Databases.
- **Interactive Dashboard**: Track your enrolled courses and overall learning progress.
- **Video Lessons**: High-quality video lessons with automatic progress tracking.
- **Certificate Generation**: Earn a premium, printable certificate automatically upon 100% course completion.

### For Administrators
- **Content Management System (CMS)**: Create, edit, and delete courses with ease.
- **Lesson Manager**: Add video lessons, set lesson orders, and manage durations.
- **Video Uploads**: Integrated video file upload system that stores content locally.
- **Student Overview**: Monitor registered students and their roles.

##  Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.5
- **Database**: MySQL 8.0+
- **Data Access**: Spring JDBC (JdbcTemplate)
- **Frontend**: HTML5, CSS3 (Vanilla), JavaScript (ES6+)
- **Security**: BCrypt for secure password hashing

##  Prerequisites

Before running the project, ensure you have:
- **Java 17 JDK** installed.
- **MySQL Server** running.
- **Apache Maven** (included path in `start.bat` defaults to `%USERPROFILE%\maven\apache-maven-3.9.6`).

##  Setup & Installation

1. **Database Setup**:
   - Create a database named `learning_platform` in your MySQL server.
   - Update the credentials in `src/main/resources/application.properties`:
     ```properties
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

2. **Running the Application**:
   - Simply double-click the **`start.bat`** file in the root directory.
   - This script will automatically configure the Maven path and start the Spring Boot server.
   - Alternatively, run the following command in your terminal:
     ```bash
     mvn spring-boot:run
     ```

3. **Accessing the Platform**:
   - Open your browser and navigate to `http://localhost:8080`
   - Default Admin Credentials: (Check `data.sql` for initial user seeds)

##  Project Structure

- `src/main/java`: Backend controllers, models, and repositories.
- `src/main/resources/static`: Frontend assets (HTML, CSS, JS, Images).
- `src/main/resources/schema.sql`: Database table definitions.
- `src/main/resources/data.sql`: Initial data seeding for testing.
- `videos/`: (Auto-created) Stores uploaded lesson videos.

---

*Developed as part of the LearnHub Internship Project.*
