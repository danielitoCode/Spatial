package com.elitec.spatial.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.elitec.spatial.presentation.feature.code.screen.CodeScreen
import com.elitec.spatial.presentation.feature.shapes.models.ShapesContentScreen
import com.elitec.spatial.presentation.screens.LandingScreen
import com.elitec.spatial.presentation.screens.MainScreen
import com.elitec.spatial.presentation.screens.SplashScreen

@Composable
fun InternalNavigationWrapper(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(MainRoutesKey.MainHome)

    NavigationTagScreen(
        navigateTo = { route ->
            backStack.navigateTo(route)
        },
        modifier = Modifier.fillMaxSize()
    ) {
        NavDisplay(
            modifier = modifier,
            backStack = backStack,
            onBack = { backStack.navigateBack() },
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(250)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(250)
                )
            },
            popTransitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(250)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(250)
                )
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(250)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(250)
                )
            },
            entryProvider = entryProvider {
                entry<MainRoutesKey.MainHome> {
                    MainScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                entry<MainRoutesKey.Shapes> {
                    ShapesContentScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                entry<MainRoutesKey.Code> {
                    CodeScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        )
    }
}