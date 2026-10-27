#  AI Social Media Agent

An AI-powered social media management platform built with **Java Spring Boot** that allows users to connect multiple social media platforms and manage their posts from a single application.

The long-term goal of this project is to allow a user to create a post once and publish it across multiple social media platforms such as **LinkedIn, Facebook, Instagram, and X (Twitter)** through their respective APIs.

---

##  Project Goal

Managing content across multiple social media platforms can be time-consuming.

This project aims to provide a single platform where users can:

- 🔐 Create an account and securely log in
- 🔗 Connect their social media accounts
- ✍️ Create posts from one dashboard
- 🤖 Use AI to generate or improve post content
- 📤 Publish posts to multiple platforms
- 📊 Track publishing status
- 🗓️ Eventually schedule posts
- 🔄 Manage connected social media accounts

### Example

Instead of manually posting:

```text
LinkedIn
Facebook
Instagram
X
```

the user will eventually be able to create:

```text
"5 tips for learning Java in 2026"
```

once and publish it to multiple connected platforms.

---

# 🛠️ Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven

### Authentication

- JWT (JSON Web Token)
- BCrypt password hashing
- Spring Security

### Social Media Integration

Currently developing:

- LinkedIn OAuth 2.0
- LinkedIn API

Planned:

- Facebook
- Instagram
- X (Twitter)
- Other platforms

### Development Tools

- IntelliJ IDEA
- PostgreSQL
- Postman
- Git & GitHub

---

# 🏗️ Current Architecture

The application currently follows a layered Spring Boot architecture:

```text
Client / Postman
       │
       ▼
┌─────────────────────┐
│     Controller      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│      Service        │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│     Repository      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│    PostgreSQL DB    │
└─────────────────────┘
```

Authentication is handled separately through Spring Security:

```text
Request
   │
   ▼
JWT Authentication Filter
   │
   ▼
Validate JWT
   │
   ▼
Spring Security
   │
   ▼
Controller
```

---

#  Authentication

Users can register and log in using the application's authentication system.

The authentication flow is:

```text
Register
   │
   ▼
Password hashed with BCrypt
   │
   ▼
User stored in PostgreSQL
   │
   ▼
Login
   │
   ▼
JWT generated
   │
   ▼
JWT used for protected API requests
```

Protected requests use:

```http
Authorization: Bearer <JWT>
```

---

# 🔗 LinkedIn OAuth

The project currently implements the beginning of the LinkedIn OAuth 2.0 connection flow.

The current flow is:

```text
User
 │
 │ Login
 ▼
AI Social Media Agent
 │
 │ /api/oauth/linkedin/authorize
 ▼
LinkedIn
 │
 │ User authorizes application
 ▼
LinkedIn Callback
 │
 │ authorization code + state
 ▼
Spring Boot
 │
 │ Validate OAuth state
 ▼
PostgreSQL
 │
 │ Identify user
 ▼
LinkedIn Token Endpoint
 │
 │ Exchange authorization code
 ▼
Access Token
```

### OAuth State Protection

The application generates a random OAuth `state` value and stores it in PostgreSQL.

Example:

```text
state
------------------------------------
8d758484-f5e0-464a-8e5c-c63099d1623a
```

The state is associated with:

```text
User Email
Expiration Time
```

When LinkedIn redirects back to the application, the state is verified before continuing the OAuth process.

This helps protect the OAuth flow against CSRF-related attacks.

---

#  Project Structure

The project is organized approximately as follows:

```text
src/
└── main/
    └── java/
        └── com.example.demo/
            │
            ├── config/
            │   └── SecurityConfig.java
            │
            ├── controller/
            │   ├── UserController.java
            │   └── LinkedInOAuthController.java
            │
            ├── dto/
            │   └── LinkedInTokenResponse.java
            │
            ├── entity/
            │   ├── User.java
            │   └── OAuthState.java
            │
            ├── repository/
            │   ├── UserRepository.java
            │   └── OAuthStateRepository.java
            │
            ├── security/
            │   ├── JwtAuthenticationFilter.java
            │   ├── JwtService.java
            │   └── CustomUserDetailsService.java
            │
            └── service/
                ├── UserService.java
                └── LinkedInOAuthService.java
```

---

#  Database

The application uses **PostgreSQL**.

Current database entities include:

### User

Stores application users.

