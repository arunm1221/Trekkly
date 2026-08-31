package com.example.trekkly.presentation.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trekkly.presentation.home.ui.HomeScreen

sealed class BottomTab(val route: String,val label: String,val icon: ImageVector){
    data object Home: BottomTab("home","Home", Icons.Default.Home)
    data object Trekks: BottomTab("trekks","Trekks", Icons.Default.Explore)
    data object Map: BottomTab("map","Map", Icons.Default.Map)
    data object Settings: BottomTab("settings","Settings", Icons.Default.Settings)
}

@Composable
fun MainScreen(){
    val tabs = listOf(BottomTab.Home, BottomTab.Trekks, BottomTab.Map, BottomTab.Settings)
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar{
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                tabs.forEach { tab->
                    NavigationBarItem(
                        selected = currentRoute==tab.route,
                        onClick = {
                            navController.navigate(tab.route){
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop=true
                                restoreState=true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding->
        NavHost(
            navController=navController,
            startDestination = BottomTab.Home.route,
            modifier = Modifier.padding(padding)
        ){
            composable(BottomTab.Home.route) { HomeScreen()}
            composable(BottomTab.Trekks.route) { PlaceholderScreen("Trekks") }
            composable(BottomTab.Map.route) { PlaceholderScreen("Map") }
            composable(BottomTab.Settings.route) { PlaceholderScreen("Settings") }
        }

    }

}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name)
    }
}