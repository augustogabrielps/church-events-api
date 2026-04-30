# Serve API

Backend API for a community volunteer and event management platform.

---

## 📖 Overview

Serve is a mobile-first platform designed to help churches and communities organize volunteers for events in a structured and efficient way.

The main goal is to replace informal processes (such as WhatsApp groups and spreadsheets) with a clear system that improves organization, participation, and ultimately the impact of community actions.

---

## 🎯 Purpose

Many community events rely on volunteers but suffer from:

* lack of organization
* unclear responsibilities
* last-minute shortages
* poor visibility of who is helping

Serve solves this by providing:

* structured event creation
* role-based volunteer assignment
* real-time visibility for leaders

---

## 🧱 Core Features (MVP)

* Create events
* Define event dates
* Define volunteer roles (e.g., kitchen, reception, cleaning)
* Allow users to sign up for roles and dates
* Provide a dashboard for leaders to track participation

---

## 🚀 Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Flyway (database migrations)
* PostgreSQL

### Infrastructure

* Supabase (PostgreSQL hosting)
* Docker (local development)
* GitHub (version control)

---

## ⚙️ Environment Configuration

This project uses environment variables to handle sensitive data.

Required variables:

```bash
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
```

---

## 🗄️ Database

* PostgreSQL hosted on Supabase
* Managed via Flyway migrations
* No schema is created manually

---

## ▶️ Running the Application

### Using Maven Wrapper

```bash
./mvnw spring-boot:run -DskipTests
```

### Using IntelliJ

Run the main class:

```
ServeApiApplication
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

Note: Tests currently load the full Spring context and require a valid database configuration.

---

## 📂 Project Structure

```
src/
 ├── main/
 │   ├── java/com/serve/
 │   └── resources/
 │       ├── application.properties
 │       └── db/migration/
 └── test/
```

---

## 🔄 Database Migrations

Migrations are handled via Flyway.

Location:

```
src/main/resources/db/migration
```

Naming convention:

```
V1__initial_schema.sql
V2__add_new_feature.sql
```

---

## 🔐 Security

* Credentials are not stored in the repository
* Environment variables are used for sensitive data
* Future versions will include authentication (JWT)

---

## 🧭 Roadmap

### Phase 1 (Current)

* Volunteer management
* Event and role organization

### Phase 2

* Ticket sales
* Payment tracking

### Phase 3

* Financial reporting
* Event profitability tracking

### Phase 4

* Notifications
* Mobile app integration

---

## 🤝 Contribution

This is currently a personal project, but contributions and suggestions are welcome.

---

## 📌 Status

🚧 In development — MVP phase

---

## 📄 License

To be defined