```text
User
├── id
├── fullName
├── email
├── password
├── provider
├── createdAt
└── updatedAt
```

### OAuthState

Temporarily stores OAuth transactions.

```text
OAuthState
├── id
├── state
├── userEmail
└── expiresAt
```

More social-media-related entities will be added as development continues.

---

#  API Endpoints

## Authentication

### Register

```http
POST /api/auth/register
```

### Login

```http
POST /api/auth/login
```

---

## Users

### Create User

```http
POST /api/users
```

---

## LinkedIn

### Start LinkedIn OAuth

```http
GET /api/oauth/linkedin/authorize
```

Requires authentication.

```http
Authorization: Bearer <JWT>
```

### LinkedIn OAuth Callback

```http
GET /api/oauth/linkedin/callback
```

This endpoint receives the authorization code and OAuth state from LinkedIn.

---

# 🧪 Testing

The API is currently being tested using **Postman**.

Typical authentication flow:

```text
1. Register
      ↓
2. Login
      ↓
3. Receive JWT
      ↓
4. Add JWT to Authorization header
      ↓
5. Access protected endpoints
```

LinkedIn connection:

```text
1. Login
      ↓
2. Call /api/oauth/linkedin/authorize
      ↓
3. Open returned LinkedIn URL
      ↓
4. Authorize application
      ↓
5. LinkedIn redirects to callback
      ↓
6. Validate OAuth state
      ↓
7. Exchange authorization code
      ↓
8. Receive LinkedIn access token
```

---

# 🗺️ Development Roadmap

## ✅ Completed

- [x] Spring Boot project setup
- [x] PostgreSQL configuration
- [x] User entity
- [x] User registration
- [x] Password hashing
- [x] JWT authentication
- [x] JWT authentication filter
- [x] Protected API endpoints
- [x] LinkedIn OAuth authorization URL
- [x] LinkedIn OAuth callback
- [x] OAuth state generation
- [x] OAuth state storage in PostgreSQL
- [x] OAuth state validation
- [x] OAuth state expiration
- [x] OAuth state deletion after use
- [x] Authorization code → access-token exchange implementation

##  Currently Working On

- [ ] Save LinkedIn connection to PostgreSQL
- [ ] Store LinkedIn access token securely
- [ ] Retrieve LinkedIn profile information
- [ ] Create LinkedIn post API integration
- [ ] Publish a post through LinkedIn
- [ ] Handle token expiration

##  Planned

- [ ] Facebook integration
- [ ] Instagram integration
- [ ] X (Twitter) integration
- [ ] Multiple social accounts per user
- [ ] Unified post creation
- [ ] AI post generation
- [ ] AI content improvement
- [ ] Automatic hashtag generation
- [ ] Image generation
- [ ] Scheduled posts
- [ ] Publishing history
- [ ] Post analytics
- [ ] Web dashboard
- [ ] Docker deployment
- [ ] Cloud deployment

---

#  Security Notes

Sensitive credentials should **never** be committed to GitHub.

Do not commit:

```text
application.properties
```

if it contains:

```properties
linkedin.client-secret=YOUR_SECRET
```

Instead, use environment variables:

```properties
linkedin.client-id=${LINKEDIN_CLIENT_ID}
linkedin.client-secret=${LINKEDIN_CLIENT_SECRET}
```

Then configure the variables in your local environment.

Also never commit:

- JWT secrets
- OAuth access tokens
- OAuth refresh tokens
- Database passwords
- API keys
- Client secrets

---

# 🎯 Long-Term Vision

The final application is intended to work as a centralized AI-powered social media assistant.

```text
                    ┌──────────────┐
                    │     User     │
                    └──────┬───────┘
                           │
                           ▼
                ┌────────────────────┐
                │ AI Social Media    │
                │      Agent         │
                └─────────┬──────────┘
                          │
              ┌───────────┼───────────┐
              │           │           │
              ▼           ▼           ▼
          LinkedIn     Facebook       X
              │           │           │
              ▼           ▼           ▼
           Publish     Publish      Publish
```

The ultimate goal is:

> **Create once. Improve with AI. Publish everywhere.**

---

# 👨‍💻 Author

**Jerry Joydhor**

Computer Science student and software engineering enthusiast.

GitHub: [Jerry1921](https://github.com/Jerry1921)

---

# 📄 License

This project is currently being developed as a personal learning and portfolio project.
