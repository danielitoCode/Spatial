package com.elitec.spatial.presentation.feature.shapes.models

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elitec.spatial.R
import com.elitec.spatial.util.GlobalPreview
import com.elitec.spatial_compose.Element
import com.elitec.spatial_compose.Gestures
import com.elitec.spatial_compose.Modifier3D
import com.elitec.spatial_compose.Scene
import com.elitec.spatial_compose.rememberCameraState
import com.elitec.spatial_compose.state.extention.autoRotate
import com.elitec.spatial_compose_runtime_adapter.DefaultSceneRenderHostFactory
import com.elitec.spatial_units.deg
import com.elitec.spatial_units.meters

private val shapeItems = listOf(
    ShapeSectionItem("Plane", "A simple plane in a axis orientation , 2D only", 0, {}, {}),
    ShapeSectionItem("Cube", "Native cube shape", 0, {}, {}),
    ShapeSectionItem("Sphere", "Native sphere shape", 0, {}, {}),
    ShapeSectionItem("Plane", "A simple plane in a axis orientation , 2D only", 0, {}, {}),
    ShapeSectionItem("Cube", "Native cube shape", 0, {}, {}),
    ShapeSectionItem("Sphere", "Native sphere shape", 0, {}, {}),
)

@Composable
fun ShapesContentScreen(
    modifier: Modifier = Modifier
) {
    val cyclicPositions = remember {
        listOf(
            Triple(0f, 0f, -4f),
            Triple(2f, 0f, -6f),
            Triple(-2f, 0f, -6f),
            Triple(3f, 1f, -8f),
            Triple(-3f, 1f, -8f),
            Triple(0f, 2f, -10f),
            Triple(4f, -1f, -9f),
            Triple(-4f, -1f, -9f),
        )
    }
    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.spatial_icon_cleaned),
                contentDescription = "Spatial icon",
                modifier = Modifier.size(50.dp)
            )
            Column {
                Text(
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    text = "Main shapes:",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = "Visualize a Spatial main predeterminate shapes",
                    color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                )
            }
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(shapeItems) { shapeItem ->
                val cameraState = rememberCameraState(yaw = 25f.deg, pitch = (-15f).deg, zoom = 1.25f)
                    .autoRotate( true)

                Card(
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 5.dp,
                        hoveredElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.surface.copy(0.8f),
                                        MaterialTheme.colorScheme.surface,
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface.copy(0.8f),
                                        MaterialTheme.colorScheme.primary
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        fontWeight = FontWeight.SemiBold,
                                        text = shapeItem.tittle,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = shapeItem.description,
                                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                                    )
                                }
                                Icon(
                                    painter = painterResource(R.drawable.ic_launcher_foreground),
                                    contentDescription = "Like shape",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                ) {
                                    Button(
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        onClick = {}
                                    ) {
                                        Text(
                                            text = "</>   CODE"
                                        )
                                    }
                                    Button(
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        onClick = {}
                                    ) {
                                        Text(
                                            color = MaterialTheme.colorScheme.surface,
                                            text = "[    ]   SCENE"
                                        )
                                    }
                                }
                                Scene(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    renderHostFactory = DefaultSceneRenderHostFactory,
                                    cameraState = cameraState,
                                    contentScale = 0.4f,
                                    gestures = Gestures.orbitAndZoom(),
                                    backgroundColor = Color.Blue
                                ) {
                                    when(shapeItem.tittle.lowercase()) {
                                        "plane" -> Element.Plane(
                                            modifier = Modifier3D.Default
                                                .size(4f.meters, 0.1f.meters, 3f.meters)
                                        )
                                        "cube" -> Element.Cube(
                                            modifier = Modifier3D.Default
                                                .size(2f.meters)
                                        )
                                        "sphere" ->  Element.Sphere(
                                            modifier = Modifier3D.Default
                                                .size(3f.meters)
                                        )
                                    }
                                }
                            }



                        }
                    }
                }
            }
        }
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
fun ShapesContentScreenPreview() {
    GlobalPreview {
        ShapesContentScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        )
    }
}