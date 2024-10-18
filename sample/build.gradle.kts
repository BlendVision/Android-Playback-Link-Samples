plugins {
    id("com.blendvision.playback.link.application.config")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.blendvision.playback.link.sample"
}

dependencies {

    implementation(libs.bv.playbacklink)

    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.coroutines)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}