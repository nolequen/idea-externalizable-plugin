plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.7.0"
}

group = "su.nlq"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    plugins.add("java")
    version.set("2022.2")
    instrumentCode.set(false)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
}
