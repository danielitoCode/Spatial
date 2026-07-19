import org.gradle.api.Project
import org.gradle.api.logging.Logger

internal val Project.logger: Logger
    get() = this.logger

internal fun Project.log(message: String) {
    logger.lifecycle("[Spatial] $message")
}

internal fun Project.warn(message: String) {
    logger.warn("[Spatial] $message")
}

internal fun Project.error(message: String) {
    logger.error("[Spatial] $message")
}