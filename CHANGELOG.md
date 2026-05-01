# Changelog

## [1.0.1] - 2026-05-01

### Added

- Bean validation annotations on all entity fields

### Fixed

- Expired/invalid JWT tokens crashing requests instead of falling through
- 404 on hard refresh of client-side routes (Vercel SPA rewrite rule)

## [1.0.0] - 2026-04-19

### Added

- User authentication (register, login, JWT-based sessions)
- Account management (checking, savings, credit card)
- Transaction tracking with categories and deposit/withdrawal support
- Bill tracking with recurring bills, due dates, and status (paid, unpaid, overdue)
- Dashboard with account summaries and recent transactions
- Demo account for portfolio review
