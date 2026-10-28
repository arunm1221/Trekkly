package com.example.trekkly.presentation.Splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trekkly.R
import com.example.trekkly.presentation.theme.PrimaryCard
import com.example.trekkly.presentation.theme.PrimaryText
import com.example.trekkly.presentation.theme.SecondaryColor
import com.example.trekkly.presentation.theme.TextColor
import kotlinx.coroutines.delay
import java.util.Locale.getDefault
import kotlin.time.Duration.Companion.milliseconds

/**
 * Shows the branded splash, then routes by session state instead of always
 * sending the user to the auth screen.
 *
 * [onSplashFinished] receives true when a signed-in session was restored.
 */
@Composable
fun SplashScreen(
    onSplashFinished: (isLoggedIn: Boolean) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var animationFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2150.milliseconds)
        animationFinished = true
    }

    // Wait for both the animation and the session lookup before navigating.
    LaunchedEffect(animationFinished, isLoggedIn) {
        val resolved = isLoggedIn
        if (animationFinished && resolved != null) {
            onSplashFinished(resolved)
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150.milliseconds)
        contentVisible = true
    }
    Box(modifier = Modifier.fillMaxSize()){

        Image(painter = painterResource(R.drawable.mountain_trekk), contentDescription = "",
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().background(color = PrimaryCard))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Box(modifier = Modifier.align(Alignment.Center).fillMaxWidth(), contentAlignment = Alignment.Center){
            AnimatedVisibility(
                visible = contentVisible,
                enter = slideInVertically(
                    animationSpec = tween(durationMillis = 1000),
                    initialOffsetY = {fullHeight -> fullHeight/2}
                )+ fadeIn(animationSpec = tween(durationMillis = 1000))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ){
                    Image(imageVector = ImageVector.vectorResource(R.drawable.trekkly), contentDescription = "",
                        modifier = Modifier.size(86.dp).clip(CircleShape))

                    Text(text = buildAnnotatedString {
                        withStyle(SpanStyle(color = SecondaryColor, fontWeight = FontWeight.Bold)){
                            append(stringResource(R.string.Trekk))
                        }
                        withStyle(SpanStyle(color = TextColor, fontWeight = FontWeight.Bold)){
                            append(stringResource(R.string.ly))
                        }
                    }, style = MaterialTheme.typography.headlineLarge)

                    Text(text = stringResource(R.string.moto).uppercase(getDefault()),
                        color = PrimaryText, style = MaterialTheme.typography.labelSmall
                    )
                }
                
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenView(){
    SplashContent()
}