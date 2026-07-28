package com.elitec.spatial_material

import com.elitec.spatial_core.scene.MaterialData

/**
 * Public material abstraction used by Spatial APIs.
 *
 * Render backends still consume [MaterialData] as the low-level scene contract;
 * implementations of this interface own the conversion from expressive material
 * models to that renderer-friendly representation.
 */
sealed interface SpatialMaterial {
    /** Converts this public material model into the scene/render contract. */
    fun toMaterialData(): MaterialData
}

/**
 * Physically based material description.
 *
 * The current renderer contract only carries base color, so [metallic] and
 * [roughness] are retained in the public model for forward compatibility while
 * [toMaterialData] projects the base color into [MaterialData].
 */
data class PbrMaterial(
    val r: Float = 0.8f,
    val g: Float = 0.8f,
    val b: Float = 0.8f,
    val a: Float = 1.0f,
    val metallic: Float = 0.0f,
    val roughness: Float = 0.5f,
) : SpatialMaterial {
    override fun toMaterialData(): MaterialData = MaterialData(
        r = r,
        g = g,
        b = b,
        a = a,
        metallicFactor = metallic,
        roughnessFactor = roughness
    )
}

/**
 * Unlit material that contributes only its color to the current render pipeline.
 */
data class UnlitMaterial(
    val r: Float = 0.8f,
    val g: Float = 0.8f,
    val b: Float = 0.8f,
    val a: Float = 1.0f,
) : SpatialMaterial {
    override fun toMaterialData(): MaterialData = MaterialData(r = r, g = g, b = b, a = a)
}

/** Wraps an existing core [MaterialData] value as a public [SpatialMaterial]. */
fun MaterialData.toSpatialMaterial(): SpatialMaterial = PbrMaterial(
    r = r,
    g = g,
    b = b,
    a = a,
    metallic = metallicFactor,
    roughness = roughnessFactor
)