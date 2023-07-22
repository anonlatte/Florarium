package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class SerializationModule: Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
        target.plugins.apply("kotlin-parcelize")
    }
}