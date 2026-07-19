package io.github.danielitocode.spatial.buildlogic.android.internal

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure


internal class CompileConfiguration(
    private val project: Project
) {


    fun configure() {

        project.extensions
            .configure<LibraryExtension> {


                compileSdk = 36


                defaultConfig {

                    minSdk = 24


                }


                compileOptions {


                    sourceCompatibility =
                        JavaVersion.VERSION_17


                    targetCompatibility =
                        JavaVersion.VERSION_17

                }

            }

    }

}