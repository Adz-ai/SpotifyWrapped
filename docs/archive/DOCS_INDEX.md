# Documentation Index

Welcome! Here's a guide to all the documentation for this project.

## 📚 Documentation Overview

### 🚀 Getting Started

1. **[QUICKSTART.md](QUICKSTART.md)** ⭐ START HERE
   - Step-by-step checklist to get the app running
   - Takes ~10 minutes
   - Perfect for first-time setup

2. **[README.md](README.md)**
   - Project overview and features
   - API endpoint documentation
   - Architecture explanation
   - Quick reference

### 🔐 Spotify Setup

3. **[SPOTIFY_SETUP.md](SPOTIFY_SETUP.md)** ⭐ IMPORTANT
   - Detailed Spotify Developer Dashboard setup
   - Step-by-step with explanations
   - Common issues and troubleshooting
   - OAuth flow explanations
   - Security best practices

### 🗺️ Implementation Guide

4. **[IMPLEMENTATION_ROADMAP.md](IMPLEMENTATION_ROADMAP.md)**
   - What works and what doesn't (yet)
   - How to implement user authorization
   - Future feature ideas
   - Development phases

### 🎨 Frontend (NEW!)

5. **[RUN_FULL_STACK.md](RUN_FULL_STACK.md)** ⭐ START HERE FOR FRONTEND
   - How to run both backend and frontend together
   - Quick 2-step startup guide
   - Troubleshooting tips
   - What to expect

6. **[FRONTEND_SETUP.md](FRONTEND_SETUP.md)**
   - Detailed frontend setup and features
   - Project structure explanation
   - Available commands and scripts
   - UI component breakdown

7. **[frontend/README.md](frontend/README.md)**
   - Technical frontend documentation
   - TypeScript/ESLint/Prettier configuration
   - Development workflow
   - Code quality tools

### 📁 Configuration Files

8. **[.env.example](.env.example)**
   - Template for environment variables
   - Copy to `.env` and fill in credentials

9. **[application.yml](src/main/resources/application.yml)**
   - Spring Boot configuration
   - API settings and defaults

## 🎯 What To Read Based on Your Goal

### Goal: Run the Full Stack App with Frontend
```
1. RUN_FULL_STACK.md
2. SPOTIFY_SETUP.md (if you haven't set up Spotify yet)
```

### Goal: Just Get Backend Running
```
1. QUICKSTART.md
2. SPOTIFY_SETUP.md (Section: "Step 2: Create a New App")
```

### Goal: Understand Why User Endpoints Don't Work
```
1. SPOTIFY_SETUP.md (Section: "Why It's Not Working")
2. IMPLEMENTATION_ROADMAP.md (Section: "What Doesn't Work Yet")
```

### Goal: Make User Endpoints Work
```
1. SPOTIFY_SETUP.md (Section: "Next Steps: Implementing User Authorization")
2. IMPLEMENTATION_ROADMAP.md (Section: "Phase 1")
```

### Goal: Understand the Architecture
```
1. README.md (Section: "Architecture")
2. Browse the code in src/main/java/org/adarssh/
```

### Goal: Add New Features
```
1. IMPLEMENTATION_ROADMAP.md (All Phases)
2. Spotify API docs (linked in resources)
```

## 🔧 Quick Commands Reference

### Build & Run
```bash
# Build
./gradlew clean build

# Run
export SPOTIFY_CLIENT_ID="your_id"
export SPOTIFY_CLIENT_SECRET="your_secret"
./gradlew bootRun
```

### Test Endpoints
```bash
# Health check
curl http://localhost:8080/api/health

# Top tracks (will fail without user auth)
curl http://localhost:8080/api/spotify/top/tracks?limit=5
```

### Environment Setup
```bash
# Copy environment template
cp .env.example .env

# Load environment variables (Unix/Mac)
export $(cat .env | xargs)
```

## ❓ Common Questions

**Q: Why don't the Spotify endpoints work?**
A: They require user authorization (OAuth). See [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md) "Why It's Not Working" section.

**Q: How do I set up Spotify Developer Dashboard?**
A: Follow [QUICKSTART.md](QUICKSTART.md) or detailed guide in [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md).

**Q: How do I implement user login?**
A: See [IMPLEMENTATION_ROADMAP.md](IMPLEMENTATION_ROADMAP.md) Phase 1.

**Q: Can I test without implementing OAuth?**
A: Yes! See [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md) "Temporary Workaround: Manual Token" section.

**Q: What Java version do I need?**
A: Java 21 or higher. Check with: `java -version`

## 📝 File Structure

```
SpotifyWrapped/
├── README.md                          # Main project documentation
├── QUICKSTART.md                      # Quick setup checklist
├── SPOTIFY_SETUP.md                   # Detailed Spotify setup guide
├── IMPLEMENTATION_ROADMAP.md          # Development roadmap
├── DOCS_INDEX.md                      # This file!
├── .env.example                       # Environment variable template
├── build.gradle.kts                   # Gradle build configuration
├── src/
│   ├── main/
│   │   ├── java/org/adarssh/
│   │   │   ├── SpotifyWrappedApplication.java  # Main class
│   │   │   ├── config/                # Configuration classes
│   │   │   ├── controller/            # REST endpoints
│   │   │   ├── service/               # Business logic
│   │   │   ├── dto/                   # Data transfer objects
│   │   │   └── exception/             # Error handling
│   │   └── resources/
│   │       └── application.yml        # Spring Boot config
│   └── test/                          # Tests (to be added)
└── old-code/                          # Original code (backup)
```

## 🎓 Learning Resources

### Spring Boot
- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2 Client](https://spring.io/guides/tutorials/spring-boot-oauth2/)

### Spotify API
- [Spotify Web API Docs](https://developer.spotify.com/documentation/web-api)
- [Spotify Authorization Guide](https://developer.spotify.com/documentation/web-api/concepts/authorization)
- [Spotify API Console (Testing)](https://developer.spotify.com/console/)

### Java 21
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)
- [Java Records](https://docs.oracle.com/en/java/javase/21/language/records.html)

## 💡 Tips

1. **Start with QUICKSTART.md** - Get the basics working first
2. **Read error messages** - They're usually helpful
3. **Check the logs** - Application prints detailed debug info
4. **Use the Spotify Console** - Test API calls directly
5. **Keep credentials secure** - Never commit them to git

## 🆘 Getting Help

If you're stuck:

1. ✅ Check the relevant documentation above
2. ✅ Look at the error logs (`logging.level.org.adarssh=DEBUG`)
3. ✅ Verify your Spotify Dashboard settings
4. ✅ Test with curl commands from the docs
5. ✅ Check Spotify API status page

## 📅 Documentation Updates

- **2025-01-15**: Initial documentation created
  - Quick start guide
  - Spotify setup guide
  - Implementation roadmap
  - This index

---

**Need something that's not here?** Feel free to add more documentation as you work on the project!
