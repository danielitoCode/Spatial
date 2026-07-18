plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.10" apply false
    id("com.vanniktech.maven.publish") version "0.34.0" apply false
}

/**
 * Single source of truth for the version every spatial-* module publishes
 * under. Local/CI builds use the value pinned in gradle.properties; the
 * Publish workflow overrides it per-release via -PVERSION_NAME=<tag>.
 */
val spatialVersion: String = providers.gradleProperty("VERSION_NAME").getOrElse("0.1.0-alpha01")

/**
 * Whether this invocation is an actual release publish (signing + upload to
 * Maven Central) as opposed to a CI/local verification run. Set by the
 * Publish workflow via -PspatialRelease=true. Left false, publishing tasks
 * stop at publishToMavenLocal and never require signing credentials, which
 * is what lets the CI workflow exercise every publication without needing
 * release secrets.
 */
val isReleaseBuild: Boolean = providers.gradleProperty("spatialRelease").getOrElse("false").toBoolean()

allprojects {
    version = spatialVersion
}

/**
 * Publishing conventions shared by every spatial-* module: Maven Central
 * target, common POM metadata (license/developers/scm), and signing that is
 * only required for genuine release publishes. Each module still declares
 * its own coordinates()/artifactId/name/description, since those are
 * module-specific; everything identical across modules lives here instead
 * of being copy-pasted 15 times.
 */
subprojects {

    pluginManager.withPlugin("com.vanniktech.maven.publish") {

        // Deferred to afterEvaluate: configuring the mavenPublishing/signing
        // extensions here (rather than eagerly at plugin-apply time, before
        // the module's own build.gradle.kts and AGP have finished
        // configuring it) avoids racing internal wiring that can finalize
        // these properties first and throw "value for this property is
        // final and cannot be changed any further" when we then try to set
        // them ourselves.
        afterEvaluate {

            extensions.configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {

                publishToMavenCentral()

                if (isReleaseBuild) {
                    signAllPublications()
                }

                pom {

                    inceptionYear.set("2026")

                    url.set("https://github.com/danielitoCode/Spatial")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("danielitocode")
                            name.set("Daniel")
                        }
                    }

                    scm {
                        url.set("https://github.com/danielitoCode/Spatial")
                        connection.set("scm:git:git://github.com/danielitoCode/Spatial.git")
                        developerConnection.set("scm:git:ssh://github.com/danielitoCode/Spatial.git")
                    }
                }
            }

            if (isReleaseBuild) {
                extensions.configure<org.gradle.plugins.signing.SigningExtension> {
                    useGpgCmd()
                }
            }
        }
    }
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
 * Publishes every Spatial module to the configured mavenPublishing target
 * (Maven Central when -PspatialRelease=true, otherwise whatever
 * publishToMavenCentral's underlying tasks resolve to locally). This is the
 * task the "Publish" GitHub workflow invokes on a tagged release; the CI
 * workflow never calls it and instead stops at verifyRepository.
 */
tasks.register("publishAllModules") {

    description = "Publishes every Spatial module to Maven Central."

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

/**
 * True for any subproject that applies the Maven publishing plugin, i.e.
 * every module actually shipped as a library artifact. Derived from the
 * applied plugin rather than a manually maintained flag, so it can't go
 * stale the way a hand-set extra["publishable"] marker would if a module
 * is added or renamed and someone forgets to flip it.
 */
fun Project.isPublishable(): Boolean =
    pluginManager.hasPlugin("com.vanniktech.maven.publish")

tasks.register("verifyArtifacts") {

    group = "verification"

    description = "Verifies generated publication artifacts."

    dependsOn("verifyPublications")

    doLast {

        val publishableModules = subprojects.filter { it.isPublishable() }

        check(publishableModules.isNotEmpty()) {
            "Expected at least one publishable module, found none. " +
                "Check that the maven-publish plugin is still applied where expected."
        }

        publishableModules.forEach { module ->

            val jarOutputs = module.layout.buildDirectory.dir("libs").get().asFile
            val aarOutputs = module.layout.buildDirectory.dir("outputs/aar").get().asFile

            val hasJar = jarOutputs.listFiles()?.any { it.extension == "jar" } == true
            val hasAar = aarOutputs.listFiles()?.any { it.extension == "aar" } == true

            check(hasJar || hasAar) {
                "No published artifact (.jar/.aar) found for module '${module.path}'. " +
                    "Expected one of: ${jarOutputs.path}, ${aarOutputs.path}"
            }
        }

        println("")
        println("Publication artifacts verified for ${publishableModules.size} modules.")
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
