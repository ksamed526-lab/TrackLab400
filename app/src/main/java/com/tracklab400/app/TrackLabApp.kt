package com.tracklab400.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tracklab400.app.ui.navigation.TrackLabNavHost
import com.tracklab400.app.ui.navigation.TrackLabRoutes
import com.tracklab400.app.ui.navigation.trackLabBottomItems
import com.tracklab400.app.ui.navigation.trackLabBottomRoutes
import kotlinx.coroutines.delay

@Composable
fun TrackLabApp(
    navController: NavHostController = rememberNavController(),
    pendingRoute: String? = null,
    onPendingRouteConsumed: () -> Unit = {},
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in trackLabBottomRoutes

    LaunchedEffect(pendingRoute) {
        val target = pendingRoute ?: return@LaunchedEffect
        try {
            navController.navigate(TrackLabRoutes.HOME) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
            delay(250)
            navController.navigate(target) {
                launchSingleTop = true
            }
        } catch (_: IllegalArgumentException) {
            // Hedef rota geçersizse ana ekranda kal
        }
        onPendingRouteConsumed()
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    trackLabBottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.labelRes),
                                )
                            },
                            label = { Text(stringResource(item.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        TrackLabNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
