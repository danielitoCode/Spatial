package com.elitec.spatial_camera

enum class CameraUpdateSource {
    Gesture,
    Remote,
    Animation,
}

data class CameraSnapshot(
    val yaw: Float = 0f,
    val pitch: Float = 0f,
    val zoom: Float = 1f,
    val version: Long = 0L,
    val source: CameraUpdateSource = CameraUpdateSource.Gesture,
)

data class CameraDelta(
    val deltaYaw: Float = 0f,
    val deltaPitch: Float = 0f,
    val zoomScaleDelta: Float = 1f,
)

/**
 * Runtime contract with atomic camera operations only.
 *
 * Thread policy:
 * - Writes should be serialized on Main/UI thread OR through a single runtime event loop.
 * - Reads are served from immutable snapshots created at synchronization points after each write.
 *
 * Source precedence on conflicts (same frame/version):
 * Gesture > Remote > Animation.
 */
interface CameraRuntimeContract {
    fun orbitTo(yaw: Float, pitch: Float, source: CameraUpdateSource = CameraUpdateSource.Gesture)
    fun applyDelta(delta: CameraDelta, source: CameraUpdateSource = CameraUpdateSource.Gesture)
    fun zoomTo(zoom: Float, source: CameraUpdateSource = CameraUpdateSource.Gesture)
    fun animateTo(yaw: Float, pitch: Float, zoom: Float, durationMs: Long = 300L)
    fun snapshot(): CameraSnapshot
}

class SpatialCamera(
    initialState: CameraSnapshot = CameraSnapshot(),
) : CameraRuntimeContract {
    private var state: CameraSnapshot = normalize(initialState)

    override fun orbitTo(yaw: Float, pitch: Float, source: CameraUpdateSource) {
        writeAtomic(source) {
            copy(
                yaw = yaw,
                pitch = pitch.coerceIn(-89f, 89f),
            )
        }
    }

    override fun applyDelta(delta: CameraDelta, source: CameraUpdateSource) {
        writeAtomic(source) {
            copy(
                yaw = yaw + delta.deltaYaw,
                pitch = (pitch + delta.deltaPitch).coerceIn(-89f, 89f),
                zoom = (zoom * sanitizeScale(delta.zoomScaleDelta)).coerceIn(0.3f, 4f),
            )
        }
    }

    override fun zoomTo(zoom: Float, source: CameraUpdateSource) {
        writeAtomic(source) {
            copy(zoom = zoom.coerceIn(0.3f, 4f))
        }
    }

    override fun animateTo(yaw: Float, pitch: Float, zoom: Float, durationMs: Long) {
        // Implementación básica de animación.
        orbitTo(yaw, pitch, source = CameraUpdateSource.Animation)
        zoomTo(zoom, source = CameraUpdateSource.Animation)
    }

    override fun snapshot(): CameraSnapshot = state

    private fun writeAtomic(
        source: CameraUpdateSource,
        transform: CameraSnapshot.() -> CameraSnapshot,
    ) {
        val current = state
        val candidate = normalize(current.transform())
        val selected = resolveConflict(current = current, incoming = candidate, source = source)
        state = selected.copy(version = current.version + 1, source = selected.source)
    }

    private fun resolveConflict(
        current: CameraSnapshot,
        incoming: CameraSnapshot,
        source: CameraUpdateSource,
    ): CameraSnapshot {
        val currentPrecedence = precedence(current.source)
        val incomingPrecedence = precedence(source)
        return if (incomingPrecedence >= currentPrecedence) {
            incoming.copy(source = source)
        } else {
            current
        }
    }

    private fun precedence(source: CameraUpdateSource): Int = when (source) {
        CameraUpdateSource.Gesture -> 3
        CameraUpdateSource.Remote -> 2
        CameraUpdateSource.Animation -> 1
    }

    private fun sanitizeScale(scale: Float): Float = if (scale <= 0f) 1f else scale

    private fun normalize(snapshot: CameraSnapshot): CameraSnapshot = snapshot.copy(
        pitch = snapshot.pitch.coerceIn(-89f, 89f),
        zoom = snapshot.zoom.coerceIn(0.3f, 4f),
    )
}
