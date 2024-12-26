plugins {
    alias(libs.plugins.pochak.android.feature)
    alias(libs.plugins.pochak.android.library.compose)
}

android {
    namespace = "com.site.pochak.app.feature.post"
}

dependencies {
    implementation(projects.core.data)
    implementation(project(":core:domain"))
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.coil.kt.compose)
}