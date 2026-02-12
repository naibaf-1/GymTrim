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
        versionCode = 4
        // How it works:
        // v.MAJOR.MINOR.PATCH[-optionalSuffix]
        // Major: Incompatible with previous version
        // Minor: Compatible with previous version
        // => Both for new features
        // Patch: Bug-fixes, improvements, etc.
        versionName = "v.2.0.2-rose-breasted_flycatcher"

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
//    implementation(libs.core) => Not FOSS => Error with F-droid => If causing problems replace with FOSS alternative
    implementation (libs.mpandroidchart)
    implementation(libs.preference.ktx)
    implementation (libs.mpandroidchart.vv310)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.constraintlayout)
    //noinspection GradlePath
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.material.v190) // Update if necessary
    implementation (libs.picasso)

}