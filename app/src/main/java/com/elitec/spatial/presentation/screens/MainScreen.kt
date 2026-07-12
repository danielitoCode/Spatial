package com.elitec.spatial.presentation.screens

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elitec.spatial.R
import com.elitec.spatial.presentation.navigation.MainRoutesKey
import com.elitec.spatial.util.GlobalPreview
import kotlin.random.Random

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    var isRotatedTagsFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
            fontWeight = FontWeight.Bold,
            text = "Welcome to:  $isRotatedTagsFocused"
        )
        Spacer(Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                fontWeight = FontWeight.Bold,
                text = "SPATIAL"
            )
            Spacer(Modifier.width(10.dp))
            Image(
                painter = painterResource(R.drawable.spatial_icon_cleaned),
                contentDescription = "Spatial library banner",
                modifier = Modifier.size(80.dp),
            )

        }
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "Spatial...😶 What it is?"
            )
            Spacer(Modifier.height(5.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = "Spatial is a modern declarative 3D rendering library for Android inspired by Jetpack Compose."
            )
            Spacer(Modifier.height(15.dp))
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "Goal:"
            )
            Spacer(Modifier.height(5.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                text = "Make 3D scene creation feel as natural and expressive as Compose, while completely hiding the complexity of OpenGL and GPU pipelines from the developer."
            )
            Spacer(Modifier.height(15.dp))
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "🌐 Official web site:"
            )
            Text(
                textDecoration = TextDecoration.Underline,
                text = "https://github.com/danielitoCode/Spatial"
            )
            Spacer(Modifier.height(15.dp))
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "📒 Documentation:"
            )
            Text(
                textDecoration = TextDecoration.Underline,
                text = "https://deepwiki.com/danielitoCode/Spatial"
            )
            Spacer(Modifier.height(15.dp))
            Surface(
                shadowElevation = 5.dp,
                tonalElevation = 5.dp,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(15.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    text = "Spatial is not intended to become a full game engine. Instead, it focuses on:\n" +
                            "→ Declarative scene composition\n" +
                            "→ Smooth cinematic motion\n" +
                            "→ State-driven rendering\n" +
                            "→ Compose-first APIs\n" +
                            "→ Natural gestures\n" +
                            "→ Modular rendering architecture\n" +
                            "→ Clean GPU abstraction"
                )
            }
            Spacer(Modifier.height(15.dp))
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "Current status:"
            )
            Spacer(Modifier.height(5.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                text = "The Core #1 is Complete ✅, exposes its public Compose API from the root package.com.elitec.spatial_compose"
            )
            Spacer(Modifier.height(15.dp))
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "Core #1 Lighting Decision:"
            )
            Spacer(Modifier.height(5.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                text = "Core #1 keeps lighting as contracts only. exists so scene, light, and future renderer modules can agree on shape, direction, color, and intensity metadata, but Core #1 does not transport lights through the render frame and does not evaluate real lighting in shaders.LightData")
            Spacer(Modifier.height(15.dp))
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "Included:"
            )
            Spacer(Modifier.height(5.dp))
            FlowRow(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
                    .onFocusEvent { focusState ->
                        isRotatedTagsFocused = focusState.isFocused
                    },
            ) {
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Essential 3D primitives",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Orbit camera",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Smooth zoom",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Inertia and damping",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Declarative scene API",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Basic camera/transform motion system",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Gesture system",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Flat-color material rendering (no active lighting/shading in Core #1)",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Material abstraction",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Units system",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "GPU abstraction layer",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.rotate(
                        animateFloatAsState(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                            targetValue = if(isRotatedTagsFocused)
                                0f
                            else
                                Random
                                    .nextInt(0,8)
                                    .toFloat()
                        ).value
                    ),
                    shadowElevation = 3.dp,
                    tonalElevation = 5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Compose integration",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            Spacer(Modifier.height(15.dp))
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "Core #1 Success Criteria:"
            )
            Spacer(Modifier.height(5.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                text = "The core #1 is complete when:\n" +
                        "→ Primitives render correctly\n" +
                        "→ Camera feels cinematic\n" +
                        "→ Zoom feels smooth\n" +
                        "→ Gestures feel natural\n" +
                        "→ Motion transitions work\n" +
                        "→ Compose integration works\n" +
                        "→ API feels elegant\n" +
                        "→ 60 FPS remain stable\n" +
                        "→ OpenGL complexity stays hidden"
            )
            Spacer(Modifier.height(15.dp))
            Surface(
                shadowElevation = 5.dp,
                tonalElevation = 5.dp,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium,
                        text = "\uD83D\uDD2E Long-Term Vision"
                    )
                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Spatial is not intended to become a full game engine. Instead, it focuses on:\n" +
                                "→ Declarative scene composition\n" +
                                "→ Smooth cinematic motion\n" +
                                "→ State-driven rendering\n" +
                                "→ Compose-first APIs\n" +
                                "→ Natural gestures\n" +
                                "→ Modular rendering architecture\n" +
                                "→ Clean GPU abstraction"
                    )
                }
            }
            Text(
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                text = "Created by: danielitoCode"
            )
        }
    }
}

@Preview(showBackground = true, name = "Light View")
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    name = "Night view"
)
@Composable
fun MainScreenPreview() {
    GlobalPreview {
        MainScreen(
            Modifier
                .fillMaxSize()
                .padding(10.dp)
        )
    }
}