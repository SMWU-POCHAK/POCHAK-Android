plugins {
    alias(libs.plugins.pochak.android.library)
    alias(libs.plugins.pochak.hilt)
}

android {
    namespace = "com.site.pochak.app.core.network"
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)

    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)

    testImplementation(libs.kotlinx.coroutines.test)
}
