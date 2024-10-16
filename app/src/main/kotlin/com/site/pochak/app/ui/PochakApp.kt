package com.site.pochak.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.site.pochak.app.core.designsystem.component.PochakNavigationBar
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Navy00
import com.site.pochak.app.feature.login.navigation.LoginRoute
import com.site.pochak.app.navigation.PochakNavHost

@Composable
fun PochakApp(
    appState: PochakAppState,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentDestination?.hasRoute(LoginRoute::class) == false) {
                PochakNavigationBar {
                    appState.topLevelDestinations.forEach { destination ->
                        val selected =
                            currentDestination.hierarchy.any { it.hasRoute(destination.route) }

                        NavigationBarItem(
                            selected = selected,
                            onClick = { appState.navigateToTopLevelDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = if (selected) destination.selectedIconId else destination.unselectedIconId),
                                    contentDescription = stringResource(destination.titleTextId),
                                )
                            },
                            label = { Text(stringResource(destination.titleTextId)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Navy00,
                                unselectedIconColor = Gray03,
                                selectedTextColor = Navy00,
                                unselectedTextColor = Gray03,
                                indicatorColor = Color.Transparent,
                            ),
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PochakNavHost(appState = appState)
        }
    }
}
