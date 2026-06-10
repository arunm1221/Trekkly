package com.example.trekkly.common.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpacer(){
    Spacer(modifier = Modifier.height(20.dp))
}@Composable
fun HorizontalSpacerSmall(){
    Spacer(modifier = Modifier.width(10.dp))
}

@Composable
fun VerticalSpacerSmall(){
    Spacer(modifier = Modifier.height(8.dp))
}@Composable
fun VerticalSpacerLarge(){
    Spacer(modifier = Modifier.height(36.dp))
}

