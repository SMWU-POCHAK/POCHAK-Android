plugins {
    alias(libs.plugins.pochak.android.feature)
    alias(libs.plugins.pochak.android.library.compose)
}

android {
    namespace = "com.site.pochak.app.feature.home"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.androidx.runtime.livedata)
}