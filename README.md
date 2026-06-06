
# Student Report

A brief description of what this project does and who it's for

## Tech Stack

### Backend

* **Language:** Kotlin, Java 17
* **Framework:** Spring Boot
* **Security:** Spring Security (Custom Session-based Token Authentication)
* **ORM:** Spring Data JPA / Hibernate
* **Database Migration:** Flyway
* **Relational Database:** PostgreSQL
* **Object Storage:** MinIO (S3-compatible storage for image uploads)
* **Template Engine:** Thymeleaf
* **Styling:** Bootstrap 5.3.8, Custom CSS
* **Icons:** Bootstrap Icons
* **Scripting:** Vanilla JavaScript (DOM manipulation, Fetch API)
* **Build Tool:** Gradle (Kotlin DSL)
* **Containerization:** Docker, Docker Compose
* **API Documentation:** OpenAPI 3.0 (YAML specification included)
## Features

- Light/dark mode toggle
- Live previews
- Fullscreen mode
- Cross platform


## Demo

Insert gif or link to demo

## Color Reference

| Color             | Hex                                                                |
| ----------------- | ------------------------------------------------------------------ |
| Example Color | ![#0a192f](https://dummyimage.com/10/0a192f/white?text=+) #0a192f |
| Example Color | ![#f8f8f8](https://dummyimage.com/10/f8f8f8/white?text=+) #f8f8f8 |
| Example Color | ![#00b48a](https://dummyimage.com/10/00b48a/white?text=+) #00b48a |
| Example Color | ![#00d1a0](https://dummyimage.com/10/00d1a0/white?text=+)) #00d1a0 |

## API Reference

**Base URL:** `/api/v1`

**Authentication:** Bearer Token required for protected routes (`Authorization: Bearer <token>`).

**Idempotency:** `Idempotency-Key` header (UUID v4) supported on `POST`, `PUT`, and `PATCH` requests for safe retries.

---

### Auth

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Register a new student account | No |
| `POST` | `/auth/login` | Login and obtain a Bearer token | No |
| `POST` | `/auth/logout` | Logout and invalidate the current session token | Yes |
| `GET` | `/auth/me` | Get the currently authenticated user | Yes |

---

### Users

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/users/me` | Get own profile | Yes |
| `PUT` | `/users/me` | Update own profile (name / email) | Yes |
| `PATCH` | `/users/me/password` | Change own password | Yes |
| `GET` | `/users/me/stats` | Get own report statistics | Yes |
| `GET` | `/users` | List all users | Yes (Admin) |
| `GET` | `/users/{id}` | Get user by ID | Yes (Admin) |
| `PUT` | `/users/{id}` | Update user by ID | Yes (Admin) |
| `DELETE` | `/users/{id}` | Delete user by ID | Yes (Admin) |
| `GET` | `/users/{id}/stats` | Get report statistics for a specific user | Yes (Admin) |

---

### Student Data

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/users/me/student-data` | Get own student data | Yes |
| `PATCH` | `/users/me/student-data` | Update own student data | Yes |
| `GET` | `/users/{id}/student-data` | Get student data for a user | Yes (Admin) |
| `PATCH` | `/users/{id}/student-data` | Update student data for a user | Yes (Admin) |

---

### Reports

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/reports` | List reports (paginated, supports filters) | Yes |
| `POST` | `/reports` | Create a new report (Initial status: Pending) | Yes |
| `GET` | `/reports/{id}` | Get report by ID | Yes |
| `PUT` | `/reports/{id}` | Update report content (Allowed if Pending) | Yes |
| `DELETE` | `/reports/{id}` | Soft-delete report | Yes |
| `PATCH` | `/reports/{id}/status` | Update report status | Yes (Admin) |

---

### Report Images

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/reports/{id}/images` | List images attached to a report | Yes |
| `POST` | `/reports/{id}/images` | Upload images for a report (Max 5MB each) | Yes |
| `DELETE` | `/reports/{id}/images/{imageId}` | Delete a specific image from a report | Yes |

---

### Buildings

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/buildings` | List all buildings (paginated) | Yes |
| `POST` | `/buildings` | Create a new building | Yes (Admin) |
| `GET` | `/buildings/{id}` | Get building by ID | Yes |
| `PUT` | `/buildings/{id}` | Update building | Yes (Admin) |
| `DELETE` | `/buildings/{id}` | Delete building | Yes (Admin) |
| `GET` | `/buildings/{id}/rooms` | List all rooms in a building | Yes |

---

### Rooms

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/rooms` | List all rooms (paginated, supports filters) | Yes |
| `POST` | `/rooms` | Create a new room | Yes (Admin) |
| `GET` | `/rooms/{id}` | Get room by ID | Yes |
| `PUT` | `/rooms/{id}` | Update room | Yes (Admin) |
| `DELETE` | `/rooms/{id}` | Delete room | Yes (Admin) |

---

### Categories

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/categories` | List all categories | Yes |
| `POST` | `/categories` | Create a new category | Yes (Admin) |
| `GET` | `/categories/{id}` | Get category by ID | Yes |
| `PUT` | `/categories/{id}` | Update category | Yes (Admin) |
| `DELETE` | `/categories/{id}` | Delete category | Yes (Admin) |

---

### Upvotes

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/reports/{id}/upvotes` | Get upvote summary and current-user status | Yes |
| `POST` | `/reports/{id}/upvotes` | Upvote a report (Once per user) | Yes |
| `DELETE` | `/reports/{id}/upvotes` | Remove own upvote from a report | Yes |

---

### Report Log

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/reports/{id}/logs` | Get audit log for a specific report | Yes |
| `GET` | `/report-logs` | List all report log entries | Yes (Admin) |
| `GET` | `/report-logs/{id}` | Get a single log entry by ID | Yes (Admin) |