plugins {
    alias(libs.plugins.pochak.jvm.library)
    alias(libs.plugins.pochak.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}