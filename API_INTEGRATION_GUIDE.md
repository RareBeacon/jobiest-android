# Jobiest Native Mobile API Integration Guide

## Overview
The Jobiest Native Android Client communicates with the backend via two HTTPS channels:
1. **Supabase Auth API** (`https://cbxloutahmalorumaihc.supabase.co/auth/v1/`) for identity and session management using the safe public anon key.
2. **Jobiest REST API Gateway** (`https://www.jobiest.ai/api/`) for business logic, applications, AI tools, job discovery, and payments, authenticated via `Authorization: Bearer <access_token>`.

---

## 1. Authentication Endpoints (Supabase GoTrue)

### 1.1 Password Sign-In
* **URL**: `POST https://cbxloutahmalorumaihc.supabase.co/auth/v1/token?grant_type=password`
* **Headers**:
  ```http
  apikey: <SUPABASE_ANON_KEY>
  Content-Type: application/json
  ```
* **Request**:
  ```json
  { "email": "candidate@example.com", "password": "••••••••" }
  ```
* **Response (200 OK)**:
  ```json
  {
    "access_token": "eyJhbGciOi...",
    "token_type": "bearer",
    "expires_in": 3600,
    "refresh_token": "uG8...",
    "user": { "id": "uuid", "email": "candidate@example.com" }
  }
  ```

### 1.2 User Registration
* **URL**: `POST https://cbxloutahmalorumaihc.supabase.co/auth/v1/signup`
* **Request**:
  ```json
  {
    "email": "candidate@example.com",
    "password": "••••••••",
    "data": { "full_name": "Philip Ogungboye" }
  }
  ```

### 1.3 Token Refresh
* **URL**: `POST https://cbxloutahmalorumaihc.supabase.co/auth/v1/token?grant_type=refresh_token`
* **Request**:
  ```json
  { "refresh_token": "uG8..." }
  ```

---

## 2. Jobiest Core REST Endpoints (`https://www.jobiest.ai/api/`)

All requests require:
```http
Authorization: Bearer <access_token>
Accept: application/json
Content-Type: application/json
```

### 2.1 Profile & Preferences
- `GET /api/profile` — Retrieves user's profile row (`full_name`, `phone`, `target_roles`, `account_status`).
- `POST /api/profile` — Updates full name, phone number, and target roles.
- `GET /api/profile/completeness` — Returns profile completion score percentage (`percent: Int`) and missing sections.
- `GET /api/preferences` — Returns preferred locations, remote types, and application mode (`manual` vs `auto`).
- `POST /api/preferences/mode` — Switches application mode: `{ "mode": "auto" }` or `{ "mode": "manual" }`.

### 2.2 Job Discovery & Bookmarking
- `GET /api/jobs?q={query}&location={location}&source={source}&page={page}&limit={limit}`
  - Searches aggregated job board postings.
  - Automatically identifies whether each returned job is saved by the current user (`isSaved: true/false`).
- `GET /api/saved-jobs` — Lists all jobs bookmarked by the user.
- `POST /api/saved-jobs` — Bookmarks a job: `{ "jobId": "<uuid>" }`.
- `DELETE /api/saved-jobs?jobId=<uuid>` — Removes a bookmarked job.

### 2.3 Application Tracking & Auto-Apply Pipeline
- `GET /api/applications` — Lists candidate applications across all stages.
- `POST /api/applications` — Manually logs an external application:
  ```json
  {
    "mode": "manual",
    "company": "Stripe",
    "title": "Software Engineer",
    "url": "https://stripe.com/jobs/123",
    "status": "SUBMITTED"
  }
  ```
- `POST /api/applications/target` — Directs the AI Agent to target any career link:
  ```json
  { "url": "https://boards.greenhouse.io/company/jobs/456" }
  ```
- `POST /api/applications/{id}/approve` — Approves a drafted application.
- `POST /api/applications/{id}/auto-submit` — Queues for automated browser submission.
- `POST /api/applications/{id}/reject` — Rejects a generated application draft.
- `POST /api/applications/{id}/withdraw` — Withdraws an active application.

### 2.4 AI Career Tools & ATS
- `POST /api/ats/scan` — Runs ATS keyword match analysis:
  ```json
  {
    "resumeText": "...",
    "jobDescription": "..."
  }
  ```
- `POST /api/free-tools/generate` — Executes tailored AI career accelerators:
  ```json
  {
    "toolId": "free-cover-letter-writer",
    "answers": { "role": "Senior Android Engineer", "context": "10 years Kotlin experience" }
  }
  ```

### 2.5 Billing & Monetization
- `GET /api/entitlements` — Returns current plan (`FREE`, `BASIC`, `PREMIUM`, `MAX`), daily AI generation credits remaining, and monthly auto-apply quotas.
- `POST /api/billing/flutterwave/create` — Generates a Flutterwave checkout session:
  ```json
  {
    "plan": "PREMIUM",
    "provider": "flutterwave",
    "redirectUrl": "https://www.jobiest.ai/billing/success"
  }
  ```
