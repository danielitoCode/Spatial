package com.elitec.spatial.presentation.models

data class ShapeSectionItem(
    val tittle: String,
    val description: String,
    val image: Int,
    val onCodeClick: () -> Unit,
    val onSceneClick: () -> Unit
)
