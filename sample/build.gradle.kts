plugins {
    id("com.blendvision.playback.link.application.config")
}

android {
    namespace = "com.blendvision.playback.link.sample"
}

dependencies {

    implementation(libs.bvplaybacklink)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.coroutines)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}