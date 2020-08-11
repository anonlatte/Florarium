private const val androidGradleVersion = "4.0.1"
private const val ktlintVersion = "9.3.0"
private const val checkUpdatesPluginVersion = "1.0.209"

// Compile dependencies
private const val appCompatVersion = "1.2.0"
private const val archCoreVersion = "2.1.0"
private const val constraintLayoutVersion = "1.1.3"
private const val coreVersion = "1.3.1"
private const val espressoVersion = "3.2.0"
private const val fragmentVersion = "1.2.5"
private const val glideVersion = "4.11.0"
private const val hamcrestVersion = "2.2"
private const val junitExtVersion = "1.1.1"
private const val junitVersion = "4.13"
private const val leakCanaryVersion = "2.4"
private const val lifeCycleVersion = "2.2.0"
private const val materialVersion = "1.2.0"
private const val navigationVersion = "2.3.0"
private const val recyclerviewSelectionVersion = "1.0.0"
private const val roomVersion = "2.2.5"
private const val timberVersion = "4.7.1"
private const val workVersion = "2.4.0"

object Config {
    object BuildPlugins {
        const val checkUpdatesPlugin = "name.remal:gradle-plugins:$checkUpdatesPluginVersion"
        const val androidGradle = "com.android.tools.build:gradle:$androidGradleVersion"
        const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:$ktlintVersion"
    }

    object App {
        const val applicationId = "com.anonlatte.florarium"
        const val buildToolsVersion = "29.0.3"
        const val compileSdkVersion = 29
        const val minSdkVersion = 16
        const val targetSdkVersion = 29
        const val versionCode = 1
        const val versionName = "1.0"
    }

    object Libs {
        const val appcompat = "androidx.appcompat:appcompat:${appCompatVersion}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${constraintLayoutVersion}"
        const val core = "androidx.core:core-ktx:${coreVersion}"
        const val fragment = "androidx.fragment:fragment-ktx:${fragmentVersion}"
        const val glide = "com.github.bumptech.glide:glide:${glideVersion}"
        const val glideCompiler = "com.github.bumptech.glide:compiler:${glideVersion}"
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${leakCanaryVersion}"
        const val lifecycleExtensions =
            "androidx.lifecycle:lifecycle-extensions:${lifeCycleVersion}"
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${lifeCycleVersion}"
        const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:${lifeCycleVersion}"
        const val material = "com.google.android.material:material:${materialVersion}"
        const val navigationDynamicFeatures =
            "androidx.navigation:navigation-dynamic-features-fragment:${navigationVersion}"
        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:${navigationVersion}"
        const val navigationUI = "androidx.navigation:navigation-ui-ktx:${navigationVersion}"
        const val recyclerviewSelection =
            "androidx.recyclerview:recyclerview-selection:${recyclerviewSelectionVersion}"
        const val room = "androidx.room:room-ktx:${roomVersion}"
        const val roomCompiler = "androidx.room:room-compiler:${roomVersion}"
        const val roomRuntime = "androidx.room:room-runtime:${roomVersion}"
        const val timber = "com.jakewharton.timber:timber:${timberVersion}"
        const val vectorDrawable = "androidx.vectordrawable:vectordrawable:1.1.0"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${lifeCycleVersion}"
        const val workRuntime = "androidx.work:work-runtime-ktx:${workVersion}"
    }

    object TestLibs {
        const val archCore = "androidx.arch.core:core-testing:${archCoreVersion}"
        const val espresso = "androidx.test.espresso:espresso-core:${espressoVersion}"
        const val hamcrest = "org.hamcrest:hamcrest-library:${hamcrestVersion}"
        const val junit = "junit:junit:${junitVersion}"
        const val junitExt = "androidx.test.ext:junit:${junitExtVersion}"
        const val navigation = "androidx.navigation:navigation-testing:${navigationVersion}"
        const val room = "androidx.room:room-testing:${roomVersion}"
    }
}