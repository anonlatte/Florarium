plugins {
    val kotlinVersion = "1.8.22"
    val gradleVersion = "8.0.2"
    val navigationVersion = "2.5.3"

    id("com.android.application") version gradleVersion apply false
    id("com.android.library") version gradleVersion apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("org.jetbrains.kotlin.kapt") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.firebase.crashlytics") version "2.9.4"
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("androidx.navigation.safeargs.kotlin") version navigationVersion apply false
}
