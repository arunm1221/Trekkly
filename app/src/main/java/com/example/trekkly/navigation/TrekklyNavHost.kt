package com.example.trekkly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trekkly.presentation.AuthScreen.AuthenticationScreen
import com.example.trekkly.presentation.Splash.SplashScreen

@Composable
fun TrekklyNavHost(
    navHostController: NavHostController = rememberNavController(),
    startDestination: String =  ScreenDestination.SplashScreen.route
){
    NavHost(navController = navHostController,startDestination = startDestination) {
        composable(ScreenDestination.SplashScreen.route){
            SplashScreen(onSplashFinished = {
                navHostController.navigate(ScreenDestination.AuthenticationScreen.route
                ){
                    popUpTo(ScreenDestination.SplashScreen.route){
                        inclusive=true
                    }
                }
            })
        }

        composable(ScreenDestination.AuthenticationScreen.route){
            AuthenticationScreen(
                onLoginClick = {
                    // navHostController.navigate(ScreenDestination.Login.route)
                },
                onSignUpClick = {
                    // navHostController.navigate(ScreenDestination.SignUp.route)
                },
                onTermsClick = {
                    // navHostController.navigate(ScreenDestination.Terms.route)
                },
                onPrivacyPolicyClick = {
                    // navHostController.navigate(ScreenDestination.Privacy.route)
                }
            )
        }
    }

}