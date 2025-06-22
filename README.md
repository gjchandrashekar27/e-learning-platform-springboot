# 📚 Jnana - E-Learning Platform 🚀

Welcome to **Jnana**, a modern, intuitive, and interactive E-Learning platform where **Tutors** can share knowledge, and **Learners** can enroll, study, take quizzes, and earn certificates 📜!

---

## 🔍 Overview

**Jnana** is a full-stack web application designed to simplify online learning and course creation. The platform supports:
- 🎓 Course creation by Tutors
- 🧑‍💻 Learning and certification for Learners
- 💳 Razorpay integration for payments
- 🤖 ChatGPT API for quiz evaluations
- 📩 Certificate generation and email delivery

---

## 🧑‍💼 Roles & Responsibilities

### 🧑‍🏫 Tutor
- Register via email OTP
- Create and manage courses
- Upload section videos, notes & quizzes
- Publish courses
- View learner reviews & reply to comments

### 👨‍🎓 Learner
- Register & log in
- Enroll in free or paid courses
- Sequentially access course content
- Submit quizzes evaluated by ChatGPT
- Attempt final quiz and earn certificates
- Submit reviews and comments

---

## 🛠️ Tech Stack

| Layer      | Technologies Used |
|------------|-------------------|
| Backend    | Spring Boot, JPA, MySQL |
| Frontend   | Thymeleaf, HTML, CSS, JS |
| Payment    | Razorpay API 💳 |
| Evaluation | ChatGPT API 🤖 |
| Emailing   | SMTP, JavaMailSender 📧 |
| Auth       | Session-based login with OTP |

---

## 📂 Project Structure

src/
├── controller/ # Web controllers for tutors & learners
├── service/ # Business logic
├── repository/ # Spring JPA Repositories
├── model/ # Entity classes
├── utils/ # Helper classes (OTP, PDF Generator, etc.)
├── templates/ # Thymeleaf HTML pages
└── static/ # CSS, JS, images

## ⚙️ Features

### ✅ Functional Highlights
- ✍️ Tutor & Learner registration with OTP verification
- 📁 Course & Section management
- 📽️ Upload videos, notes & quizzes
- 🧩 Quiz submission evaluated via ChatGPT
- 🔐 Quiz-gated content unlocking
- 📜 Final quiz + auto certificate generation
- 📧 Certificate emailed on successful completion
- 💬 Comment & review system
- 💳 Razorpay integration for paid courses

### 🔒 Non-Functional Highlights
- Responsive UI for all screens 📱
- Session timeout and secure access 🔐
- Data stored in MySQL DB 🗃️
- Email notifications for verification and certificates 📤

---
## 🧰 Spring Boot Starters

The project uses the following Spring Boot starters to simplify integration:

| Starter | Description |
|--------|-------------|
| `spring-boot-starter-web` | For building REST APIs and web apps |
| `spring-boot-starter-thymeleaf` | For rendering dynamic HTML using Thymeleaf |
| `spring-boot-starter-data-jpa` | For ORM and database access using JPA/Hibernate |
| `spring-boot-starter-mail` | To send OTPs and certificates via email |
| `spring-boot-starter-validation` | For input validation and error handling |
| `spring-boot-starter-security` *(optional)* | Can be used for securing endpoints (if added) |
| `spring-boot-starter-test` | For unit and integration testing |
| `spring-session-jdbc` | For session-based authentication using JDBC |

## 🧰 Spring Boot Starters

| Dependency | Purpose |
|------------|---------|
| `spring-boot-starter-web` | Core web functionalities |
| `spring-boot-starter-thymeleaf` | Dynamic front-end using Thymeleaf |
| `spring-boot-starter-data-jpa` | Database connectivity with MySQL |
| `spring-boot-starter-mail` | Email services for OTP and certificates |
| `spring-session-jdbc` | Persist user sessions in DB |
| `spring-boot-starter-validation` | Input form validations |
| `spring-boot-starter-test` | Writing and executing tests |

## 🙏 Special Thanks

A heartfelt thank you to everyone who made this project possible:

- 💡 **OpenAI (ChatGPT)** – for smart quiz evaluation
- 💳 **Razorpay** – for seamless payment integration
- 📧 **JavaMailSender** – for reliable email delivery
- 🛠️ **Spring Boot Team** – for the powerful backend framework
- 🌐 **Thymeleaf** – for creating clean and dynamic UI pages
- 👥 **Learners & Tutors** – for inspiring real-world platform use cases
- ❤️ And a special thanks to the developer community for tutorials, docs, and guidance!

> _Built with passion, purpose, and a lot of coffee ☕!_

## 📬 Contact Me

Feel free to connect or reach out for collaboration, feedback, or opportunities:

- 🔗 [LinkedIn – Chandrashekar GJ](https://www.linkedin.com/in/chandrashekar-gj)  
- 📧 Email: [chandrashekar.gj19@gmail.com](mailto:chandrashekar.gj19@gmail.com)

---

## 🙏 Thank You

Thank you for exploring the **Jnana E-Learning Platform**.  
This project was built with passion to bridge the gap between tutors and learners through technology.

If you found this helpful or interesting, feel free to ⭐️ star the repo, give feedback, or connect with me.  
Let’s keep learning and building amazing things together! 🚀

— **Chandrashekar GJ**

#Java #SpringBoot #FullStackDevelopment #ELearningPlatform #Thymeleaf #Razorpay #OpenAI #ChatGPT #JPA #MySQL #RESTAPI #BackendDevelopment #SoftwareEngineering #WebDevelopment #JavaDeveloper #StudentProject #OpenSource
