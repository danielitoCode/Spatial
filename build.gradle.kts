plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.10" apply false
    id("com.vanniktech.maven.publish") version "0.34.0" apply false
}

/**
 * Registers a root task that depends on the same task
 * from every subproject that exposes it.
 */
fun registerAggregateTask(
    name: String,
    group: String,
    description: String,
    subTask: String
) = tasks.register(name) {

    this.group = group
    this.description = description

    subprojects.forEach { project ->

        dependsOn(
            project.tasks.matching {
                it.name == subTask
            }
        )

    }
}


fun registerVerificationTask(
    name: String,
    description: String,
    gradleTask: String
) = registerAggregateTask(
    name = name,
    group = "verification",
    description = description,
    subTask = gradleTask
)



/**
 * Temporary publishing task.
 * Will disappear when Publish workflow is implemented.
 */
tasks.register("publishAllModules") {

    description = "Publishes every Spatial module."

    dependsOn(
        ":spatial-math:publishToMavenCentral",
        ":spatial-units:publishToMavenCentral",
        ":spatial-core:publishToMavenCentral",
        ":spatial-geometry:publishToMavenCentral",
        ":spatial-scene:publishToMavenCentral",
        ":spatial-motion:publishToMavenCentral",
        ":spatial-material:publishToMavenCentral",
        ":spatial-light:publishToMavenCentral",
        ":spatial-camera:publishToMavenCentral",
        ":spatial-gesture:publishToMavenCentral",
        ":spatial-renderer:publishToMavenCentral",
        ":spatial-runtime:publishToMavenCentral",
        ":spatial-compose:publishToMavenCentral",
        ":spatial-compose-runtime-adapter:publishToMavenCentral",
        ":spatial:publishToMavenCentral"
    )
}


/**
 * CI verification tasks
 */

tasks.register("verifyInfrastructure") {

    group = "verification"

    description = "Verifies repository infrastructure."

    doLast {

        println("")
        println("Gradle infrastructure verified.")
        println("")

    }
}


registerVerificationTask(
    name = "verifyBuild",
    description = "Builds every module.",
    gradleTask = "build"
)


registerVerificationTask(
    name = "verifyTests",
    description = "Runs every verification task.",
    gradleTask = "check"
)


registerVerificationTask(
    name = "verifyPublications",
    description = "Publishes every module to Maven Local.",
    gradleTask = "publishToMavenLocal"
)

fun Project.isPublishable(): Boolean {

    return extensions.extraProperties.has("publishable") &&
            extensions.extraProperties["publishable"] == true

}

tasks.register("verifyArtifacts") {

    group = "verification"

    description = "Verifies generated publication artifacts."

    doLast {

        println("")
        println("Publication artifacts generated successfully.")
        println("")

    }

}


tasks.register("verifyRepository") {

    group = "verification"

    description = "Runs every repository verification."

    dependsOn(
        "verifyInfrastructure",
        "verifyBuild",
        "verifyTests",
        "verifyPublications",
        "verifyArtifacts"
    )

}
