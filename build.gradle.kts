plugins {
	java
	id("org.springframework.boot") version "3.4.6"
	id("io.spring.dependency-management") version "1.1.5"
}

group = "com.handsofretail"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation("org.springframework.boot:spring-boot-starter-security")

	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

	implementation("io.jsonwebtoken:jjwt-api:0.11.5")

	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")

	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	implementation("com.h2database:h2")

	runtimeOnly("com.mysql:mysql-connector-j")

	compileOnly("org.projectlombok:lombok")

	annotationProcessor("org.projectlombok:lombok")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testImplementation("org.springframework.security:spring-security-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.apache.poi:poi:5.3.0")
    implementation("org.apache.poi:poi-ooxml:5.3.0")
	implementation("org.flywaydb:flyway-core:9.20.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}