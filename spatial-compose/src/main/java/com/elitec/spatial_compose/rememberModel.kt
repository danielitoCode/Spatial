package com.elitec.spatial_compose

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalResources
import com.elitec.spatial_compose.ModelResource.Companion.unwrapResId
import com.elitec.spatial_geometry.GlobalMeshRegistry
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
 * @return The loaded [MeshData], [MeshData.FallbackTriangle] while loading, or [MeshData.ErrorMesh] after a failed load.
 */
@Composable
public fun rememberModel(model: ModelResource): MeshData {
    val resources = LocalResources.current
    val cachedModels = LocalModelCache.current

    Log.i(TAG, "rememberModel ENTER id=${model.id} rawRes=${model.rawResIdOrNull()}")

    // 1. Check cache first. Re-publish cached meshes so renderers created after loading can JIT
    // upload the real mesh from the global registry on their first frame.
    cachedModels[model.id]?.let { cachedMesh ->
        Log.i(
            TAG,
            "rememberModel CACHE_HIT id=${model.id} verts=${cachedMesh.vertexCount} idx=${cachedMesh.indexCount}",
        )
        GlobalMeshRegistry.register(model.id, cachedMesh)
        return cachedMesh
    }

    // 2. If not in cache, setup loading state and publish a visible placeholder under the final
    // mesh id before the async load completes. SpatialGlRenderer can then JIT upload this fallback
    // immediately instead of skipping the node as unknown.
    val state = remember(model.id) { mutableStateOf<ModelLoadState>(ModelLoadState.Loading) }
    Log.i(TAG, "rememberModel STATE id=${model.id} phase=${state.value::class.simpleName}")

    LaunchedEffect(model.id) {
        Log.i(TAG, "rememberModel LaunchedEffect START id=${model.id}")
        GlobalMeshRegistry.register(model.id, MeshData.FallbackTriangle)
        Log.i(TAG, "rememberModel registered FallbackTriangle id=${model.id}")

        cachedModels[model.id]?.let { cachedMesh ->
            Log.i(TAG, "rememberModel LaunchedEffect CACHE_HIT id=${model.id}")
            GlobalMeshRegistry.register(model.id, cachedMesh)
            state.value = ModelLoadState.Loaded(cachedMesh)
            return@LaunchedEffect
        }

        val loadState = withContext(Dispatchers.IO) {
            try {
                val resId = unwrapResId(model)
                Log.i(TAG, "rememberModel IO openRawResource resId=$resId id=${model.id}")
                resources.openRawResource(resId).use { inputStream ->
                    val available = runCatching { inputStream.available() }.getOrDefault(-1)
                    Log.i(TAG, "rememberModel IO stream opened availableBytes=$available → GltfBinaryParser.parse")
                    val mesh = GltfBinaryParser.parse(inputStream)
                    Log.i(
                        TAG,
                        "rememberModel IO PARSE_OK id=${model.id} verts=${mesh.vertexCount} " +
                            "idx=${mesh.indexCount} hasIdx=${mesh.hasIndices}",
                    )
                    ModelLoadState.Loaded(mesh)
                }
            } catch (e: Exception) {
                Log.e(TAG, "rememberModel IO PARSE_FAIL id=${model.id}", e)
                ModelLoadState.Error(MeshData.ErrorMesh, e)
            }
        }

        val loadedMesh = loadState.mesh
        Log.i(
            TAG,
            "rememberModel REGISTER_FINAL id=${model.id} phase=${loadState::class.simpleName} " +
                "verts=${loadedMesh.vertexCount} idx=${loadedMesh.indexCount}",
        )

        GlobalMeshRegistry.register(model.id, loadedMesh)
        cachedModels[model.id] = loadedMesh
        state.value = loadState
        Log.i(TAG, "rememberModel DONE id=${model.id} registrySize=${GlobalMeshRegistry.size()}")
    }

    return state.value.mesh
}

private const val TAG = "SpatialModelLoad"

/**
 * A process-wide cache for loaded 3D models mapped by their resource ID.
 * This prevents re-parsing the same GLB file on every recomposition or activity recreation.
 */
internal val LocalModelCache = staticCompositionLocalOf { mutableMapOf<String, MeshData>() }

/** Internal state kept so failures are distinguishable from in-flight loading. */
internal sealed interface ModelLoadState {
    val mesh: MeshData

    data object Loading : ModelLoadState {
        override val mesh: MeshData = MeshData.FallbackTriangle
    }

    data class Loaded(override val mesh: MeshData) : ModelLoadState

    data class Error(
        override val mesh: MeshData,
        val cause: Throwable,
    ) : ModelLoadState
}
