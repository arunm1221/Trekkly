package com.example.trekkly.presentation.AuthScreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trekkly.R
import com.example.trekkly.common.components.VerticalSpacer
import com.example.trekkly.common.components.VerticalSpacerSmall
import com.example.trekkly.presentation.theme.PrimaryText
import com.example.trekkly.presentation.theme.SecondaryColor
import com.example.trekkly.presentation.theme.TextColor


@Composable
fun AuthenticationScreen(
    onLoginClick:()-> Unit,
    onSignUpClick:()->Unit,
    onTermsClick:()-> Unit,
    onPrivacyPolicyClick:()-> Unit
){
    Box(modifier = Modifier.fillMaxWidth()){
        BackgroundLayer()
        Column(modifier = Modifier.fillMaxSize()) {
            BrandLockUp(modifier= Modifier.padding(start = 24.dp, top = 56.dp))
            Spacer(modifier = Modifier.weight(1f))
            WelcomeContent(
                onLoginClick = onLoginClick,
                onSignUpClick = onSignUpClick,
                onTermsClick = onTermsClick,
                onPrivacyPolicyClick = onPrivacyPolicyClick,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp)
            )
        }
    }

}

@Composable
fun WelcomeContent(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(R.string.moto_1),
            color = TextColor,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text =stringResource(R.string.track_content),
            color = PrimaryText.copy(alpha = 0.35f),
            fontFamily = FontFamily.Default

        )
        VerticalSpacerSmall()
        Button(onClick = onLoginClick,modifier= Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryColor,
                contentColor = Color.Black
            )) {
            Text(text = stringResource(R.string.login), fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onSignUpClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text(text = stringResource(R.string.signup), fontWeight = FontWeight.Bold)
        }
        VerticalSpacer()
        LegalText(onTermsClick=onTermsClick,onPrivacyPolicyClick)
        VerticalSpacer()
    }
}

@Composable
fun LegalText(onTermsClick: () -> Unit, onPrivacyPolicyClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)) {
            append("By continuing you agree to our ")
        }
        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(SpanStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)) {
            append("Terms")
        }
        pop()
        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)) {
            append(" & ")
        }
        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(SpanStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)) {
            append("Privacy Policy")
        }
        pop()
    }
    ClickableText(
        text = annotatedText,
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(textAlign = TextAlign.Center),
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                .firstOrNull()?.let { onTermsClick() }
            annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                .firstOrNull()?.let { onPrivacyPolicyClick() }
        }
    )
}

@Composable
fun BrandLockUp(modifier: Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        Image(imageVector = ImageVector.vectorResource(R.drawable.trekkly), contentDescription = "",
            modifier= Modifier.size(36.dp).clip(CircleShape))

        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = SecondaryColor, fontWeight = FontWeight.Bold)){
                append(stringResource(R.string.Trekk))
            }
            withStyle(style = SpanStyle(color = TextColor, fontWeight = FontWeight.Bold)){
                append(stringResource(R.string.ly))
            }
        }, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun BackgroundLayer() {
    Image(painter = painterResource(R.drawable.mountain_walk), contentDescription = "",
        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
}


@Preview
@Composable
fun AuthScreenPreview(){
    AuthenticationScreen(
        onLoginClick = {},
        onSignUpClick = {},
        onTermsClick = {},
        onPrivacyPolicyClick = {}
    )
}
