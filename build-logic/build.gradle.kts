plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationConfigPlugin") {
            id = "com.blendvision.playback.link.application.config"
            implementationClass = "AndroidApplicationConfigPlugin"
        }
    }
}
