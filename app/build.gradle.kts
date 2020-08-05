plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("org.jlleitschuh.gradle.ktlint")
    id("name.remal.check-dependency-updates")
}

android {
    compileSdkVersion(Config.App.compileSdkVersion)
    buildToolsVersion(Config.App.buildToolsVersion)

    defaultConfig {
        applicationId = Config.App.applicationId
        minSdkVersion(Config.App.minSdkVersion)
        targetSdkVersion(Config.App.targetSdkVersion)
        versionCode = Config.App.versionCode
        versionName = Config.App.versionName

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
    kapt(Config.Libs.roomCompiler)
    implementation(embeddedKotlin("stdlib-jdk7"))
    implementation(Config.Libs.timber)
    implementation(Config.Libs.material)
    implementation(Config.Libs.workRuntime)
    implementation(Config.Libs.vectorDrawable)
    implementation(Config.Libs.roomRuntime)
    implementation(Config.Libs.room)
    implementation(Config.Libs.navigationUI)
    implementation(Config.Libs.navigationFragment)
    implementation(Config.Libs.navigationDynamicFeatures)
    implementation(Config.Libs.viewModel)
    implementation(Config.Libs.lifecycleRuntime)
    implementation(Config.Libs.liveData)
    implementation(Config.Libs.lifecycleExtensions)
    implementation(Config.Libs.fragment)
    implementation(Config.Libs.core)
    implementation(Config.Libs.constraintLayout)
    implementation(Config.Libs.appcompat)
    debugImplementation(Config.Libs.leakCanary)

    androidTestImplementation(Config.TestLibs.archCore)
    androidTestImplementation(Config.TestLibs.navigation)
    androidTestImplementation(Config.TestLibs.espresso)
    androidTestImplementation(Config.TestLibs.junitExt)
    testImplementation(Config.TestLibs.room)
    testImplementation(Config.TestLibs.junit)
    testImplementation(Config.TestLibs.hamcrest)
}

ktlint {
    android.set(true)
    outputColorName.set("RED")
}
