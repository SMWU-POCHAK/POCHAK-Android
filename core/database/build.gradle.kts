plugins {
    alias(libs.plugins.pochak.android.library)
    alias(libs.plugins.pochak.hilt)
}

android {
    namespace = "com.site.pochak.app.core.database"
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
    api(projects.core.model)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
