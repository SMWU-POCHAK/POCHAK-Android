plugins {
    alias(libs.plugins.pochak.android.library)
    alias(libs.plugins.pochak.hilt)
}

android {
    namespace = "com.site.pochak.app.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.gms.auth)
}