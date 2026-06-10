package com.example.trekkly.navigation

import android.net.Uri

sealed class ScreenDestination(val route: String) {

    data object SplashScreen : ScreenDestination("SplashScreen")
    data object AuthenticationScreen : ScreenDestination("AuthenticationScreen")
    data object SignUpScreen : ScreenDestination("SignUpScreen")

    /**
     * Query-parameter route — safer than path parameters for strings that may contain
     * special characters like '+' (dial codes) or Firebase's verificationId format.
     *
     * Usage: navController.navigate(OtpVerification.createRoute(vId, phone, name))
     */
    data object OtpVerification : ScreenDestination(
        "OtpVerification?verificationId={verificationId}&phoneNumber={phoneNumber}&fullName={fullName}"
    ) {
        fun createRoute(
            verificationId: String,
            phoneNumber: String,
            fullName: String
        ): String = "OtpVerification?" +
                "verificationId=${Uri.encode(verificationId)}&" +
                "phoneNumber=${Uri.encode(phoneNumber)}&" +
                "fullName=${Uri.encode(fullName)}"
    }
}