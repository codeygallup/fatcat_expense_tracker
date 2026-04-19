<a name="readme-top"></a>

# Fatcat ![fatcat](/client/public/favicon.ico)  [<img align="right" src="https://img.shields.io/badge/license-MIT-00beef"></img>](LICENSE) [<img align="right" src="https://img.shields.io/badge/version-1.0.0-14b8a6"></img>](#)

### Description

A full-stack personal finance web app for tracking accounts, transactions, and bills. Built with Vue 3 and Spring Boot.

  <p align="right"><a href="#readme-top">(Return to top)</a></p>

## Table of Contents

[Description](#description) • [Demo](#demo) • [Features](#features) • [Installation](#installation) • [Technologies](#technologies)

### Demo

**🔗 Live Site:** [https://fatcat-expense-tracker.vercel.app](https://fatcat-expense-tracker.vercel.app)
_(Server may take a few seconds to spin up — hang tight!)_

### Demo Access

You can explore Fatcat without creating an account using the demo credentials below:

Email: `demo@fatcat.com`

Password: `Demo1234!`

> Demo account is monitored and may be reset periodically.

  <p align="right"><a href="#readme-top">(Return to top)</a></p>

### Features

- 🔐 **Secure Authentication** — JWT-based auth with register, login, and logout
- 🏦 **Account Management** — Track checking, savings, and credit card accounts
- 💸 **Transaction Tracking** — Log deposits and withdrawals with category support
- 📋 **Bill Tracking** — Manage recurring bills with due dates and paid/unpaid/overdue statuses
- 📊 **Dashboard** — Account summaries and recent transaction history at a glance

  <p align="right"><a href="#readme-top">(Return to top)</a></p>

### Installation

**Prerequisites:** Java 21, Maven, Node.js 18+, PostgreSQL

1. Clone the repo:

```bash
git clone https://github.com/codeygallup/fatcat_expense_tracker.git
cd fatcat_expense_tracker
```

2. Create `server/src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fatcat
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secret=your_secret
jwt.expiration=86400000
```

3. Run the backend:

```bash
cd server
mvn spring-boot:run -Dspring.profiles.active=local
```

4. Create `client/.env.local`:

```
VITE_API_URL=http://localhost:8080/api
```

5. Run the frontend:

```bash
cd client
npm install && npm run dev
```

  <p align="right"><a href="#readme-top">(Return to top)</a></p>

### Technologies

**Frontend:**

- ![Vue.js](https://img.shields.io/badge/vuejs-%2335495e.svg?style=for-the-badge&logo=vuedotjs&logoColor=%234FC08D)
- ![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
- ![TailwindCSS](https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white)
- ![Vite](https://img.shields.io/badge/vite-%23646CFF.svg?style=for-the-badge&logo=vite&logoColor=white)

**Backend:**

- ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
- ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
- ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
- ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

**Deployment:**

- ![Vercel](https://img.shields.io/badge/vercel-%23000000.svg?style=for-the-badge&logo=vercel&logoColor=white)
- ![Render](https://img.shields.io/badge/Render-%46E3B7.svg?style=for-the-badge&logo=render&logoColor=white)

  <p align="right"><a href="#readme-top">(Return to top)</a></p>

#### Links

**Author:** Codey Gallup
**GitHub:** [@codeygallup](https://github.com/codeygallup)
**Email:** codey.gallup@gmail.com

  <p align="right"><a href="#readme-top">(Return to top)</a></p>