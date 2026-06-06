# Terra API — Claude Code Context

## Objective
Spring Boot 3.5.14 API for Notion integration with dashboard and webhook support. Building a flexible dual-stack (Web + WebFlux) foundation for sync and async request handling.

## Stack
- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.5.14 (Web + WebFlux)
- **Build:** Gradle Kotlin DSL
- **Config:** dotenv-java for .env support
- **Testing:** JUnit 5 + Reactor Test
- **Observability:** Spring Actuator (health, metrics)

## Current State
- ✅ Project initialization complete (Phase 1)
- ✅ Notion integration layer (Phase 1.5): NotionService, models, controllers wired
- ✅ Dashboard endpoint (GET /api/dashboard) verified operational with graceful error handling
- ✅ Webhook controller scaffolded
- ✅ Configuration layer (EnvConfig, CacheConfig, WebClientConfig) in place
- ✅ Error handling hardened: graceful degradation (empty arrays) on Notion API failures
- ✅ Logging added to DashboardController and NotionService for observability
- ✅ App tested: running on port 8081, health endpoint returns UP
- ✅ Commit d80dc49 pushed to origin/master
- ⏳ Database persistence layer pending (no JPA/R2DBC yet)

## Next Action
Expand Notion query capabilities or begin database persistence design (entities, JPA repositories).

## Open Blockers
None active. Database/persistence design is intentionally pending; no urgent blockers.

## Key Decisions Log
| Date | Decision | Rationale |
|---|---|---|
| 2026-06-06 | Graceful degradation on API failures | Return empty arrays instead of 500 errors when Notion API fails; improves reliability and UX (partial data beats no data) |
| 2026-06-06 | Comprehensive error logging | Log all exceptions in reactive chains (onErrorResume) even when handled gracefully; enables observability without breaking flows |
| 2026-06-06 | Dual-stack (Web + WebFlux) | Maximum flexibility—start with Web, adopt reactive selectively without refactor |
| 2026-06-06 | .env via dotenv-java | Secure config management; avoids hardcoding secrets in application.yaml |
| 2026-06-06 | Spring Actuator included | Observability from day one (health checks, metrics for production readiness) |