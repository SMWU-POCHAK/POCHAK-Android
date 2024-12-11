plugins {
    alias(libs.plugins.pochak.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.site.pochak.app.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
}