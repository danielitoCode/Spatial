package com.elitec.spatial_compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.elitec.spatial_core.render.Color4

/** Convierte un [Color] de Compose a [Color4] inmutable de core (copy-by-value). */
fun Color.toColor4(): Color4 = Color4(this.red, this.green, this.blue, this.alpha)

/** Convierte un [Color4] de core a [Color] de Compose para interoperar con UI. */
fun Color4.toComposeColor(): Color = Color(red = r, green = g, blue = b, alpha = a)

/**
 * Color de clear recomendado para render basado en el `background` del Material Theme activo.
 *
 * Ownership:
 * - Compose provee un `Color` borrowed del tema actual.
 * - Se transforma a `Color4` por valor (sin compartir mutabilidad).
 */
@Composable
fun rememberMaterialThemeClearColor(): Color4 = MaterialTheme.colorScheme.background.toColor4()