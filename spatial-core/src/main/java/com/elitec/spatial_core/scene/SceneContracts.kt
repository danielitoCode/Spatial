package com.elitec.spatial_core.scene

/**
 * Datos de material como contrato puro.
 * El módulo spatial-material implementa la lógica;
 * este tipo es lo que fluye por el pipeline de render.
 */
data class MaterialData(
    val r: Float = 0.8f,
    val g: Float = 0.8f,
    val b: Float = 0.8f,
    val a: Float = 1.0f,
    val baseColor: ColorFactor = ColorFactor(r, g, b, a),
    val metallicFactor: Float = 1.0f,
    val roughnessFactor: Float = 1.0f,
    val baseColorTexture: TextureReference? = null,
    val metallicRoughnessTexture: TextureReference? = null,
    val normalTexture: TextureReference? = null,
    val occlusionTexture: TextureReference? = null,
    val emissiveTexture: TextureReference? = null,
)

/** RGBA factor used by glTF PBR material contracts. */
data class ColorFactor(
    val r: Float = 0.8f,
    val g: Float = 0.8f,
    val b: Float = 0.8f,
    val a: Float = 1.0f,
)

/** Optional texture slot reference mirroring glTF textureInfo indices without owning image bytes. */
data class TextureReference(
    val index: Int,
    val texCoord: Int = 0,
)

/**
 * Core #1 mantiene este tipo como metadata compartida para módulos de escena/luz
 * y futuros renderers; no implica transporte por frame ni evaluación activa de
 * iluminación en el renderer actual.
 */
data class LightData(
    val dirX: Float = 0f,
    val dirY: Float = -1f,
    val dirZ: Float = 0f,
    val intensity: Float = 1f,
    val r: Float = 1f,
    val g: Float = 1f,
    val b: Float = 1f,
)

/**
 * Nodo procesado listo para enviar al renderer.
 * El scene graph produce estos nodos; el renderer los consume.
 * Ninguno de los dos necesita saber del otro.
 */
data class RenderableNode(
    val meshId: String,
    val modelMatrix: FloatArray = FloatArray(16) { if (it % 5 == 0) 1f else 0f },
    val material: MaterialData = MaterialData(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RenderableNode) return false
        return meshId == other.meshId &&
            modelMatrix.contentEquals(other.modelMatrix) &&
            material == other.material
    }

    override fun hashCode(): Int {
        var result = meshId.hashCode()
        result = 31 * result + modelMatrix.contentHashCode()
        result = 31 * result + material.hashCode()
        return result
    }
}
