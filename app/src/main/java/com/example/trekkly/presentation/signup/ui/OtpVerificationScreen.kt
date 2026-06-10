package com.example.trekkly.presentation.signup.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trekkly.R
import com.example.trekkly.common.components.VerticalSpacerLarge
import com.example.trekkly.common.components.VerticalSpacerSmall
import com.example.trekkly.presentation.AuthScreen.BackgroundLayer
import com.example.trekkly.presentation.signup.uievents.OtpEvent
import com.example.trekkly.presentation.signup.uievents.OtpUiState
import com.example.trekkly.presentation.signup.viewmodel.OtpViewModel
import com.example.trekkly.presentation.theme.PrimaryColor
import com.example.trekkly.presentation.theme.PrimaryText
import com.example.trekkly.presentation.theme.SecondaryColor
import com.example.trekkly.presentation.theme.TextColor
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    onBackClick:()-> Unit,
    onVerificationSuccess:()-> Unit,
    viewModel: OtpViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when(event){
                is OtpEvent.NavigateHome -> onVerificationSuccess()
            }
        }
    }

    OtpScreenContent(
        uiState =uiState,
        phoneNumber=phoneNumber,
        onBackClick=onBackClick,
        onDigitChanged = viewModel::onDigitChanged,
        onVerifyClick = viewModel::onVerifyClick,
        onResendClick = {
            (context as? Activity)?.let { viewModel.onResendClick(it) }
        }
    )

}

@Composable
fun OtpScreenContent(
    uiState: OtpUiState,
    phoneNumber: String,
    onBackClick: () -> Unit,
    onDigitChanged: (Int, String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit
) {
    val focusRequesters = remember { List(6){ FocusRequester() } }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
    Box(modifier = Modifier.fillMaxSize()){
        BackgroundLayer()
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))
        Column(modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp)) {
            BackButton(onBackClick = onBackClick,
                modifier = Modifier.padding(top = 16.dp))
            VerticalSpacerLarge()
            OtpHeadLine(phoneNumber)
            VerticalSpacerLarge()
            OtpDigitRow(digits = uiState.digits,
                focusRequesters= focusRequesters,
                onDigitChanged = onDigitChanged)
            VerticalSpacerLarge()
            VerifyButton(isLoading = uiState.isLoading, enabled = uiState.isOtpComplete,
                onClick = onVerifyClick)
            uiState.errorMessage?.let { message->
                VerticalSpacerSmall()
                Text(text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.weight(1f))
                ResendFooter(
                    canResend = uiState.canResend,
                    cooldownSeconds = uiState.resendCooldownSeconds,
                    onResendClick = onResendClick,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

        }
    }
}

@Composable
fun OtpDigitRow(
    digits: List<String>,
    focusRequesters: List<FocusRequester>,
    onDigitChanged: (Int, String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        digits.forEachIndexed { index, digit ->
            OtpDigitBox(
                value = digit,
                onValueChange = { newValue->
                    onDigitChanged(index,newValue)
                    if (newValue.isNotEmpty() && index< focusRequesters.lastIndex){
                        focusRequesters[index+1].requestFocus()
                    }
                },focusRequester= focusRequesters[index],
                onBackspaceOnEmpty = {
                    if (index > 0) focusRequesters[index - 1].requestFocus()
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ResendFooter(
    canResend: Boolean,
    cooldownSeconds: Int,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val annotatedText = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)) {
            append("Didn't receive it?  ")
        }
        if (canResend) {
            pushStringAnnotation(tag = "RESEND", annotation = "resend")
            withStyle(SpanStyle(color = SecondaryColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)) {
                append("Resend code")
            }
            pop()
        } else {
            withStyle(SpanStyle(color = PrimaryText, fontSize = 13.sp)) {
                append("Resend in ${cooldownSeconds}s")
            }
        }
    }
    ClickableText(
        text = annotatedText,
        modifier = modifier.fillMaxWidth(),
        style = TextStyle(textAlign = TextAlign.Center),
        onClick = { offset ->
            if (canResend) {
                annotatedText.getStringAnnotations(tag = "RESEND", start = offset, end = offset)
                    .firstOrNull()?.let { onResendClick() }
            }
        }
    )
}

@Composable
private fun VerifyButton(isLoading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor.copy(alpha = 0.8f),
            contentColor = TextColor,
            disabledContainerColor = PrimaryColor.copy(alpha = 0.4f),
            disabledContentColor = TextColor.copy(alpha = 0.4f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = TextColor, strokeWidth = 2.dp)
        } else {
            Text(text = "Verify & Continue", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}


@Composable
fun OtpDigitBox(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onBackspaceOnEmpty: () -> Unit,
    modifier: Modifier
) {
    BasicTextField(
        value=value,
        onValueChange = {newValue->
            val digit = newValue.filter(Char::isDigit).takeLast(1)
            onValueChange(digit)
        },
        modifier = modifier.height(64.dp)
            .focusRequester(focusRequester)
            .onKeyEvent{event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace && value.isEmpty()){
                    onBackspaceOnEmpty()
                    true
                }else false
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        cursorBrush = SolidColor(SecondaryColor),
        textStyle = TextStyle(
            color = TextColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(
                    width = 1.5.dp,
                    color = if (value.isNotEmpty()) SecondaryColor
                    else Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }
    )
}

@Composable
fun OtpHeadLine(phoneNumber: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.verification).uppercase(),
            color = PrimaryText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextColor)) { append(stringResource(R.string.enter_the)) }
                withStyle(SpanStyle(color = SecondaryColor)) { append(stringResource(R.string.four_digit_code)) }
            },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = PrimaryText)) { append(stringResource(R.string.sent_to)) }
                withStyle(SpanStyle(color = TextColor, fontWeight = FontWeight.SemiBold)) {
                    append(phoneNumber)
                }
            },
            style = MaterialTheme.typography.bodyMedium
        )

    }
}

@Composable
@Preview(showBackground = true)
fun OtpVerificationScreenPreview(){
    OtpScreenContent(
        uiState = OtpUiState(),
        phoneNumber = "+91 9876543210",
        onBackClick = {},
        onDigitChanged = { _, _ -> },
        onVerifyClick = {},
        onResendClick = {}
    )
}