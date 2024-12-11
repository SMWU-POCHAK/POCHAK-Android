plugins {
    alias(libs.plugins.pochak.android.library)
    alias(libs.plugins.pochak.hilt)
}

android {
    namespace = "com.site.pochak.app.core.data"
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                // Room schema 파일 경로 설정
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.network)
    api(projects.core.model)
    implementation(libs.okhttp.logging)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.hilt.android)
    ksp(libs.room.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
