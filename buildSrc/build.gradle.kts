plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("common_module") {
            id = "common_module"
            implementationClass = "plugins.CommonModule"
        }
        create("di_module") {
            id = "di_module"
            implementationClass = "plugins.DiModule"
        }
        create("serialization_module") {
            id = "serialization_module"
            implementationClass = "plugins.SerializationModule"
        }
    }
}