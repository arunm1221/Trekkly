package com.example.trekkly.presentation.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trekkly.common.components.VerticalSpacerLarge
import com.example.trekkly.domain.model.CountryCode
import com.example.trekkly.presentation.AuthScreen.BackgroundLayer
import com.example.trekkly.presentation.login.event.LoginEvent
import com.example.trekkly.presentation.login.event.LoginUiState
import com.example.trekkly.presentation.login.viewmodel.LoginViewModel
import com.example.trekkly.presentation.signup.ui.BackButton
import com.example.trekkly.presentation.signup.ui.CountryCodeSelected
import com.example.trekkly.presentation.theme.PrimaryText
import com.example.trekkly.presentation.theme.SecondaryColor
import com.example.trekkly.presentation.theme.TextColor
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    onBackClick:()-> Unit,
    onLoginClick:()-> Unit,
    onSignUpClick:()->Unit,
    viewModel: LoginViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.event.collectLatest { event ->
            when(event){
                is LoginEvent.NavigateToHome -> onLoginClick()
            }

        }
    }

    LoginContent(
        uiState=uiState,
        onBackCLick = onBackClick,
        onLoginClick = viewModel::onLoginClicked,
        onSignUpClick = onSignUpClick,
        onPhoneNUmberChange = viewModel::onPhoneNumberChanged,
        onCountrySelected = viewModel::onCountryCodeChanged


    )

}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onBackCLick: () -> Unit,
    onLoginClick: ()-> Unit,
    onSignUpClick: () -> Unit,
    onPhoneNUmberChange: (String)-> Unit,
    onCountrySelected: (CountryCode)-> Unit
) {
    Box(modifier = Modifier.fillMaxSize())
    BackgroundLayer()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
    )
    Column(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
        .padding(horizontal = 24.dp)) {

        BackButton(
            onBackClick = onBackCLick,
            modifier = Modifier.padding(top = 16.dp)
        )
        VerticalSpacerLarge()
        LoginHeadLine()
        VerticalSpacerLarge()
        LoginForm(uiState,
            onPhoneNumberChanged = onPhoneNUmberChange,
            onCountrySelected = onCountrySelected)
        VerticalSpacerLarge()
        LoginButton(uiState.isLoading, enabled = uiState.isFormValid,onLoginClick)
        Spacer(modifier = Modifier.weight(1f))
        LoginFooter(onSignUpClick = onSignUpClick,
            modifier = Modifier.padding(bottom = 32.dp))


    }

}

@Composable
private fun LoginFooter(onSignUpClick: () -> Unit, modifier: Modifier = Modifier) {
    val annotatedText = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)) {
            append("New to Trekly?  ")
        }
        pushStringAnnotation(tag = "SIGNUP", annotation = "signup")
        withStyle(SpanStyle(color = SecondaryColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)) {
            append("Sign up")
        }
        pop()
    }
    ClickableText(
        text = annotatedText,
        modifier = modifier.fillMaxWidth(),
        style = TextStyle(textAlign = TextAlign.Center),
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "SIGNUP", start = offset, end = offset)
                .firstOrNull()?.let { onSignUpClick() }
        }
    )
}

@Composable
fun LoginForm(
    uiState: LoginUiState,
    onPhoneNumberChanged: (String) -> Unit,
    onCountrySelected: (CountryCode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(com.example.trekkly.R.string.phone_number).uppercase(),
            color = PrimaryText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {Text(text = "0000000000")},
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            leadingIcon = {
                CountryCodeSelected(selected = uiState.selectedCountryCode,
                    countryCodes = uiState.countryCodes,
                    onCountryCodeSelected = onCountrySelected)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor,
                focusedBorderColor = SecondaryColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedContainerColor = Color.White.copy(alpha = 0.06f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.06f),
                cursorColor = SecondaryColor,
                focusedPlaceholderColor = PrimaryText.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = PrimaryText.copy(alpha = 0.6f)
            )

        )
        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

    }
}

@Composable
private fun LoginButton(isLoading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2D4A3E),
            contentColor = TextColor,
            disabledContainerColor = Color(0xFF2D4A3E).copy(alpha = 0.4f),
            disabledContentColor = TextColor.copy(alpha = 0.4f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = TextColor, strokeWidth = 2.dp)
        } else {
            Text(text = stringResource(com.example.trekkly.R.string.login), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LoginHeadLine() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(com.example.trekkly.R.string.welcome_back).uppercase(),
            color = PrimaryText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextColor)) { append("Back on\n") }
                withStyle(SpanStyle(color = SecondaryColor)) { append("the trail.") }
            },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )

    }
}


@Preview(showBackground = true)
@Composable
fun LoginPreview(){
    LoginContent(
        uiState = LoginUiState(),
        onBackCLick = {},
        onLoginClick = {},
        onSignUpClick = {},
        onPhoneNUmberChange = {}
    ) { }
}