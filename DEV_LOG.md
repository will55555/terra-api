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

### GitHub Remote Setup & SSH Authentication

#### What This Is and Why It Matters

A **remote** is a connection link between your local git repo and a server (GitHub in this case).
It tells git where to push commits and pull updates from. SSH authentication is a cryptographic 
key-pair method — more secure and convenient than HTTPS + passwords/tokens because:

- **No credentials in memory:** SSH uses public/private key pairs instead of passwords
- **No token expiration:** Keys don't expire; tokens require periodic renewal
- **Automation-friendly:** CI/CD can use SSH keys without exposing secrets
- **Standard in industry:** All major platforms (GitHub, GitLab, Bitbucket) use SSH by default

#### How SSH Authentication Works (Simplified)

1. **Key pair generation:** You generate a public key (safe to share) and private key (secret, never shared)
2. **GitHub stores public key:** You add your public key to GitHub at Settings > SSH and GPG keys
3. **Git uses private key:** When you push/pull, git signs requests with your private key
4. **GitHub verifies:** GitHub verifies the signature using your stored public key
5. **Access granted:** If valid, the request succeeds without passwords

#### Step-by-Step: What We Did

```bash
# 1. Initialize git in the project directory (local-only, no network yet)
git init
# Creates .git/ folder that tracks all version history

# 2. Stage all files from Spring Initializer
git add .
# Prepares all files (src/, build.gradle.kts, .gitignore, etc.) for the first commit

# 3. Create the initial commit (local)
git commit -m "Initial commit: Spring Boot project initialized..."
# Snapshots the current state. This lives in .git/ until we connect to a remote

# 4. Configure the remote connection to GitHub
git remote add origin git@github.com:will55555/terra-api.git
#
# Breaking this down:
# - 'git remote add'     = tell git about a new server
# - 'origin'              = nickname for this remote (convention: "origin" = main remote)
# - 'git@github.com:...' = SSH protocol (alternative: https://github.com/... for HTTPS)
#   - 'git'               = username for SSH
#   - 'github.com'        = GitHub's SSH server
#   - 'will55555/terra-api.git' = the repo path on GitHub
#

# 5. Push commits to GitHub (first time)
git push -u origin master
#
# Breaking this down:
# - 'git push'       = send local commits to remote
# - '-u'             = "upstream" flag, sets origin/master as tracking branch
#                      (future 'git pull' without args will know to pull from here)
# - 'origin'         = the remote we configured above
# - 'master'         = the branch to push (this project uses master, not main)
#
# Result: GitHub now has your commits, and your local branch tracks the remote
```

#### How to Reproduce on a New Machine

If you clone this repo or set it up fresh elsewhere:

```bash
# If starting fresh with the downloaded project:
cd ~/projects/terra-api
git init
git add .
git commit -m "Initial commit: Spring Boot project initialized"
git remote add origin git@github.com:will55555/terra-api.git
git push -u origin master

# If cloning an existing repo from GitHub:
git clone git@github.com:will55555/terra-api.git
# This does init + remote + pull in one command
```

#### SSH Key Prerequisites (What Must Be True First)

Before the above commands work, SSH keys must be installed:

**Check if you have SSH keys:**
```bash
ls -la ~/.ssh/
# Look for id_rsa (private) and id_rsa.pub (public)
# On Windows: dir %userprofile%\.ssh\
```

**If no keys exist, generate them:**
```bash
ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa
# Generates a 4096-bit RSA key pair
# Press Enter twice (no passphrase) for passwordless authentication
# Result: ~/.ssh/id_rsa (private, chmod 600) and ~/.ssh/id_rsa.pub (public)
```

**Add public key to GitHub:**
1. Copy your public key: `cat ~/.ssh/id_rsa.pub`
2. Go to https://github.com/settings/keys
3. Click "New SSH key"
4. Paste the public key content
5. Title it (e.g., "My Dev Machine")
6. Click "Add SSH key"

**Test the connection:**
```bash
ssh -T git@github.com
# Expected output: "Hi will55555! You've successfully authenticated..."
```

#### Why SSH Over HTTPS

| Aspect | SSH | HTTPS |
|--------|-----|-------|
| Authentication | Public/private key pair | Username + password/token |
| Security | Cryptographic signing | Bearer token in memory |
| Token expiry | Keys don't expire | Tokens expire (e.g., 30 days) |
| Automation | Safe for CI/CD | Risky; requires secret management |
| Ease | One-time setup | Repeated renewals |

**Decision:** Chose SSH because this is a personal dev machine with long-lived repos.
SSH requires setup once, then "just works" for all future repos on that machine.

#### Troubleshooting

**Problem:** `Permission denied (publickey)`
- **Cause:** GitHub doesn't have your public key, or private key isn't accessible to git
- **Fix:** Verify `~/.ssh/id_rsa.pub` content is in https://github.com/settings/keys

**Problem:** `Could not resolve hostname github.com`
- **Cause:** Network issue or GitHub is down
- **Fix:** Check internet, wait, or verify DNS: `nslookup github.com`

**Problem:** `fatal: not a git repository`
- **Cause:** Running git commands outside a repo directory or in a directory without `.git/`
- **Fix:** `cd` into the project root and verify `.git/` exists

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