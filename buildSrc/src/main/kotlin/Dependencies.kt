object BuildPlugins {
    object App {
        const val buildToolsVersion = "29.0.3"
        const val compileSdkVersion = 29
        const val minSdkVersion = 16
        const val targetSdkVersion = 29
        const val versionCode = 1
        const val versionName = "1.0"
    }

    object Versions {
        const val appCompatVersion = "1.1.0"
        const val archCoreVersion = "2.1.0"
        const val constraintLayoutVersion = "1.1.3"
        const val coreVersion = "1.3.1"
        const val espressoVersion = "3.2.0"
        const val fragmentVersion = "1.2.5"
        const val hamcrestVersion = "2.2"
        const val junitExtVersion = "1.1.1"
        const val junitVersion = "4.13"
        const val leakCanaryVersion = "2.4"
        const val lifeCycleVersion = "2.2.0"
        const val materialVersion = "1.2.0-rc01"
        const val navigationVersion = "2.3.0"
        const val roomVersion = "2.2.5"
        const val timberVersion = "4.7.1"
        const val workVersion = "2.4.0"
    }

    object Libs {
        const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
        const val timber = "com.jakewharton.timber:timber:${Versions.timberVersion}"
        const val material = "com.google.android.material:material:${Versions.materialVersion}"
        const val workRuntime = "androidx.work:work-runtime-ktx:${Versions.workVersion}"
        const val vectorDrawable = "androidx.vectordrawable:vectordrawable:1.1.0"
        const val roomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
        const val room = "androidx.room:room-ktx:${Versions.roomVersion}"
        const val navigationUI =
            "androidx.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
        const val navigationDynamicFeatures =
            "androidx.navigation:navigation-dynamic-features-fragment:${Versions.navigationVersion}"
        const val viewModel =
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifeCycleVersion}"
        const val lifecycleRuntime =
            "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycleVersion}"
        const val liveData =
            "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifeCycleVersion}"
        const val lifecycleExtensions =
            "androidx.lifecycle:lifecycle-extensions:${Versions.lifeCycleVersion}"
        const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragmentVersion}"
        const val core = "androidx.core:core-ktx:${Versions.coreVersion}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVersion}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
        const val leakCanary =
            "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanaryVersion}"
    }

    object TestLibs {
        const val archCore = "androidx.arch.core:core-testing:${Versions.archCoreVersion}"
        const val navigation =
            "androidx.navigation:navigation-testing:${Versions.navigationVersion}"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.espressoVersion}"
        const val junitExt = "androidx.test.ext:junit:${Versions.junitExtVersion}"
        const val room = "androidx.room:room-testing:${Versions.roomVersion}"
        const val junit = "junit:junit:${Versions.junitVersion}"
        const val hamcrest = "org.hamcrest:hamcrest-library:${Versions.hamcrestVersion}"
    }
}