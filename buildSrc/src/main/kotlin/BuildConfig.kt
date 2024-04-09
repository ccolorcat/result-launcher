import org.gradle.api.JavaVersion

/**
 * Author: ccolorcat
 * Date: 2023-12-19
 * GitHub: https://github.com/ccolorcat
 */
object BuildConfig {
    const val groupId = "cc.colorcat.runtime"

    const val compileSdk = 33
    const val minSdk = 23
    const val targetSdk = compileSdk

    const val versionCode = 9
    const val versionName = "2.1.2"

    val javaVersion = JavaVersion.VERSION_1_8
    const val jvmTarget = "1.8"
}

object Libs {
    object Remote {
        const val coreKtx = "androidx.core:core-ktx:1.10.1"
        const val appcompat = "androidx.appcompat:appcompat:1.3.1"
        const val material = "com.google.android.material:material:1.4.0"
        const val activityKtx = "androidx.activity:activity-ktx:1.7.2"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.5.5"
        const val kotlinxCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
        const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.6.2"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val androidJunit = "androidx.test.ext:junit:1.1.5"
        const val androidEspressoCore = "androidx.test.espresso:espresso-core:3.5.1"
    }
}
