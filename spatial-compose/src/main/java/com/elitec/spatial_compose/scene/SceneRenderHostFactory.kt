package com.elitec.spatial_compose.scene

import android.content.Context

public fun interface SceneRenderHostFactory {
    fun create(context: Context): SceneRenderHost
}