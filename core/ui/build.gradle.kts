plugins {
    alias(libs.plugins.pochak.android.library)
    alias(libs.plugins.pochak.android.library.compose)
}

android {
    namespace = "com.dyddyd.aquariumwidget.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
}
