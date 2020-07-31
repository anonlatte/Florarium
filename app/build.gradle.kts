plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    compileSdkVersion(BuildPlugins.App.compileSdkVersion)
    buildToolsVersion(BuildPlugins.App.buildToolsVersion)

    defaultConfig {
        applicationId = "com.anonlatte.florarium"
        minSdkVersion(BuildPlugins.App.minSdkVersion)
        targetSdkVersion(BuildPlugins.App.targetSdkVersion)
        versionCode = BuildPlugins.App.versionCode
        versionName = BuildPlugins.App.versionName

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf("room.incremental" to "true")
            }
        }

        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))
    kapt(BuildPlugins.Libs.roomCompiler)
    implementation(embeddedKotlin("stdlib-jdk7"))
    implementation(BuildPlugins.Libs.timber)
    implementation(BuildPlugins.Libs.material)
    implementation(BuildPlugins.Libs.workRuntime)
    implementation(BuildPlugins.Libs.vectorDrawable)
    implementation(BuildPlugins.Libs.roomRuntime)
    implementation(BuildPlugins.Libs.room)
    implementation(BuildPlugins.Libs.navigationUI)
    implementation(BuildPlugins.Libs.navigationFragment)
    implementation(BuildPlugins.Libs.navigationDynamicFeatures)
    implementation(BuildPlugins.Libs.viewModel)
    implementation(BuildPlugins.Libs.lifecycleRuntime)
    implementation(BuildPlugins.Libs.liveData)
    implementation(BuildPlugins.Libs.lifecycleExtensions)
    implementation(BuildPlugins.Libs.fragment)
    implementation(BuildPlugins.Libs.core)
    implementation(BuildPlugins.Libs.constraintLayout)
    implementation(BuildPlugins.Libs.appcompat)
    debugImplementation(BuildPlugins.Libs.leakCanary)

    androidTestImplementation(BuildPlugins.TestLibs.archCore)
    androidTestImplementation(BuildPlugins.TestLibs.navigation)
    androidTestImplementation(BuildPlugins.TestLibs.espresso)
    androidTestImplementation(BuildPlugins.TestLibs.junitExt)
    testImplementation(BuildPlugins.TestLibs.room)
    testImplementation(BuildPlugins.TestLibs.junit)
    testImplementation(BuildPlugins.TestLibs.hamcrest)
}

ktlint {
    android.set(true)
    outputColorName.set("RED")
}
