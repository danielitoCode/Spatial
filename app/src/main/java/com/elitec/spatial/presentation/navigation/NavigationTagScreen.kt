package com.elitec.spatial.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elitec.spatial.util.GlobalPreview

@Composable
fun NavigationTagScreen(
    navigateTo: (MainRoutesKey) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var pageSelected by rememberSaveable { mutableStateOf("Home") }

    val buttonsTagsItems = listOf(
        Pair("Home") {
            pageSelected = "Home"
            navigateTo(MainRoutesKey.MainHome)
        },
        Pair("Shapes") {
            pageSelected = "Shapes"
            navigateTo(MainRoutesKey.Shapes)
        },
        Pair("Examples") {
            pageSelected = "Examples"
            navigateTo(MainRoutesKey.Code)
        }
    )

    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.onBackground,
                shadowElevation = 5.dp,
                tonalElevation = 5.dp,
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp)
                ) {
                    items(buttonsTagsItems) { buttonItem ->
                        var iconSize = animateDpAsState(
                            if(pageSelected == buttonItem.first) 20.dp else 30.dp
                        )
                        var buttonBackground = animateColorAsState(
                            if(pageSelected == buttonItem.first)
                                MaterialTheme.colorScheme.background
                            else
                                MaterialTheme.colorScheme.onBackground
                        )
                        Button(
                            border = BorderStroke(1.dp,buttonBackground.value),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            onClick = buttonItem.second
                        ) {
                            Text(
                                text = buttonItem.first
                            )
                        }
                    }
                }
            }
        }
    ) {  innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            content()
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun NavigationTagScreenPreview() {
    GlobalPreview(
        modifier = Modifier.fillMaxSize()
    ) {
        NavigationTagScreen(
            navigateTo = {},
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "CONTENT")
            }
        }
    }
}