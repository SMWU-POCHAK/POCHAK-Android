plugins {
    alias(libs.plugins.pochak.android.feature)
    alias(libs.plugins.pochak.android.library.compose)
}

android {
    namespace = "com.site.pochak.app.feature.camera"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.androidx.core)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.exifinterface)
    implementation(libs.accompanist.flowlayout)
}