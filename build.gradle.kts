plugins {
    id("org.springframework.boot") version "2.7.16-SNAPSHOT"
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("com.bmuschko.docker-spring-boot-application") version "9.3.2"
    id("io.gatling.gradle") version "3.9.5.5"
}

docker {
    springBootApplication {
        baseImage.set("openjdk:17-jdk-slim")
    }
}

group = "ru.tim.conference"
version = "1.0.0"

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.16-SNAPSHOT"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    implementation(platform("org.testcontainers:testcontainers-bom:1.18.3"))
    implementation(group = "org.testcontainers", name = "jdbc")
    implementation(group = "org.testcontainers", name = "postgresql")
    implementation(group = "io.github.microutils", name = "kotlin-logging", version = "+")

    testImplementation(platform("org.testcontainers:testcontainers-bom:1.18.3"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    gatlingImplementation(platform("org.testcontainers:testcontainers-bom:1.18.3"))
    gatlingImplementation("org.testcontainers:junit-jupiter")
    gatlingImplementation(group = "io.github.microutils", name = "kotlin-logging", version = "+")
    gatlingImplementation(group = "org.testcontainers", name = "postgresql")
    gatlingImplementation("com.google.code.gson:gson:2.10.1")
}

tasks {
    check {
        dependsOn(gatlingRun)
    }

    gatlingRun{
        dependsOn(dockerBuildImage)
    }
}
