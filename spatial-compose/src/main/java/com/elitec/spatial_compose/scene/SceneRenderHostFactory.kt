package com.elitec.spatial_compose.scene

import android.content.Context

internal fun interface SceneRenderHostFactory {
    fun create(context: Context): SceneRenderHost
}