package com.elitec.spatial_compose.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.scene.SceneElement
import com.elitec.spatial_compose.shapes.PrimitiveShape

@Immutable
object Element {
    @Composable
    fun Cube(modifier: Modifier3D = Modifier3D.Default) {
        SceneElement(shape = PrimitiveShape.Cube, modifier = modifier)
    }

    @Composable
    fun Sphere(modifier: Modifier3D = Modifier3D.Default) {
        SceneElement(shape = PrimitiveShape.Sphere, modifier = modifier)
    }

    @Composable
    fun Plane(modifier: Modifier3D = Modifier3D.Default) {
        SceneElement(shape = PrimitiveShape.Plane, modifier = modifier)
    }
}