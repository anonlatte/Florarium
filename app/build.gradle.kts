import com.android.build.api.variant.FilterConfiguration.FilterType.ABI

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    kotlin("plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
}

android {

    namespace = "com.anonlatte.florarium"

    compileSdk = meta.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.anonlatte.florarium"
        minSdkPreview = meta.versions.minSdk.get()
        targetSdkPreview = meta.versions.targetSdk.get()
        versionCode = meta.versions.appCode.get().toInt()
        versionName = meta.versions.app.get()

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.incremental"] = "true"
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }

    applicationVariants.all {
        outputs.all {
            val abiName = this.filters.find { it.filterType == ABI.name }?.identifier
            val builtType = buildType.name
            val versionName = versionName
            val variantOutputImpl =
                this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            variantOutputImpl.outputFileName = buildString {
                append("FlorariumV")
                append(versionName)
                if (abiName != null) {
                    append("[$abiName]")
                }
                append("[${builtType}].apk")
            }
        }
    }


    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    kapt(libs.androidxRoomCompiler)
    kapt(libs.hiltAndroid)
    kapt(libs.hiltCompiler)
    kapt(libs.hiltAndroidCompiler)

    implementation(libs.androidxAppcompat)

    implementation(libs.androidxConstraintLayout)
    implementation(libs.bundles.androidxCore)
    implementation(libs.androidFragment)
    implementation(libs.bundles.androidxLifecycle)
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.room)

    implementation(libs.bundles.datastore)

    implementation(libs.workManager)

    implementation(libs.androidMaterial)
    implementation(libs.bundles.hilt)

    implementation(libs.timber)

    implementation(libs.bundles.coil)
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    debugImplementation("androidx.fragment:fragment-testing:1.5.7")
    debugImplementation(libs.bundles.leakCanary)
    debugImplementation(libs.leakCanaryDebug)

    androidTestImplementation(libs.bundles.androidTestLibs)
    testImplementation(libs.bundles.testLibs)
}
