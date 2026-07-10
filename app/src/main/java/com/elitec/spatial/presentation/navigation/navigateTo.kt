package com.elitec.spatial.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun NavBackStack<NavKey>.navigateTo(destination: NavKey) {
    add(destination)
}