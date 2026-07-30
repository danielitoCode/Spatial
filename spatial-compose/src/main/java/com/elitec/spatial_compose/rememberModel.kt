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
import com.elitec.spatial_geometry.normalizedToUnitBounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Loads and caches a [ModelResource] into a [MeshData] structure.
 *
 * Parsed meshes are centered and scaled to a unit AABB (see [normalizedToUnitBounds]) so
 * [Modifier3D.size] frames them like Core #1 primitives.
 */
@Composable
public fun rememberModel(model: ModelResource): MeshData {
    val resources = LocalResources.current
    val cachedModels = LocalModelCache.current

    Log.i(TAG, "rememberModel ENTER id=${model.id} rawRes=${model.rawResIdOrNull()}")

    cachedModels[model.id]?.let { cachedMesh ->
        val mesh = ensureUnitBounds(cachedMesh, model.id, fromCache = true)
        if (mesh !== cachedMesh) {
            cachedModels[model.id] = mesh
        }
        GlobalMeshRegistry.register(model.id, mesh)
        return mesh
    }

    val state = remember(model.id) { mutableStateOf<ModelLoadState>(ModelLoadState.Loading) }
    Log.i(TAG, "rememberModel STATE id=${model.id} phase=${state.value::class.simpleName}")

    LaunchedEffect(model.id) {
        Log.i(TAG, "rememberModel LaunchedEffect START id=${model.id}")
        GlobalMeshRegistry.register(model.id, MeshData.FallbackTriangle)

        cachedModels[model.id]?.let { cachedMesh ->
            val mesh = ensureUnitBounds(cachedMesh, model.id, fromCache = true)
            cachedModels[model.id] = mesh
            GlobalMeshRegistry.register(model.id, mesh)
            state.value = ModelLoadState.Loaded(mesh)
            return@LaunchedEffect
        }

        val loadState = withContext(Dispatchers.IO) {
            try {
                val resId = unwrapResId(model)
                Log.i(TAG, "rememberModel IO openRawResource resId=$resId id=${model.id}")
                resources.openRawResource(resId).use { inputStream ->
                    val available = runCatching { inputStream.available() }.getOrDefault(-1)
                    Log.i(TAG, "rememberModel IO stream opened availableBytes=$available → GltfBinaryParser.parse")
                    val rawMesh = GltfBinaryParser.parse(inputStream)
                    Log.i(
                        TAG,
                        "rememberModel IO PARSE_OK id=${model.id} verts=${rawMesh.vertexCount} " +
                            "idx=${rawMesh.indexCount} rawBounds=${rawMesh.computeBounds()}",
                    )
                    val mesh = ensureUnitBounds(rawMesh, model.id, fromCache = false)
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
                "verts=${loadedMesh.vertexCount} idx=${loadedMesh.indexCount} bounds=${loadedMesh.computeBounds()}",
        )

        GlobalMeshRegistry.register(model.id, loadedMesh)
        cachedModels[model.id] = loadedMesh
        state.value = loadState
        Log.i(TAG, "rememberModel DONE id=${model.id} registrySize=${GlobalMeshRegistry.size()}")
    }

    return state.value.mesh
}

/**
 * Unit AABB = longest axis ≤ 1 after [normalizedToUnitBounds].
 * Cached meshes from older builds may still be authoring-scale (±100s of units).
 */
private fun ensureUnitBounds(mesh: MeshData, modelId: String, fromCache: Boolean): MeshData {
    val bounds = mesh.computeBounds()
    val extent = bounds?.let {
        maxOf(it.extentX, it.extentY, it.extentZ)
    } ?: 0f
    if (extent <= 1.5f && extent.isFinite()) {
        Log.i(
            TAG,
            "rememberModel bounds OK id=$modelId fromCache=$fromCache extent=$extent bounds=$bounds",
        )
        return mesh
    }
    val normalized = mesh.normalizedToUnitBounds()
    Log.w(
        TAG,
        "rememberModel RE-NORMALIZE id=$modelId fromCache=$fromCache " +
            "rawExtent=$extent → bounds=${normalized.computeBounds()}",
    )
    return normalized
}

private const val TAG = "SpatialModelLoad"

internal val LocalModelCache = staticCompositionLocalOf { mutableMapOf<String, MeshData>() }

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
