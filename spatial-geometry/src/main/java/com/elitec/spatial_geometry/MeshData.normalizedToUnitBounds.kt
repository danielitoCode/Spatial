package com.elitec.spatial_geometry

/**
 * Returns a copy of this [MeshData] with vertex positions uniformly rescaled and re-centered so
 * the mesh fits within a canonical unit bounding box (largest extent == 1.0, centered at the
 * origin) - matching the convention this engine's built-in primitives (`Cube`/`Sphere`/`Plane`)
 * already follow by construction (each spans exactly `-0.5..0.5` per axis).
 *
 * **Why this exists:** a `.glb` file's vertex positions are authored in whatever arbitrary
 * units/scale the source tool used - there's no guarantee they land anywhere near this engine's
 * ~1-unit-per-axis primitive convention. `Modifier3D.size(n.meters)` applies its size as a flat
 * multiplier directly on top of a mesh's raw vertex coordinates (see `Modifier3D.toModelMatrix`),
 * which only means "n meters across" for a mesh that was already exactly 1 unit across to begin
 * with. Without this normalization, the same `.size(3f.meters)` call would produce wildly
 * different real-world sizes for a primitive vs. a loaded model, and for a large source mesh
 * (hundreds/thousands of raw units across - not a hypothetical: see
 * `GltfBinaryParserTest`/`sample_model.glb`, whose raw positions span roughly ±400 to ±600 per
 * axis) can place geometry entirely outside the camera's far clip plane or leave the camera
 * effectively inside the mesh, making a correctly-loaded, correctly-uploaded model simply not
 * appear on screen.
 *
 * Normals, texCoords, indices, drawMode, and material are left untouched: a uniform scale with no
 * rotation or shear doesn't change normal directions, and texCoords/indices/material don't depend
 * on world-space position at all.
 *
 * A mesh with zero vertices, or whose bounding box has zero/non-finite extent (e.g. every vertex
 * at the same point), is returned unchanged rather than dividing by zero.
 */
fun MeshData.normalizedToUnitBounds(): MeshData {
    if (vertices.isEmpty()) return this

    var minX = Float.POSITIVE_INFINITY
    var minY = Float.POSITIVE_INFINITY
    var minZ = Float.POSITIVE_INFINITY
    var maxX = Float.NEGATIVE_INFINITY
    var maxY = Float.NEGATIVE_INFINITY
    var maxZ = Float.NEGATIVE_INFINITY

    var i = 0
    while (i < vertices.size) {
        val x = vertices[i]
        val y = vertices[i + 1]
        val z = vertices[i + 2]
        if (x < minX) minX = x
        if (x > maxX) maxX = x
        if (y < minY) minY = y
        if (y > maxY) maxY = y
        if (z < minZ) minZ = z
        if (z > maxZ) maxZ = z
        i += 3
    }

    val extent = maxOf(maxX - minX, maxY - minY, maxZ - minZ)
    if (extent <= 0f || !extent.isFinite()) return this

    val centerX = (minX + maxX) / 2f
    val centerY = (minY + maxY) / 2f
    val centerZ = (minZ + maxZ) / 2f
    val scale = 1f / extent

    val normalized = FloatArray(vertices.size)
    i = 0
    while (i < vertices.size) {
        normalized[i] = (vertices[i] - centerX) * scale
        normalized[i + 1] = (vertices[i + 1] - centerY) * scale
        normalized[i + 2] = (vertices[i + 2] - centerZ) * scale
        i += 3
    }

    return copy(vertices = normalized)
}
