package com.example.trekkly.presentation

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.trekkly.navigation.TrekklyNavHost
import com.example.trekkly.presentation.Splash.SplashScreen
import com.example.trekkly.presentation.theme.TrekklyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    private var keeSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { keeSplashOnScreen }
        splashScreen.setOnExitAnimationListener { provider ->
            val fadeOut = ObjectAnimator.ofFloat(
                provider.view, View.ALPHA,1f,0f
            )
            fadeOut.interpolator = AccelerateInterpolator()
            fadeOut.duration = 300L
            fadeOut.doOnEnd{provider.remove()}
            fadeOut.start()
        }
        setContent {
            keeSplashOnScreen =false
            TrekklyTheme {
                TrekklyNavHost()

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
