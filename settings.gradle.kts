pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven( url = "https://jitpack.io" )
    }
    versionCatalogs {
        create("meta") {
            version("app", "1.0")
            version("appCode", "1")
            version("compileSdk", "33")
            version("minSdk", "24")
            version("targetSdk", "33")
        }
        create("libs") {
            val kotlinVersion = version("kotlinStdlibVersion", "1.8.21")
            val androidxCoreVersion = version("androidxCoreVersion", "1.10.0")
            val androidxAppcompatVersion = version("androidxAppcompatVersion", "1.6.1")
            val viewBindingDelegateVersion = version("viewBindingDelegateVersion", "1.5.9")
            val androidxRecyclerviewVersion = version("androidxRecyclerviewVersion", "1.3.0")
            val androidxConstraintLayoutVersion =
                version("androidxConstraintLayoutVersion", "2.1.4")
            val androidMaterialVersion = version("androidMaterialVersion", "1.8.0")
            val androidFragmentVersion = version("androidFragmentVersion", "1.5.7")
            val androidxActivityVersion = version("androidxActivityVersion", "1.7.1")
            val androidxLifecycleVersion = version("androidxLifecycleVersion", "2.6.1")
            val timberVersion = version("timberVersion", "5.0.1")
            val kotlinxCoroutinesVersion = version("kotlinxCoroutinesVersion", "1.7.0-RC")
            val hiltVersion = version("hiltVersion", "2.45")
            val roomVersion = version("roomVersion", "2.5.1")
            val datastoreVersion = version("datastoreVersion", "1.0.0")
            val coilVersion = version("coilVersion", "2.4.0")
            val leakCanaryVersion = version("leakCanaryVersion", "2.10")
            val firebaseCrashlyticsVersion = version("firebaseCrashlyticsVersion", "18.3.7")
            val firebaseAnalyticsVersion = version("firebaseAnalyticsVersion", "21.2.2")
            val junitVersion = version("junitVersion", "4.13.2")
            val mockitoVersion = version("mockitoVersion", "4.0.0")
            val archVersion = version("archVersion", "2.1.0")
            val truthVersion = version("truthVersion", "1.1.3")
            val androidxTestCoreVersion = version("androidxTestCoreVersion", "1.5.0")
            val androidxJunitVersion = version("androidxJunitVersion", "1.1.5")
            val espressoVersion = version("espressoVersion", "3.4.0")
            val firebaseMessagingVersion = version("firebaseMessagingVersion", "23.1.2")
            val detektVersion = version("detektVersion", "1.22.0")
            val navigationVersion = version("navigationVersion", "2.5.3")
            val splashscreenVersion = version("splashscreenVersion", "1.0.1")
            val workManagerVersion = version("workManagerVersion", "2.8.0")
            val hamcrestVersion = version("hamcrestVersion", "2.2")
            val navigationTestingVersion = version("navigationTestingVersion", "2.6.0")

            // AndroidX Core
            library("androidxCore", "androidx.core", "core-ktx").versionRef(androidxCoreVersion)
            library("coreSplashscreen", "androidx.core", "core-splashscreen").versionRef(splashscreenVersion)
            bundle(
                "androidxCore",
                listOf(
                    "androidxCore",
                    "coreSplashscreen"
                )
            )

            // WorkManager
            library("workManager", "androidx.work", "work-runtime-ktx").versionRef(workManagerVersion)


            // Appcompat (AndroidX)
            library("androidxAppcompat", "androidx.appcompat", "appcompat")
                .versionRef(androidxAppcompatVersion)

            library(
                "viewBindingDelegate",
                "com.github.kirich1409",
                "viewbindingpropertydelegate-noreflection"
            )
                .versionRef(viewBindingDelegateVersion)

            // RecyclerView (AndroidX)
            library("androidxRecyclerview", "androidx.recyclerview", "recyclerview")
                .versionRef(androidxRecyclerviewVersion)

            // ConstraintLayout (AndroidX)
            library("androidxConstraintLayout", "androidx.constraintlayout", "constraintlayout")
                .versionRef(androidxConstraintLayoutVersion)

            // Material Components (Material)
            library("androidMaterial", "com.google.android.material", "material")
                .versionRef(androidMaterialVersion)

            // Fragment API
            library("androidFragment", "androidx.fragment", "fragment-ktx")
                .versionRef(androidFragmentVersion)

            // Activity androidx API
            library("androidxActivity", "androidx.activity", "activity-ktx")
                .versionRef(androidxActivityVersion)

            // Lifecycle
            library("androidxLifecycleViewModel", "androidx.lifecycle", "lifecycle-viewmodel-ktx")
                .versionRef(androidxLifecycleVersion)
            library("androidxLifecycleRuntime", "androidx.lifecycle", "lifecycle-runtime-ktx")
                .versionRef(androidxLifecycleVersion)
            bundle(
                "androidxLifecycle",
                listOf(
                    "androidxLifecycleViewModel",
                    "androidxLifecycleRuntime"
                )
            )

            // Timber for logging
            library("timber", "com.jakewharton.timber", "timber").versionRef(timberVersion)

            // Coroutines
            library("kotlinxCoroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-android")
                .versionRef(kotlinxCoroutinesVersion)

            // Hilt Di
            library("hiltAndroid", "com.google.dagger", "hilt-android").versionRef(hiltVersion)
            library("hiltCompiler", "com.google.dagger", "hilt-compiler").versionRef(hiltVersion)
            library("hiltAndroidCompiler", "com.google.dagger", "hilt-android-compiler").versionRef(
                "hiltVersion"
            )
            bundle("hilt", listOf("hiltAndroid", "hiltCompiler"))

            // Database (Room)
            library("androidxRoom", "androidx.room", "room-runtime").versionRef(roomVersion)
            library("androidxRoomCommon", "androidx.room", "room-common").versionRef(roomVersion)
            library("androidxRoomCompiler", "androidx.room", "room-compiler")
                .versionRef(roomVersion)
            library("androidxRoomKtx", "androidx.room", "room-ktx").versionRef(roomVersion)
            bundle("room", listOf("androidxRoom", "androidxRoomKtx"))

            // Jetpack DataStore
            library("datastorePrefs", "androidx.datastore", "datastore-preferences")
                .versionRef(datastoreVersion)
            library("datastoreCore", "androidx.datastore", "datastore-core")
                .versionRef(datastoreVersion)
            bundle("datastore", listOf("datastorePrefs", "datastoreCore"))

            // Media management and image (Coil)
            library("coilMain", "io.coil-kt", "coil").versionRef(coilVersion)
            bundle("coil", listOf("coilMain"))

            // Leak Canary
            library("leakCanaryDebug", "com.squareup.leakcanary", "leakcanary-android")
                .versionRef(leakCanaryVersion)
            bundle("leakCanary", listOf("leakCanaryDebug"))

            // Firebase
            library("crashlytics", "com.google.firebase", "firebase-crashlytics-ktx")
                .versionRef(firebaseCrashlyticsVersion)
            library("analytics", "com.google.firebase", "firebase-analytics-ktx")
                .versionRef(firebaseAnalyticsVersion)
            bundle("firebase", listOf("crashlytics", "analytics"))

            // Test libs
            library("junit", "junit", "junit").versionRef(junitVersion)
            library("mockito", "org.mockito.kotlin", "mockito-kotlin").versionRef(mockitoVersion)
            library("roomTesting", "androidx.room", "room-testing").versionRef(roomVersion)
            library("hamcrest", "org.hamcrest", "hamcrest-library").versionRef(hamcrestVersion)
            bundle("testLibs", listOf("junit", "mockito", "roomTesting", "hamcrest"))

            // Android test
            library("androidxJunit", "androidx.test.ext", "junit")
                .versionRef(androidxJunitVersion)
            library("androidxTestCore", "androidx.test", "core")
                .versionRef(androidxTestCoreVersion)
            library("androidxJunitKtx", "androidx.test.ext", "junit-ktx")
                .versionRef(androidxJunitVersion)
            library("espressoCore", "androidx.test.espresso", "espresso-core")
                .versionRef(espressoVersion)
            library("navigationTesting", "androidx.navigation", "navigation-testing")
                .versionRef(navigationTestingVersion)
            bundle(
                "androidTestLibs",
                listOf(
                    "androidxJunit",
                    "androidxJunitKtx",
                    "espressoCore",
                    "androidxTestCore",
                    "navigationTesting"
                )
            )

            //Firebase msg
            library("messagingLib", "com.google.firebase", "firebase-messaging-ktx")
                .versionRef(firebaseMessagingVersion)
            bundle("messaging", listOf("messagingLib"))

            //Detekt
            library(
                "detekt.cli",
                "io.gitlab.arturbosch.detekt",
                "detekt-cli"
            ).versionRef(detektVersion)
            library(
                "detekt.formatting",
                "io.gitlab.arturbosch.detekt",
                "detekt-formatting"
            ).versionRef(detektVersion)

            //Navigation
            library("navigationDynamicFeatures",
                "androidx.navigation",
                "navigation-dynamic-features-fragment"
            )
                .versionRef(navigationVersion)
            library("navigationFragmentKtx", "androidx.navigation", "navigation-fragment-ktx")
                    .versionRef(navigationVersion)
            library("navigationUiKtx", "androidx.navigation", "navigation-ui-ktx")
                    .versionRef(navigationVersion)
            bundle(
                "navigation",
                listOf("navigationDynamicFeatures", "navigationFragmentKtx", "navigationUiKtx",)
            )

        }
    }
}

include(":app")
rootProject.name = "Florarium"