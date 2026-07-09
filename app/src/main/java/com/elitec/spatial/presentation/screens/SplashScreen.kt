package com.elitec.spatial.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elitec.spatial.R
import com.elitec.spatial.util.GlobalPreview


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(
                    Brush.radialGradient(
                       colors = listOf(
                           MaterialTheme.colorScheme.onSurface,
                           MaterialTheme.colorScheme.onSurface.copy(0.7f),
                           MaterialTheme.colorScheme.onSurface.copy(0.3f),
                           MaterialTheme.colorScheme.onSurface.copy(0.1f),
                           Color.Transparent
                       )
                    )
                )
        )
        Image(
            painter = painterResource(R.drawable.spatial_banner),
            contentDescription = "Spatial Library Banner",
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Preview(
    showBackground = true
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun SplashScreenPreview() {
    GlobalPreview {
        SplashScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}