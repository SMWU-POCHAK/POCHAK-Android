plugins {
    alias(libs.plugins.pochak.android.library)
    alias(libs.plugins.pochak.android.room)
    alias(libs.plugins.pochak.hilt)
}

android {
    namespace = "com.site.pochak.app.core.database"
}

dependencies {
    api(projects.core.model)
}
