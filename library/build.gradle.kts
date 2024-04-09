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

        aarMetadata {
            minCompileSdk = BuildConfig.minSdk
        }

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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(Libs.Remote.activityKtx)
    implementation(Libs.Remote.fragmentKtx)

    testImplementation(Libs.Test.junit)
    androidTestImplementation(Libs.Test.androidJunit)
    androidTestImplementation(Libs.Test.androidEspressoCore)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = BuildConfig.groupId
            artifactId = "result-launcher"
            version = BuildConfig.versionName

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "LocalMaven"
            url = project.uri("${project.rootProject.file("maven")}")
        }
    }
}
