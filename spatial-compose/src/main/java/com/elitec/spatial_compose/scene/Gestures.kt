package com.elitec.spatial_compose.scene

object Gestures {
    fun none(): SceneGestures = SceneGestures(SceneGestures.Mode.None)
    /**
     * Enables one-finger orbit gestures. The default is [GestureSensitivity.Adaptive], matching the
     * current public API while smoothing the effective per-pixel angular delta.
     */
    fun orbit(sensitivity: GestureSensitivity = GestureSensitivity.Adaptive): SceneGestures =
        SceneGestures(SceneGestures.Mode.Orbit, sensitivity)

    /**
     * Enables one-finger orbit and two-finger pinch zoom gestures at the same time.
     */
    fun orbitAndZoom(sensitivity: GestureSensitivity = GestureSensitivity.Adaptive): SceneGestures =
        SceneGestures(SceneGestures.Mode.OrbitAndZoom, sensitivity)
}