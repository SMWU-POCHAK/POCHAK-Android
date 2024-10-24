plugins {
    alias(libs.plugins.pochak.android.application)
    alias(libs.plugins.pochak.android.application.compose)
    alias(libs.plugins.pochak.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.site.pochak.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.site.pochak.app"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)

    implementation(projects.feature.login)
    implementation(projects.feature.home)
    implementation(projects.feature.post)
    implementation(projects.feature.camera)
    implementation(projects.feature.alarm)
    implementation(projects.feature.profile)
    implementation(projects.feature.profileSetting)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)

    ksp(libs.hilt.compiler)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.kotlin.test)

    androidTestImplementation(kotlin("test"))
}