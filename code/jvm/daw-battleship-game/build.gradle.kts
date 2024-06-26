import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "pt.isel.daw"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.springframework.security:spring-security-core:5.7.3")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// for JDBI
	implementation("org.jdbi:jdbi3-core:3.33.0")
	implementation("org.jdbi:jdbi3-kotlin:3.33.0")
	implementation("org.jdbi:jdbi3-postgres:3.33.0")
	implementation("org.postgresql:postgresql:42.5.0")


	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<Jar>("jar") {//create a fat jar with all dependencies included in it (for deployment)
	dependsOn("copyRuntimeDependencies")
	manifest {
		attributes["Main-Class"] = "pt.isel.daw.dawbattleshipgame.BattleshipApplicationKt"
		attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
	}
}

tasks.register<Copy>("copyRuntimeDependencies") {//copy all dependencies to a folder
	into("build/libs")
	from(configurations.runtimeClasspath)
}


