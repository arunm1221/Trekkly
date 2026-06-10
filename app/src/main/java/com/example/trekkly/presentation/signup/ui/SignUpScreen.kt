package com.example.trekkly.presentation.signup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.app.Activity
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
import androidx.core.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trekkly.common.components.HorizontalSpacerSmall
import com.example.trekkly.common.components.VerticalSpacer
import com.example.trekkly.domain.model.CountryCode
import com.example.trekkly.presentation.AuthScreen.BackgroundLayer
import com.example.trekkly.presentation.signup.uievents.SignUpEvent
import com.example.trekkly.presentation.signup.uievents.SignUpState
import com.example.trekkly.presentation.signup.viewmodel.SignUpViewModel
import com.example.trekkly.presentation.theme.PrimaryText
import com.example.trekkly.presentation.theme.SecondaryColor
import com.example.trekkly.presentation.theme.TextColor
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import java.util.Locale.getDefault

@Composable
fun SignUpScreen(
    onBackClick:()-> Unit,
    onLoginClick:() -> Unit,
    onNavigateToOtp: (verificationId: String, phoneNumber: String, fullName: String)-> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collectLatest {
            event ->
            when(event){
                is SignUpEvent.NavigateToOtp -> onNavigateToOtp(
                    event.verificationId,
                    event.fullPhoneNumber,
                    event.fullName
                )
            }
        }
    }

    SignUpContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onFullNameChanged = viewModel::onFullNameChanged,
        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onCountryCodeSelected = viewModel::onCountrySelected,
        onSignUpClick = {
            (context as? Activity)?.let { viewModel.onSignUpClicked(it) }
        }
    )

}

@Composable
fun SignUpContent(
    uiState: SignUpState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onCountryCodeSelected: (CountryCode) -> Unit,
    onSignUpClick: () -> Unit,

    ) {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        BackgroundLayer()
        Box(modifier = Modifier.fillMaxSize()
            .background(color = Color.Black.copy(alpha = .45f)))
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            BackButton(onBackClick = onBackClick, modifier = Modifier.padding(top = 56.dp))
            VerticalSpacer()
            SignUpHeadLine()
            VerticalSpacer()
            SignUpForm(
                uiState = uiState,
                onFullNameChanged = onFullNameChanged,
                onPhoneNumberChanged = onPhoneNumberChanged,
                onCountryCodeSelected = onCountryCodeSelected
            )
            VerticalSpacer()
            SignUpButton(
                isLoading = uiState.isLoading,
                enabled = uiState.isFormValid,
                onClick = onSignUpClick
            )
            Spacer(modifier = Modifier.weight(1f))
            SignUpFooter(onLoginClick = onLoginClick, modifier = Modifier.padding(bottom = 32.dp))
        }
    }


}

@Composable
private fun SignUpFooter(onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    val annotatedText = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)) {
            append("Already have an account? ")
        }
        pushStringAnnotation(tag = "LOGIN", annotation = "login")
        withStyle(SpanStyle(color = SecondaryColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)) {
            append("Log in")
        }
        pop()
    }
    ClickableText(
        text = annotatedText,
        modifier = modifier.fillMaxWidth(),
        style = TextStyle(textAlign = TextAlign.Center),
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "LOGIN", start = offset, end = offset)
                .firstOrNull()?.let { onLoginClick() }
        }
    )
}

@Composable
fun SignUpButton(isLoading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SecondaryColor,
            contentColor = Color.Black,
            disabledContainerColor = SecondaryColor.copy(alpha = 0.4f),
            disabledContentColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black, strokeWidth = 2.dp)
        } else {
            Text(text = "Sign Up", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun SignUpForm(
    uiState: SignUpState,
    onFullNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onCountryCodeSelected: (CountryCode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        LabelField(label=stringResource(com.example.trekkly.R.string.fullname).uppercase()){

            SignUpTextField(
                value = uiState.fullName,
                onValueChange = onFullNameChanged,
                placeholder = "Alex Trekker",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PrimaryText)
                }
            )
        }
        LabelField(label = "PHONE NUMBER") {
            PhoneNumberField(
                phoneNumber = uiState.phoneNumber,
                countryCodes = uiState.countryCode,
                selectedCountryCode = uiState.selectedCountryCode,
                onPhoneNumberChanged = onPhoneNumberChanged,
                onCountryCodeSelected = onCountryCodeSelected
            )
        }
        Text(
            text = "We'll send a verification code to your phone number to confirm your account.",
            color = PrimaryText,
            style = MaterialTheme.typography.bodySmall
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
fun PhoneNumberField(
    phoneNumber: String,
    countryCodes: List<CountryCode>,
    selectedCountryCode: CountryCode?,
    onPhoneNumberChanged: (String) -> Unit,
    onCountryCodeSelected: (CountryCode) -> Unit
) {

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {Text(text = "9539094454")},
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        leadingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = "", tint = PrimaryText)
                HorizontalSpacerSmall()
                CountryCodeSelected(
                    selected = selectedCountryCode,
                    countryCodes = countryCodes,
                    onCountryCodeSelected = onCountryCodeSelected
                )
                HorizontalSpacerSmall()
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )

            }
        },
        colors = signUpFieldColors()
    )
}

@Composable
fun CountryCodeSelected(
    selected: CountryCode?,
    countryCodes: List<CountryCode>,
    onCountryCodeSelected: (CountryCode) -> Unit
) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    Box{
        Row(modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .clickable(enabled = countryCodes.isNotEmpty()) { expanded=true }
            .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically){
            Text(text = selected?.flagEmoji ?: "🏳", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = selected?.dialCode ?: "+--",
                color = TextColor,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Choose country code",
                tint = PrimaryText,
                modifier = Modifier.size(18.dp)
            )

        }
        DropdownMenu(expanded =expanded, onDismissRequest = {expanded=false}) {
            countryCodes.forEach { code->
                DropdownMenuItem(
                    text = {Text(text="${code.flagEmoji}  ${code.countryName}   ${code.dialCode}")},
                    onClick = {
                        onCountryCodeSelected(code)
                        expanded=false
                    }
                )
            }
        }

    }
}


@Composable
fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = placeholder) },
        leadingIcon = leadingIcon,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = signUpFieldColors()
    )
}

@Composable
private fun signUpFieldColors() = OutlinedTextFieldDefaults.colors(
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

@Composable
fun LabelField(label: String, content:@Composable ()->Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label,
            color = PrimaryText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold)
        content()
    }
}

@Composable
fun SignUpHeadLine() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = stringResource(com.example.trekkly.R.string.create_account).uppercase(getDefault()),
            color = PrimaryText.copy(alpha = 0.60f),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold)

        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = TextColor)){append(stringResource(com.example.trekkly.R.string.join_the))}
            withStyle(SpanStyle(color = SecondaryColor)) { append("adventure.") }
        },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp)

    }
}

@Composable
fun BackButton(onBackClick: () -> Unit, modifier: Modifier) {
    IconButton(
        onClick = onBackClick,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f)),
    ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "", tint = TextColor)
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview(){
    SignUpContent(
        uiState = SignUpState(),
        onBackClick = {},
        onLoginClick = {},
        onFullNameChanged = {},
        onPhoneNumberChanged = {},
        onCountryCodeSelected = {},
        onSignUpClick = {}
    )
}