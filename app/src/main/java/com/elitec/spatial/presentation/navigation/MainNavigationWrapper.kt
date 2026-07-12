package com.elitec.spatial.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.elitec.spatial.presentation.feature.shapes.models.CodeScreen
import com.elitec.spatial.presentation.feature.shapes.models.ShapesContentScreen
import com.elitec.spatial.presentation.screens.LandingScreen
import com.elitec.spatial.presentation.screens.MainScreen
import com.elitec.spatial.presentation.screens.SplashScreen

@Composable
fun MainNavigationWrapper(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(MainRoutesKey.Splash)

    fun resetRoot(destination: MainRoutesKey) {
        while (backStack.isNotEmpty()) {
            backStack.removeLastOrNull()
        }
        backStack.navigateTo(destination)
    }

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
            entry<MainRoutesKey.Splash> {
                SplashScreen(
                    navigate = { backStack.navigateTo(MainRoutesKey.Landing) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<MainRoutesKey.Landing> {
                LandingScreen(
                    navigate = { backStack.navigateTo(MainRoutesKey.Home) },
                )
            }
            entry<MainRoutesKey.Home> {
                InternalNavigationWrapper(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}