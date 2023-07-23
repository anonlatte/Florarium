package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class DiModule : Plugin<Project> {

    override fun apply(target: Project) {

        target.plugins.apply("com.google.dagger.hilt.android")
        target.plugins.apply("org.jetbrains.kotlin.kapt")

        target.dependencies.apply {
            add("kapt", "com.google.dagger:hilt-android:2.45")
            add("kapt", "androidx.hilt:hilt-compiler:1.0.0")
        }
    }
}
