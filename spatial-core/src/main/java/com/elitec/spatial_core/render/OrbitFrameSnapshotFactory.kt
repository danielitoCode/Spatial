package com.elitec.spatial_core.render

import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_math.Mat4Math

/**
 * Core #1 default camera rig: an orbit camera looking at the world origin, matching the eye/lookAt
 * convention implemented by `SpatialGlRenderer`.
 *
 * This is the single source of truth for turning a [CameraSnapshot] into concrete matrices. Both
 * [buildOrbitFrameSnapshot] (used to populate the [FrameSnapshot] API contract) and the GL renderer
 * derive their eye position from the same formula, so `FrameSnapshot.viewProjection` /
 * `FrameSnapshot.cameraPosition` accurately describe what will be drawn on screen.
 */
object OrbitCamera {
    const val DEFAULT_BASE_DISTANCE = 10f
    const val DEFAULT_FOV_Y_DEGREES = 45f
    const val DEFAULT_NEAR_PLANE = 0.1f
    const val DEFAULT_FAR_PLANE = 100f

    /** Distance from the origin for a given visual [zoom] factor. Guards against zero/negative/NaN zoom. */
    fun orbitDistanceForVisualZoom(zoom: Float, baseDistance: Float = DEFAULT_BASE_DISTANCE): Float {
        val safeZoom = if (zoom.isFinite()) {
            zoom.coerceIn(CameraSnapshot.MIN_ZOOM, CameraSnapshot.MAX_ZOOM)
        } else {
            1f
        }
        val safeBaseDistance = if (baseDistance.isFinite() && baseDistance > 0f) baseDistance else DEFAULT_BASE_DISTANCE
        return safeBaseDistance / safeZoom
    }

    fun eyePosition(cameraSnapshot: CameraSnapshot, baseDistance: Float = DEFAULT_BASE_DISTANCE): Vec3 {
        val distance = orbitDistanceForVisualZoom(cameraSnapshot.zoom, baseDistance)
        val (x, y, z) = Mat4Math.orbitEyePosition(
            yawDegrees = cameraSnapshot.yaw,
            pitchDegrees = cameraSnapshot.pitch,
            distance = distance,
        )
        return Vec3(x, y, z)
    }
}

/**
 * Builds a fully populated [FrameSnapshot] for the Core #1 orbit-camera rig: `viewProjection` and
 * `cameraPosition` reflect the real [cameraSnapshot], instead of the identity/zero defaults.
 *
 * @param aspectRatio width/height of the current viewport. Must come from `onSurfaceChanged`/layout;
 * an invalid (non-finite or non-positive) value safely falls back to a square aspect ratio.
 */
fun buildOrbitFrameSnapshot(
    frameTimeNanos: Long,
    cameraSnapshot: CameraSnapshot,
    aspectRatio: Float,
    clearColor: Color4 = Color4.TRANSPARENT,
    resources: StableFrameResources = StableFrameResources.empty(),
    baseOrbitDistance: Float = OrbitCamera.DEFAULT_BASE_DISTANCE,
    fovYDegrees: Float = OrbitCamera.DEFAULT_FOV_Y_DEGREES,
    nearPlane: Float = OrbitCamera.DEFAULT_NEAR_PLANE,
    farPlane: Float = OrbitCamera.DEFAULT_FAR_PLANE,
): FrameSnapshot {
    val eye = OrbitCamera.eyePosition(cameraSnapshot, baseOrbitDistance)

    val viewMatrix = Mat4Math.lookAt(
        eyeX = eye.x, eyeY = eye.y, eyeZ = eye.z,
        centerX = 0f, centerY = 0f, centerZ = 0f,
        upX = 0f, upY = 1f, upZ = 0f,
    )
    val projectionMatrix = Mat4Math.perspective(fovYDegrees, aspectRatio, nearPlane, farPlane)
    val viewProjection = Mat4Math.multiply(projectionMatrix, viewMatrix)

    return FrameSnapshot(
        frameTimeNanos = frameTimeNanos,
        viewProjection = Mat4.from(viewProjection),
        cameraPosition = eye,
        clearColor = clearColor,
        resources = resources,
    )
}
