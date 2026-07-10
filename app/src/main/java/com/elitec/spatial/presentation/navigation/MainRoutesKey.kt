package com.elitec.spatial.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class MainRoutesKey: NavKey {
    @Serializable
    object Splash : MainRoutesKey()

    @Serializable
    object Landing : MainRoutesKey()

    @Serializable
    object MainHome : MainRoutesKey()

    @Serializable
    object Shapes : MainRoutesKey()

    @Serializable
    object Examples : MainRoutesKey()

    @Serializable
    object Code : MainRoutesKey()

    @Serializable
    object About: MainRoutesKey()
}