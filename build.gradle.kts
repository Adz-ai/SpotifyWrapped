plugins {
    id("java")
}

group = "org.adarssh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly("org.json:json:20220924")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
