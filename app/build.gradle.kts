plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cc.colorcat.runtime.sample"
    compileSdk = BuildConfig.compileSdk

    defaultConfig {
        applicationId = "cc.colorcat.runtime.sample"
        minSdk = BuildConfig.minSdk
        targetSdk = BuildConfig.targetSdk
        versionCode = BuildConfig.versionCode
        versionName = BuildConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = BuildConfig.javaVersion
        targetCompatibility = BuildConfig.javaVersion
    }

    kotlinOptions {
        jvmTarget = BuildConfig.jvmTarget
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":library"))
    implementation(Libs.Remote.coreKtx)
    implementation(Libs.Remote.appcompat)
    implementation(Libs.Remote.material)
    implementation(Libs.Remote.kotlinxCoroutinesAndroid)
    implementation(Libs.Remote.lifecycleRuntimeKtx)

    testImplementation(Libs.Test.junit)
    androidTestImplementation(Libs.Test.androidJunit)
    androidTestImplementation(Libs.Test.androidEspressoCore)

//    implementation("com.github.ccolorcat:result-launcher:1.1.0")
}