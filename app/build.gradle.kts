plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.naibaf.GymTrim"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.naibaf.GymTrim"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.yukuku.ambilwarna)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.preference)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.material)
    implementation(libs.google.material)
    implementation(libs.core)
    implementation (libs.mpandroidchart)
    implementation(libs.preference.ktx)
    implementation (libs.mpandroidchart.vv310)
    implementation(libs.androidx.work.runtime)
    //noinspection GradlePath
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.material.v190) // Update if necessary

}