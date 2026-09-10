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
import com.example.trekkly.presentation.login.ui.LoginScreen
import com.example.trekkly.presentation.main.ui.MainScreen
import com.example.trekkly.presentation.signup.ui.SignUpScreen
import com.example.trekkly.presentation.signup.ui.OtpVerificationScreen

@Composable
fun TrekklyNavHost(
    navHostController: NavHostController = rememberNavController(),
    startDestination: String =  ScreenDestination.SplashScreen.route
){
    /** Clears the whole back stack so Back from home never returns to auth. */
    fun goToMain() {
        navHostController.navigate(ScreenDestination.MainScreen.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navHostController,startDestination = startDestination) {
        composable(ScreenDestination.SplashScreen.route){
            SplashScreen(onSplashFinished = { isLoggedIn ->
                if (isLoggedIn) {
                    goToMain()
                } else {
                    navHostController.navigate(ScreenDestination.AuthenticationScreen.route) {
                        popUpTo(ScreenDestination.SplashScreen.route) { inclusive = true }
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
                            verificationId = verificationId,
                            phoneNumber = phoneNumber,
                            fullName = fullName
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
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

            OtpVerificationScreen(
                phoneNumber = phoneNumber,
                onBackClick = { navHostController.popBackStack() },
                onVerificationSuccess = { goToMain() }
            )
        }


        composable(route= ScreenDestination.LoginScreen.route){

            LoginScreen(
                onBackClick = {navHostController.popBackStack()},
                onLoginClick = { goToMain() },
                onSignUpClick = { navHostController.navigate(ScreenDestination.SignUpScreen.route) }

            )

        }

        composable (ScreenDestination.MainScreen.route){
            MainScreen()
        }
    }

}
