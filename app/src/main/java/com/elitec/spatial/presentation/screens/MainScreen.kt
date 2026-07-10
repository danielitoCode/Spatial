package com.elitec.spatial.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elitec.spatial.presentation.navigation.MainRoutesKey
import com.elitec.spatial.util.GlobalPreview

@Composable
fun MainScreen(
    navigateTo: (MainRoutesKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GlobalPreview {
        MainScreen(
            {},
            Modifier.fillMaxSize().padding(10.dp)
        )
    }
}