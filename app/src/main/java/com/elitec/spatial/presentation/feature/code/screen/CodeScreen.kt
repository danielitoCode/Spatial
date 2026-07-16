package com.elitec.spatial.presentation.feature.code.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elitec.spatial.util.GlobalPreview
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.kodeview.view.CodeTextView

@Composable
fun CodeScreen(
    modifier: Modifier = Modifier
) {
    val highlights = remember {
        mutableStateOf(
            Highlights
                .Builder(
                    language = SyntaxLanguage.KOTLIN,
                    code = """
                        Scene(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    renderHostFactory = DefaultSceneRenderHostFactory,
                                    cameraState = cameraState,
                                    contentScale = 0.4f,
                                    gestures = Gestures.orbitAndZoom(),
                                    backgroundColor = Color.Transparent
                                ) {
                                    when (shapeItem.tittle.lowercase()) {
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
                    """.trimIndent()
                )
                .build()
        )
    }

    Column(
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            shadowElevation = 5.dp,
            tonalElevation = 5.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            CodeTextView(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                highlights = highlights.value
            )
        }
    }
}

@Preview
@Composable
fun CodeScreenPreview() {
    GlobalPreview {
        CodeScreen(modifier = Modifier.fillMaxSize())
    }
}