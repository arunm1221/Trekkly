package com.example.trekkly.presentation.home.ui

import android.R
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.trekkly.domain.model.DifficultyLevel
import com.example.trekkly.domain.model.UserTrek
import com.example.trekkly.presentation.home.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel= hiltViewModel(),
    onTrekkClick:(String) -> Unit={},
    onViewAllClick:()-> Unit={}
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Spacer(Modifier.height(8.dp)) }

            item {
                Text(
                    text = "Good Morning,",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium

                )
                Text(
                    text = "Trekker",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            uiState.activeExpedition?.let {
                expedition->
                item {
                    SectionHeader(title = "Current Expedition", action ="Live", onAction =null)
                    Spacer(Modifier.height(8.dp))
                    ExpeditionCard(expedition = expedition,onClick = onTrekkClick(expedition.trek.id))
                }
            }

            if (uiState.upcoming.isNotEmpty()){
                item {
                    SectionHeader(title = "Upcoming Treks", action ="View All", onAction =onViewAllClick)
                }
                items(uiState.upcoming, key = {it.trek.id}){ userTrek->
                    UpcomingTrekItem(userTrek = userTrek,onClick = {onTrekkClick({userTrek.trek.id}.toString()) })

                }
            }
            if (!uiState.isLoading && uiState.activeExpedition==null && uiState.upcoming.isEmpty()){
                item {
                    Text(
                        text = "No Treks Scheduled yet, Schedule from Trek Tab.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }

        }
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun UpcomingTrekItem(userTrek: UserTrek, onClick: () -> Unit) {
    val trek = userTrek.trek
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = trek.coverImage,
                contentDescription = trek.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = trek.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = trek.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${trek.days} days", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.size(8.dp))
                    DifficultyChip(trek.difficultyLevel)
                }
            }
            Text(
                text = formatDate(userTrek.scheduleDate),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DifficultyChip(level: DifficultyLevel) {
    val (label, color) = when (level) {
        DifficultyLevel.EASY -> "Easy" to Color(0xFF2E7D32)
        DifficultyLevel.MODERATE -> "Moderate" to Color(0xFF2E7D32)
        DifficultyLevel.DIFFICULT -> "Hard" to Color(0xFFC62828)
        DifficultyLevel.EXPERT -> "Expert" to Color(0xFFC62828)
    }
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun formatDate(millis: Long?): String {
    if (millis == null) return ""
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))
}

@Composable
fun ExpeditionCard(expedition: UserTrek, onClick: Unit) {
    val trek = expedition.trek
    Card(
        onClick = { onClick },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
            .height(180.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()){

            AsyncImage(
                model = trek.coverImage,
                contentDescription = trek.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)),
                )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = trek.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.White.copy(0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = trek.location,
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = expedition.progress,
                    modifier = Modifier.fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${trek.altitude} m",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Day ${expedition.currentDay}/${trek.days}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String?, onAction: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        if (action!= null){
            Text(
                text = action,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = if (onAction!=null) Modifier.clickable(onClick = onAction)else Modifier
            )
        }
    }
}



