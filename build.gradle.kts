plugins {
    idea
    id("org.jetbrains.kotlin.jvm") version "1.5.21"
    id("org.jetbrains.intellij") version "1.1.4"
}

group = "su.nlq"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    plugins.add("java")
    version.set("2021.2")
    instrumentCode.set(false)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
}
