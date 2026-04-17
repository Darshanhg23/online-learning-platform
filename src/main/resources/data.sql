-- =====================================================
-- Online Learning Platform - Seed Data
-- =====================================================

-- Only insert if table is empty
INSERT INTO courses (title, description, category, duration, instructor, thumbnail_url, is_featured, total_lessons)
SELECT * FROM (SELECT
    'Java Programming for Beginners' AS title,
    'Learn the fundamentals of Java programming including OOP concepts, data structures, and more. Perfect for beginners who want to start their programming journey.' AS description,
    'Programming' AS category,
    '12 hours' AS duration,
    'Dr. Anita Sharma' AS instructor,
    '/images/java-course.png' AS thumbnail_url,
    TRUE AS is_featured,
    6 AS total_lessons
) AS tmp WHERE NOT EXISTS (SELECT 1 FROM courses LIMIT 1);

INSERT INTO courses (title, description, category, duration, instructor, thumbnail_url, is_featured, total_lessons)
SELECT * FROM (SELECT
    'Web Development with HTML & CSS' AS title,
    'Master modern web design from scratch. Build beautiful, responsive websites with HTML5 and CSS3, including Flexbox and Grid layouts.' AS description,
    'Web Development' AS category,
    '8 hours' AS duration,
    'Rahul Verma' AS instructor,
    '/images/web-course.png' AS thumbnail_url,
    TRUE AS is_featured,
    5 AS total_lessons
) AS tmp WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Web Development with HTML & CSS');

INSERT INTO courses (title, description, category, duration, instructor, thumbnail_url, is_featured, total_lessons)
SELECT * FROM (SELECT
    'Spring Boot Backend Development' AS title,
    'Build powerful REST APIs using Spring Boot, Spring JDBC, and MySQL. Covers MVC architecture, dependency injection, and database operations.' AS description,
    'Backend' AS category,
    '15 hours' AS duration,
    'Priya Patel' AS instructor,
    '/images/spring-course.png' AS thumbnail_url,
    TRUE AS is_featured,
    7 AS total_lessons
) AS tmp WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Spring Boot Backend Development');

INSERT INTO courses (title, description, category, duration, instructor, thumbnail_url, is_featured, total_lessons)
SELECT * FROM (SELECT
    'MySQL Database Design' AS title,
    'Learn relational database design, SQL queries, joins, stored procedures, and optimization techniques using MySQL.' AS description,
    'Database' AS category,
    '10 hours' AS duration,
    'Amit Singh' AS instructor,
    '/images/mysql-course.png' AS thumbnail_url,
    FALSE AS is_featured,
    5 AS total_lessons
) AS tmp WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'MySQL Database Design');

INSERT INTO courses (title, description, category, duration, instructor, thumbnail_url, is_featured, total_lessons)
SELECT * FROM (SELECT
    'JavaScript Essentials' AS title,
    'Comprehensive guide to modern JavaScript including ES6+, DOM manipulation, async programming, and building interactive web applications.' AS description,
    'Web Development' AS category,
    '11 hours' AS duration,
    'Sneha Kapoor' AS instructor,
    '/images/js-course.png' AS thumbnail_url,
    FALSE AS is_featured,
    6 AS total_lessons
) AS tmp WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'JavaScript Essentials');

INSERT INTO courses (title, description, category, duration, instructor, thumbnail_url, is_featured, total_lessons)
SELECT * FROM (SELECT
    'Object Oriented Programming in Java' AS title,
    'Deep dive into OOP principles: encapsulation, inheritance, polymorphism, and abstraction with real-world Java examples and design patterns.' AS description,
    'Programming' AS category,
    '9 hours' AS duration,
    'Dr. Anita Sharma' AS instructor,
    '/images/oop-course.png' AS thumbnail_url,
    FALSE AS is_featured,
    5 AS total_lessons
) AS tmp WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Object Oriented Programming in Java');

-- Lessons for Course 1 (Java Programming)
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 1, 'Introduction to Java & JDK Setup', 1, '45 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 1 AND lesson_order = 1);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 1, 'Variables, Data Types & Operators', 2, '60 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 1 AND lesson_order = 2);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 1, 'Control Flow: if, loops & switch', 3, '55 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 1 AND lesson_order = 3);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 1, 'Methods and Functions', 4, '50 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 1 AND lesson_order = 4);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 1, 'Arrays and Collections', 5, '65 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 1 AND lesson_order = 5);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 1, 'Exception Handling & Best Practices', 6, '50 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 1 AND lesson_order = 6);

-- Lessons for Course 2 (Web Development)
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 2, 'HTML5 Structure & Semantics', 1, '40 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 2 AND lesson_order = 1);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 2, 'CSS3 Styling & Box Model', 2, '55 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 2 AND lesson_order = 2);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 2, 'Flexbox & Grid Layouts', 3, '60 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 2 AND lesson_order = 3);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 2, 'Responsive Design & Media Queries', 4, '50 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 2 AND lesson_order = 4);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 2, 'Building a Complete Website', 5, '90 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 2 AND lesson_order = 5);

-- Lessons for Course 3 (Spring Boot)
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 3, 'Spring Boot Introduction & Setup', 1, '50 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 3 AND lesson_order = 1);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 3, 'Spring MVC & REST Controllers', 2, '65 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 3 AND lesson_order = 2);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 3, 'Spring JDBC & DataSource', 3, '70 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 3 AND lesson_order = 3);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 3, 'MySQL Integration', 4, '60 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 3 AND lesson_order = 4);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 3, 'Request/Response & JSON', 5, '55 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 3 AND lesson_order = 5);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 3, 'Exception Handling in REST', 6, '45 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 3 AND lesson_order = 6);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 3, 'Deploying Spring Boot App', 7, '60 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 3 AND lesson_order = 7);

-- Lessons for Course 4 (MySQL)
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 4, 'Introduction to Databases & SQL', 1, '40 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 4 AND lesson_order = 1);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 4, 'DDL & DML Commands', 2, '55 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 4 AND lesson_order = 2);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 4, 'Joins, Subqueries & Views', 3, '70 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 4 AND lesson_order = 3);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 4, 'Indexes & Performance Tuning', 4, '60 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 4 AND lesson_order = 4);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 4, 'Stored Procedures & Triggers', 5, '65 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 4 AND lesson_order = 5);

-- Lessons for Course 5 (JavaScript)
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 5, 'JavaScript Basics & ES6+', 1, '50 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 5 AND lesson_order = 1);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 5, 'DOM Manipulation', 2, '60 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 5 AND lesson_order = 2);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 5, 'Events & Event Listeners', 3, '45 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 5 AND lesson_order = 3);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 5, 'Fetch API & AJAX', 4, '55 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 5 AND lesson_order = 4);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 5, 'LocalStorage & SessionStorage', 5, '40 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 5 AND lesson_order = 5);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 5, 'Building an Interactive App', 6, '80 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 5 AND lesson_order = 6);

-- Lessons for Course 6 (OOP)
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 6, 'Classes, Objects & Constructors', 1, '50 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 6 AND lesson_order = 1);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 6, 'Encapsulation & Access Modifiers', 2, '45 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 6 AND lesson_order = 2);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 6, 'Inheritance & super Keyword', 3, '55 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 6 AND lesson_order = 3);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 6, 'Polymorphism & Method Overriding', 4, '60 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 6 AND lesson_order = 4);
INSERT INTO lessons (course_id, title, lesson_order, duration)
SELECT 6, 'Abstraction & Interfaces', 5, '50 min' WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE course_id = 6 AND lesson_order = 5);
