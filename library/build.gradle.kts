plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = BuildConfig.groupId
    compileSdk = BuildConfig.compileSdk

    defaultConfig {
        minSdk = BuildConfig.minSdk

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(Libs.Remote.coreKtx)
    implementation(Libs.Remote.appcompat)
    implementation(Libs.Remote.kotlinxCoroutinesAndroid)
    implementation(Libs.Remote.lifecycleRuntimeKtx)

    testImplementation(Libs.Test.junit)
    androidTestImplementation(Libs.Test.androidJunit)
    androidTestImplementation(Libs.Test.androidEspressoCore)
}

afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            register<MavenPublication>("release") {
                groupId = BuildConfig.groupId
                artifactId = "result-launcher"
                version = BuildConfig.versionName

                project.afterEvaluate {
                    from(components["release"])
                }
            }
        }
    }
}