plugins {
    alias(libs.plugins.pochak.android.feature)
    alias(libs.plugins.pochak.android.library.compose)
}

android {
    namespace = "com.site.pochak.app.feature.login"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.gms.auth)
}