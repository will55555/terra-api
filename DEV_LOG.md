# Terra API Dev Log

## Phase 1 — Project Initialization

**Date:** 2026-06-06  
**Status:** Complete

### Goal
Establish a Spring Boot 3.5.14 API foundation with both sync and async request handling 
capability. Ready for adding domain logic and persistence layers.

### Stack & Architecture

| Component | Choice | Why |
|---|---|---|
| Framework | Spring Boot 3.5.14 | Modern LTS version, latest Spring ecosystem |
| Language | Java 21 | Latest LTS JDK, virtual threads for concurrency |
| Request Handling | Web + WebFlux | Supports both traditional servlet and reactive stacks |
| Build | Gradle Kotlin | Type-safe, modern build definition |
| Persistence | (Pending) | Not yet added |

### Key Design Decision: Dual-stack (Web + WebFlux)

Included both `spring-boot-starter-web` and `spring-boot-starter-webflux` to support
both traditional servlet-based endpoints and reactive endpoints from day one. This
avoids later refactor if async/streaming is needed.

**Alternatives considered:**
- Web-only (simpler initially, requires refactor if reactive needed later)
- WebFlux-only (enforces async everywhere, steeper learning curve)

**Choice rationale:** Maximum flexibility. Start with Web, adopt WebFlux selectively
for high-concurrency routes without rebuilding.

### Included Dependencies

- **Actuator:** Health checks, metrics, endpoints for observability
- **Cache:** Spring Cache abstractions ready for performance optimization
- **Lombok:** Reduces boilerplate (`@Data`, `@Getter`, `@Setter`)
- **JUnit 5 + Reactor Test:** Testing foundation for both sync and async code

### Files Created

**`src/main/java/com/terra/api/TerraApiApplication.java`** *(generated)*  
Entry point. Runs `SpringApplication.run()` with the auto-configuration class.

**`build.gradle.kts`** *(generated)*  
Gradle build definition in Kotlin DSL. Java toolchain pinned to 21.

**`src/main/resources/application.yaml`** *(generated)*  
Application name set to `terra-api`. Ready for profiles (dev, test, prod).

### What's Missing (Intentional)

- **Database:** Pending domain model design. Will add Spring Data JPA/R2DBC.
- **Security:** No Spring Security yet. Add when auth requirements are clear.
- **API Docs:** No Springdoc OpenAPI yet. Deferring until endpoints exist.
- **Configurations:** No custom configs or beans yet.

### Next Phase

Phase 2 will add:
1. Domain models (entities)
2. Persistence layer (JPA repositories)
3. First REST controller with basic CRUD