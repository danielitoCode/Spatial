package io.github.danielitocode.spatial.buildlogic.publish

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.signing.SigningExtension

class SpatialPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply required plugins
            pluginManager.apply("com.vanniktech.maven.publish")
            pluginManager.apply("signing")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            val isReleaseBuild = providers.gradleProperty("spatialRelease").getOrElse("false").toBoolean()

            extensions.configure<MavenPublishBaseExtension> {
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
                extensions.configure<SigningExtension> {
                    useGpgCmd()
                }
            }
        }
    }
}
