package com.elitec.spatial_camera.animation

class FixedStepCameraAnimationScheduler(
    private val frameStepMs: Long = DEFAULT_FRAME_STEP_MS,
) : CameraAnimationScheduler {
    override suspend fun schedule(durationMs: Long, onFrame: (elapsedMs: Long) -> Unit) {
        val safeDuration = durationMs.coerceAtLeast(0L)
        val safeFrameStep = frameStepMs.takeIf { it > 0L } ?: DEFAULT_FRAME_STEP_MS

        if (safeDuration == 0L) {
            onFrame(0L)
            return
        }

        var elapsedMs = 0L
        while (elapsedMs < safeDuration) {
            onFrame(elapsedMs)
            elapsedMs += safeFrameStep
        }
        onFrame(safeDuration)
    }

    private companion object {
        const val DEFAULT_FRAME_STEP_MS = 16L
    }
}