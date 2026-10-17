# 🌊 Flood Relief & Disaster Management System (FRDMS)

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-0F172A?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20Boot-Backend-2563EB?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-Database-1D4ED8?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Thymeleaf-Frontend-0F766E?style=for-the-badge&logo=thymeleaf&logoColor=white" />
</p>

<p align="center">
  <strong>💙 Technology with a purpose — helping communities recover, together.</strong>
</p>

---

## 🌷 About the Project

The **Flood Relief and Disaster Management System (FRDMS)** is a web-based application developed to support the disaster relief activities of the **Healthy Lanka Alliance for Development**.

The system aims to improve how disaster-related information, affected people, volunteers, relief supplies, donations, finances, and feedback are managed through a centralized platform.

By reducing reliance on paper records, spreadsheets, phone calls, and manual reporting, FRDMS supports more organized, transparent, and efficient relief operations.

## 🎯 Project Objectives

- 🌊 Centralize disaster-related information.
- 🧑‍🤝‍🧑 Maintain information about disaster-affected people.
- 🤝 Support volunteer coordination and feedback management.
- 📦 Improve the coordination of shelters and relief distribution.
- 💰 Strengthen donation, budgeting, and financial record management.
- 📊 Provide financial insights through reports and analytics.
- 💙 Support transparent and well-organized disaster relief operations.

## ✨ Main System Modules

| Module | Description |
|---|---|
| 🌊 **Disaster Management** | Manage disaster-related information and records. |
| 🧑‍🤝‍🧑 **Victim Management** | Maintain information about people affected by disasters. |
| 🤝 **Volunteer Management** | Support volunteer information and coordination. |
| 💬 **Feedback Management** | Collect and manage feedback related to relief activities and services. |
| 🏠 **Shelter Management** | Manage shelter-related information and resources. |
| 🚚 **Distribution Management** | Coordinate relief item distribution and related requests. |
| 💙 **Donation & Finance Management** | Manage donation records, budgets, expenses, and fund allocations. |
| 📈 **Financial Analytics & Reports** | Visualize financial information and generate monthly financial reports. |

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| ☕ Java 17 | Backend programming |
| 🍃 Spring Boot | Application framework |
| 🎨 Thymeleaf | Server-side page rendering |
| 🗄️ MySQL | Relational database |
| 🧱 Maven | Dependency and build management |
| 📊 Chart.js | Financial data visualization |
| 🌐 HTML5 & CSS3 | Page structure and styling |
| ⚡ JavaScript | Interactive functionality |
| 🖥️ Bootstrap | Responsive user interface components |

## 🏗️ System Architecture

The application follows a layered structure to separate user interface handling, business logic, and database operations.

```text
                 👤 User
                    |
                    ▼
          🌐 Thymeleaf Web Pages
                    |
                    ▼
          🎮 Spring Boot Controllers
                    |
                    ▼
              ⚙️ Service Layer
                    |
                    ▼
             🗄️ Repository Layer
                    |
                    ▼
                🐬 MySQL
```

## 💙 Donation & Finance Management

The Donation & Finance Management module supports the financial side of disaster relief operations.

Key functionality includes:

- 💝 Recording and managing donations.
- 🧾 Maintaining financial expense records.
- 📋 Managing budget requests and their statuses.
- ✅ Supporting budget approval and fund allocation workflows.
- 📊 Displaying financial analytics using charts.
- 📅 Generating monthly financial reports.
- 📄 Downloading monthly reports as PDF files.

*The exact availability of each feature depends on the implemented application version.*

## 🚀 Getting Started

Follow these steps to run the project locally.

### 1. Prerequisites

Make sure you have installed:

- ☕ JDK 17
- 🧱 Maven, or use the Maven Wrapper included with the project
- 🐬 MySQL Server
- 💻 IntelliJ IDEA or another Java IDE
- 🌐 A modern web browser

### 2. Clone the Repository

```bash
git clone https://github.com/ImashaFernando/FRDMS.git
cd FRDMS
```

### 3. Create the Database

Open MySQL and create the database:

```sql
CREATE DATABASE frdms_db;
```

### 4. Configure Database Connection

Open `src/main/resources/application.properties` and configure the connection using your local MySQL username and password.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/frdms_db
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

spring.jpa.hibernate.ddl-auto=update
```

**Note:** Keep your actual database password private. Check the existing project configuration before changing any settings.

### 5. Run the Application

On Windows PowerShell, from the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, open the project in IntelliJ IDEA and run the main Spring Boot application class.

### 6. Open the Application

After the application starts successfully, open the local URL configured for the project, usually:

```text
http://localhost:8080
```

## 📁 Project Structure

```text
FRDMS/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/frdms/frdms/
│       │       ├── controllers/
│       │       ├── services/
│       │       ├── repositories/
│       │       └── ...
│       └── resources/
│           ├── templates/
│           ├── static/
│           └── application.properties
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

*The package and folder names above are illustrative. Refer to the actual source tree for the complete structure.*

## 🌟 Why FRDMS Matters

Disaster relief requires timely coordination, reliable information, and responsible resource management. FRDMS aims to bring these activities together in one digital platform, helping the organization manage information more effectively and support communities during difficult times.

## 👩‍💻 Development Team

FRDMS is a collaborative academic software engineering project developed by a student team.

**Project Organization:** Healthy Lanka Alliance for Development

**Repository:** [FRDMS on GitHub](https://github.com/ImashaFernando/FRDMS)

## 💫 Our Vision

> To support a more connected, transparent, and efficient disaster relief process through technology — because every helping hand matters. 💙

---

<p align="center">
  Made with 💙, teamwork, and a purpose to make a difference.
</p>
