pluginManagement {
    repositories {
        google() // Room 라이브러리가 위치한 저장소
        mavenCentral() // 일반적인 Java/Kotlin 라이브러리
        gradlePluginPortal() // Gradle 플러그인
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
