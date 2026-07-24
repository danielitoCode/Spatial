package com.elitec.spatial_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalResources
import com.elitec.spatial_compose.ModelResource.Companion.unwrapResId
import com.elitec.spatial_geometry.GltfBinaryParser
import com.elitec.spatial_geometry.MeshData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Loads and caches a [ModelResource] into a [MeshData] structure.
 *
 * This function acts as the 3D equivalent of `painterResource(id)`.
 * It uses a `LaunchedEffect` with [Dispatchers.IO] to parse the file off the main thread,
 * preventing UI jank when loading complex 50k-polygon models.
 *
 * While loading, it returns a simple fallback triangle to prevent GPU rendering errors.
 *
 * @param model The [ModelResource] to load (e.g., from [ModelResource.fromRawResource]).
 * @return The loaded [MeshData] (or a fallback triangle while loading).
 */
@Composable
public fun rememberModel(model: ModelResource): MeshData {
    val resources = LocalResources.current
    val cachedModels = LocalModelCache.current

    // 1. Check cache first
    cachedModels[model.id]?.let { return it }

    // 2. If not in cache, setup loading state
    val state = remember(model.id) { mutableStateOf(MeshData.FallbackTriangle) }

    LaunchedEffect(model.id) {
        // Check again in coroutine in case another composition beat us to it
        if (cachedModels.containsKey(model.id)) {
            state.value = cachedModels.getValue(model.id)
            return@LaunchedEffect
        }

        val loadedMesh = withContext(Dispatchers.IO) {
            try {
                val resId = unwrapResId(model)
                resources.openRawResource(resId).use { inputStream ->
                    GltfBinaryParser.parse(inputStream)
                }
            } catch (e: Exception) {
                // TODO: Provide a recognizable error mesh (e.g. red cube) in the future
                MeshData.FallbackTriangle
            }
        }

        com.elitec.spatial_geometry.GlobalMeshRegistry.register(model.id, loadedMesh)
        cachedModels[model.id] = loadedMesh
        state.value = loadedMesh
    }

    return state.value
}

/**
 * A process-wide cache for loaded 3D models mapped by their resource ID.
 * This prevents re-parsing the same GLB file on every recomposition or activity recreation.
 */
internal val LocalModelCache = staticCompositionLocalOf { mutableMapOf<String, MeshData>() }
