package com.example.trekkly.navigation

sealed class ScreenDestination(val route: String){

    data object SplashScreen: ScreenDestination("SplashScreen")
    data object AuthenticationScreen: ScreenDestination("AuthenticationScreen")

}