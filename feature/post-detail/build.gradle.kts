plugins {
    alias(libs.plugins.pochak.android.feature)
    alias(libs.plugins.pochak.android.library.compose)
}

android {
    namespace = "com.site.pochak.app.feature.post.detail"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.coil.kt.compose)
}