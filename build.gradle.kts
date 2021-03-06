/*
 * EPUB Generator
 * Copyright (C) 2022 qwq233 qwq233@qwq2333.top
 * https://github.com/qwq233/epub-generator
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of our Licenses
 * as published by James Clef; either
 * version 2 of the License, or any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the our
 * licenses for more details.
 *
 * You should have received a copy of the our License
 * and eula along with this software.  If not, see
 * <https://github.com/qwq233/License/blob/master/v2/LICENSE.md>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    kotlin("plugin.serialization") version "1.6.20-M1"
    application
    java
}

group = "top.qwq2333"
version = "1.0.9"

repositories {
    mavenCentral()
}

val kotlinVersion = "1.6.20"

dependencies {
    implementation("com.charleskorn.kaml:kaml:_")
    implementation("org.dom4j:dom4j:_")
    implementation("com.vladsch.flexmark:flexmark-all:_")
    implementation(Kotlin.stdlib.common)
    implementation(Kotlin.stdlib)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("top.qwq2333.MainKt")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = "top.qwq2333.MainKt"
    }
    // To add all the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

}
