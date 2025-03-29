import com.android.build.api.variant.FilterConfiguration.FilterType.ABI

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.detekt)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
    alias(libs.plugins.compose.compiler)
}

android {

    namespace = "com.anonlatte.florarium"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.anonlatte.florarium"
        minSdkPreview = libs.versions.minSdk.get()
        targetSdkPreview = libs.versions.targetSdk.get()
        versionCode = libs.versions.appCode.get().toInt()
        versionName = libs.versions.app.get()

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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    kapt(libs.androidx.room.compiler)
    kapt(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.constraintlayout)
    implementation(libs.bundles.androidx.core)
    implementation(libs.android.fragment)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.androidx.exifinterface)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.activity)
    implementation(libs.coil.compose)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.room)

    implementation(libs.bundles.datastore)

    implementation(libs.work.manager)

    implementation(libs.android.material)
    implementation(libs.bundles.hilt)

    implementation(libs.timber)

    implementation(libs.bundles.coil)
    implementation(libs.insetter)

    implementation(libs.kotlinx.serialization)

    debugImplementation(libs.android.fragment.testing)
    debugImplementation(libs.bundles.leak.canary)
    debugImplementation(libs.leak.canary)

    androidTestImplementation(libs.bundles.android.test.libs)
    testImplementation(libs.bundles.test.libs)
}
