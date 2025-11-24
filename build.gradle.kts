plugins {
    java
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    checkstyle
    id("com.github.spotbugs") version "6.4.5"
    jacoco
}

group = "org.adarssh"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    // Spring Security & OAuth2
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Logging
    implementation("org.springframework.boot:spring-boot-starter-logging")

    // API Documentation (Swagger/OpenAPI)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Monitoring and Health Checks
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Caching
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")

    // Resilience (Retry, Circuit Breaker)
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Rate Limiting
    implementation("com.bucket4j:bucket4j-core:8.10.1")

    // Lombok (optional but recommended for reducing boilerplate)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Development tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("--enable-preview")
}

// Checkstyle configuration
checkstyle {
    toolVersion = "10.12.7"
    configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
}

tasks.withType<Checkstyle> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// SpotBugs configuration
spotbugs {
    toolVersion.set("4.8.3")
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.MEDIUM)
    ignoreFailures.set(false)
    excludeFilter.set(file("${project.rootDir}/config/spotbugs/excludeFilter.xml"))
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    reports.create("html") {
        required.set(true)
        outputLocation.set(file("${project.buildDir}/reports/spotbugs/spotbugs.html"))
    }
    reports.create("xml") {
        required.set(true)
        outputLocation.set(file("${project.buildDir}/reports/spotbugs/spotbugs.xml"))
    }
}

// JaCoCo configuration
jacoco {
    toolVersion = "0.8.11"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // Generate report after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Tests are required to run before generating the report
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal() // 90% coverage target
            }
        }
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal() // 80% minimum per class
            }
            excludes = listOf(
                "org.adarssh.SpotifyWrappedApplication" // Main class - tested via @SpringBootTest
            )
        }
    }
}

// Add coverage verification to check task
tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
