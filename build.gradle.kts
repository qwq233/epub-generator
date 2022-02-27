import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20-M1"
    application
    java
}

group = "top.qwq2333"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.tongfei:progressbar:0.9.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("top.qwq23333.MainKt")
}