package com.elitec.spatial_compose

import androidx.annotation.RawRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * Identifies a 3D model asset that can be loaded and rendered by Spatial.
 *
 * This acts as the equivalent of `painterResource(R.drawable.x)` for 3D meshes.
 * The actual loading and parsing of the [MeshData] is handled by [rememberModel],
 * which caches the result by [id] so the model is only parsed once.
 *
 * @see rememberModel
 */
@Immutable
public sealed interface ModelResource {
    /**
     * A unique string identifier for this resource, used as the cache key and the `meshId`
     * in the underlying [RenderableNode]. Must be unique across all loaded models.
     */
    public val id: String

    /**
     * Returns the Android raw resource ID (`R.raw.xxx`) if this resource points to one.
     * Returns `null` for resource types that don't use Android resource IDs.
     */
    public fun rawResIdOrNull(): Int?

    public companion object {
        /**
         * Creates a reference to a glTF Binary (.glb) file located in the app's `/res/raw/` directory.
         *
         * Usage:
         * ```
         * Element.Model(
         *     model = ModelResource.fromRawResource(R.raw.my_model),
         *     modifier = Modifier3D.Default.size(2f.meters)
         * )
         * ```
         */
        @Stable
        public fun fromRawResource(@RawRes resId: Int): ModelResource {
            return RawResourceModel(resId)
        }
    }
}

/**
 * Internal concrete implementation of [ModelResource] pointing to an Android raw resource.
 */
@Stable
@Immutable
class RawResourceModel(
    @RawRes public val resId: Int,
) : ModelResource {
    override val id: String get() = "raw:$resId"
    override fun rawResIdOrNull(): Int = resId

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RawResourceModel) return false
        return resId == other.resId
    }

    override fun hashCode(): Int = resId
}
