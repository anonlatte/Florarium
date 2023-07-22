package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonModule : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")
        target.plugins.apply("org.jetbrains.kotlin.android")
    }
}

