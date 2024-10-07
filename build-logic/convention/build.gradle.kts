plugins {
    `kotlin-dsl`
}

group = "com.site.pochak.app.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "pochak.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "pochak.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "pochak.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "pochak.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "pochak.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidTest") {
            id = "pochak.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("hilt") {
            id = "pochak.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("androidRoom") {
            id = "pochak.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
    }
}
