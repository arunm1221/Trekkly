package com.example.trekkly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trekkly.presentation.AuthScreen.AuthenticationScreen
import com.example.trekkly.presentation.Splash.SplashScreen
import com.example.trekkly.presentation.home.ui.HomeScreen
import com.example.trekkly.presentation.login.ui.LoginScreen
import com.example.trekkly.presentation.signup.ui.SignUpScreen
import com.example.trekkly.presentation.signup.ui.OtpVerificationScreen

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
                     navHostController.navigate(ScreenDestination.LoginScreen.route)
                },
                onSignUpClick = {
                     navHostController.navigate(ScreenDestination.SignUpScreen.route)
                },
                onTermsClick = {
                    // navHostController.navigate(ScreenDestination.Terms.route)
                },
                onPrivacyPolicyClick = {
                    // navHostController.navigate(ScreenDestination.Privacy.route)
                }
            )
        }
        composable(ScreenDestination.SignUpScreen.route){
            SignUpScreen(
                onBackClick = {
                    navHostController.popBackStack()
                },
                onLoginClick = {
                   navHostController.navigate(ScreenDestination.LoginScreen.route)
                },
                onNavigateToOtp = { verificationId, phoneNumber, fullName ->
                    navHostController.navigate(
                        ScreenDestination.OtpVerification.createRoute(
                            verificationId, phoneNumber, fullName
                        )
                    )
                }
            )
        }

        composable(
            route = ScreenDestination.OtpVerification.route,
            arguments = listOf(
                navArgument("verificationId") { type = NavType.StringType; defaultValue = "" },
                navArgument("phoneNumber") { type = NavType.StringType; defaultValue = "" },
                navArgument("fullName") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val fullName = backStackEntry.arguments?.getString("fullName") ?: ""

            OtpVerificationScreen(
                phoneNumber = phoneNumber,
                onBackClick = { navHostController.popBackStack() },
                onVerificationSuccess = {
                    navHostController.navigate(ScreenDestination.HomeScreen.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


        composable(route= ScreenDestination.LoginScreen.route){

            LoginScreen(
                onBackClick = {navHostController.popBackStack()},
                onLoginClick = {
                    navHostController.navigate(ScreenDestination.HomeScreen.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSignUpClick = { navHostController.navigate(ScreenDestination.SignUpScreen.route) }

            )

        }

        composable(route = ScreenDestination.HomeScreen.route) {
            HomeScreen(
                onTrekkClick = { trekkId ->
                    // TODO: navigate to trekk detail once built
                },
                onViewAllClick = { /* TODO: navigate to all trekks */ },
                onSearchClick = { /* TODO: navigate to search */ }
            )
        }
    }

}