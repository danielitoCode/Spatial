package com.elitec.spatial.presentation.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LandingScreen(
    navigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(null) {
        delay(2000.milliseconds)
        navigate()
    }
    Text("LANDING")
}