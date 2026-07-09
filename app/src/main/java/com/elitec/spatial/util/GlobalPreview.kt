package com.elitec.spatial.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elitec.spatial.ui.theme.SpatialTheme

@Composable
fun GlobalPreview(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SpatialTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = modifier.fillMaxSize(),
            content = content
        )
    }
}