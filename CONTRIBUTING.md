# Contributing to Spotify Wrapped

Thank you for your interest in contributing to Spotify Wrapped! This document provides guidelines and instructions for contributing to the project.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)
- [Reporting Bugs](#reporting-bugs)
- [Feature Requests](#feature-requests)

## 🤝 Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive feedback
- Respect differing viewpoints and experiences

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Node.js 18+** and npm (for frontend)
- **Git**
- **Spotify Developer Account** ([Create one here](https://developer.spotify.com))

### First Time Setup

1. **Fork the repository** on GitHub
2. **Clone your fork**:
   ```bash
   git clone https://github.com/YOUR-USERNAME/SpotifyWrapped.git
   cd SpotifyWrapped
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/original-owner/SpotifyWrapped.git
   ```
4. **Set up Spotify credentials**:
   ```bash
   cp .env.example .env
   # Edit .env with your Spotify Client ID and Secret
   ```

## 💻 Development Setup

### Backend Setup

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run the application (development mode)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

### Environment Profiles

The project supports multiple environments:

- **dev** - Development environment (verbose logging, all actuator endpoints)
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=dev'
  ```

- **staging** - Staging environment (moderate logging, limited endpoints)
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=staging'
  ```

- **prod** - Production environment (minimal logging, security hardened)
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=prod'
  ```

## 🔄 Development Workflow

### 1. Create a Branch

Always create a new branch for your changes:

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/bug-description
```

**Branch naming conventions:**
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation changes
- `test/` - Test additions or modifications
- `refactor/` - Code refactoring
- `chore/` - Maintenance tasks

### 2. Make Your Changes

- Write clean, readable code
- Follow existing code style
- Add tests for new functionality
- Update documentation as needed

### 3. Test Your Changes

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "ClassName"

# Check code quality
./gradlew checkstyleMain spotbugsMain

# Verify test coverage
./gradlew jacocoTestReport
# Open build/reports/jacoco/test/html/index.html
```

### 4. Commit Your Changes

Follow the [commit message guidelines](#commit-messages) below.

```bash
git add .
git commit -m "feat: Add new feature description"
```

### 5. Push and Create PR

```bash
git push origin feature/your-feature-name
```

Then create a Pull Request on GitHub.

## 📝 Coding Standards

### Java Code Style

- **Follow Google Java Style Guide** (enforced by Checkstyle)
- **Use Java 21 features**: records, pattern matching, var, etc.
- **Maximum line length**: 120 characters
- **Imports**: No wildcard imports (use explicit imports)
- **Javadoc**: Required for public methods and classes

### Code Quality Tools

The project uses automated code quality checks:

- **Checkstyle** - Code style verification
  ```bash
  ./gradlew checkstyleMain checkstyleTest
  ```

- **SpotBugs** - Static bug detection
  ```bash
  ./gradlew spotbugsMain
  ```

- **JaCoCo** - Test coverage (90% minimum)
  ```bash
  ./gradlew jacocoTestCoverageVerification
  ```

All checks must pass before merging!

### Best Practices

#### ✅ Do:
- Write meaningful variable and method names
- Keep methods short and focused (< 30 lines)
- Use records for DTOs
- Add validation to API endpoints
- Write unit AND integration tests
- Log important events with appropriate levels
- Handle exceptions gracefully
- Use Optional instead of null returns

#### ❌ Don't:
- Commit commented-out code
- Use magic numbers (use constants)
- Catch generic Exception without re-throwing
- Expose sensitive information in logs
- Skip writing tests
- Push code that doesn't compile
- Ignore Checkstyle or SpotBugs warnings

## 🧪 Testing Guidelines

### Test Coverage Requirements

- **Minimum overall coverage**: 90%
- **Minimum per-class coverage**: 80%
- **All public methods must be tested**

### Test Structure

Follow the **Given-When-Then** pattern:

```java
@Test
void getTopTracksReturnsExpectedData() {
    // given
    int limit = 10;
    String timeRange = "medium_term";

    // when
    var result = spotifyService.getTopTracks(limit, timeRange);

    // then
    assertThat(result).isNotNull();
    assertThat(result.items()).hasSize(10);
}
```

### Test Types

1. **Unit Tests** - Test individual components in isolation
   ```java
   @Test
   void testMethodInIsolation() { }
   ```

2. **Integration Tests** - Test multiple components together
   ```java
   @SpringBootTest
   @ActiveProfiles("test")
   class IntegrationTest { }
   ```

3. **Controller Tests** - Test REST endpoints
   ```java
   @WebMvcTest(SpotifyController.class)
   class ControllerTest { }
   ```

## 📋 Commit Messages

We follow **Conventional Commits** specification:

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, missing semicolons)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks, dependencies

### Examples

```bash
feat: Add time range selector to wrapped endpoint

Implemented time_range parameter (short_term, medium_term, long_term)
to allow users to customize their listening period.

Closes #123
```

```bash
fix: Handle null display_name in OAuth2User

Fixed NullPointerException when user's display name is not available
from Spotify API.

Fixes #456
```

```bash
test: Add integration tests for AOP proxy methods

Created SpotifyServiceAopIntegrationTest to test methods that require
Spring AOP context for @Cacheable annotations.
```

## 🔀 Pull Request Process

### Before Submitting

- [ ] All tests pass locally
- [ ] Code follows style guidelines
- [ ] Checkstyle and SpotBugs pass
- [ ] Test coverage meets requirements (90%+)
- [ ] Documentation is updated
- [ ] Commit messages follow conventions
- [ ] Branch is up to date with main

### PR Template

When creating a PR, include:

1. **Description**: What does this PR do?
2. **Motivation**: Why is this change needed?
3. **Changes**: List of changes made
4. **Testing**: How was this tested?
5. **Screenshots**: If UI changes
6. **Related Issues**: Closes #123

### Review Process

- At least one approval required
- All CI checks must pass
- Address review comments
- Keep PR focused and small (<500 lines if possible)

## 🐛 Reporting Bugs

### Before Reporting

1. Check if the bug has already been reported
2. Verify the bug exists in the latest version
3. Collect relevant information

### Bug Report Template

```markdown
**Describe the bug**
A clear description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
What you expected to happen.

**Actual behavior**
What actually happened.

**Environment**
- OS: [e.g., macOS 14.0]
- Java Version: [e.g., Java 21]
- Browser: [if applicable]

**Logs**
```
Paste relevant logs here
```

**Screenshots**
If applicable, add screenshots.
```

## 💡 Feature Requests

We welcome feature requests! Please:

1. Check if the feature already exists
2. Check if it's already requested
3. Provide clear use case and benefits
4. Be open to discussion and alternatives

### Feature Request Template

```markdown
**Is your feature request related to a problem?**
A clear description of the problem.

**Describe the solution you'd like**
What you want to happen.

**Describe alternatives you've considered**
Other solutions you've thought about.

**Additional context**
Any other context, mockups, or examples.
```

## 📞 Questions?

- **Documentation**: Check [README.md](README.md) and other docs
- **Issues**: Browse [existing issues](https://github.com/owner/repo/issues)
- **Discussions**: Start a [discussion](https://github.com/owner/repo/discussions)

## 🎉 Recognition

Contributors will be:
- Listed in release notes
- Added to CONTRIBUTORS.md (if substantial contributions)
- Credited in commit co-authoring when applicable

---

Thank you for contributing to Spotify Wrapped! 🎵✨
