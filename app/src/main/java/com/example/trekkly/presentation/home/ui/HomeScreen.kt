package com.example.trekkly.presentation.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.trekkly.domain.model.Trekk
import com.example.trekkly.presentation.home.event.HomeEvent
import com.example.trekkly.presentation.home.viewmodel.HomeViewModel
import com.example.trekkly.presentation.theme.PrimaryColor
import com.example.trekkly.presentation.theme.PrimaryText
import com.example.trekkly.presentation.theme.SecondaryColor
import com.example.trekkly.presentation.theme.TextColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CardColor = Color(0xFF24382E)        // slightly lifted surface
private val FieldColor = Color(0xFF2A3D33)       // search field / chips
private val HardColor = Color(0xFFC75C4A)

@Composable
fun HomeScreen(
    userName: String = "Trekker",
    onTrekkClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.NavigateToTrekkDetail -> onTrekkClick(event.trekkId)
            }
        }
    }

    Scaffold(
        containerColor = PrimaryColor,
        bottomBar = { BottomNavBar() }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SearchHeader(onSearchClick) }
            item { Greeting(userName) }

            uiState.activeTrekk?.let { active ->
                item { SectionHeader(title = "Current Expedition", trailing = "Live") }
                item {
                    CurrentExpeditionCard(
                        trekk = active,
                        onClick = { viewModel.onTrekkClicked(active.id) }
                    )
                }
            }

            if (uiState.upcomingTrekks.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Upcoming Trekks",
                        trailing = "View all",
                        trailingArrow = true,
                        onTrailingClick = onViewAllClick
                    )
                }
                items(uiState.upcomingTrekks, key = { it.id }) { trekk ->
                    UpcomingTrekkCard(trekk = trekk, onClick = { viewModel.onTrekkClicked(trekk.id) })
                }
            }
        }
    }
}

@Composable
private fun SearchHeader(onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(FieldColor)
                .clickable(onClick = onSearchClick)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = PrimaryText, modifier = Modifier.size(20.dp))
            Text("Search trekks, places…", color = PrimaryText, fontSize = 15.sp)
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(FieldColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = TextColor, modifier = Modifier.size(22.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SecondaryColor)
            )
        }
    }
}

@Composable
private fun Greeting(userName: String) {
    Column {
        Text("Good morning,", color = PrimaryText, fontSize = 15.sp)
        Text(
            "$userName 🏔️",
            color = TextColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    trailing: String? = null,
    trailingArrow: Boolean = false,
    onTrailingClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextColor, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        if (trailing != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onTrailingClick)
            ) {
                Text(trailing, color = SecondaryColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                if (trailingArrow) {
                    Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = SecondaryColor, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun CurrentExpeditionCard(trekk: Trekk, onClick: () -> Unit) {
    val (currentDay, totalDays) = remember(trekk) { trekDayProgress(trekk) }
    val progress = if (totalDays > 0) currentDay.toFloat() / totalDays else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.7f)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = trekk.coverImageUrl,
            contentDescription = trekk.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // bottom-up dark gradient for legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        1f to Color(0xCC10201A)
                    )
                )
        )
        // Live pill
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SecondaryColor)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("Live", color = PrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        // Day x/y
        Text(
            "Day $currentDay/$totalDays",
            color = TextColor,
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        )
        // Title block
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(trekk.title, color = TextColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = PrimaryText, modifier = Modifier.size(15.dp))
                Text(trekk.location, color = PrimaryText, fontSize = 13.sp)
            }
            // progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(Color(0x55FFFFFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(SecondaryColor)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.Terrain, contentDescription = null, tint = TextColor, modifier = Modifier.size(16.dp))
                Text("${formatMeters(trekk.maxAltitudeMeters)}m", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun UpcomingTrekkCard(trekk: Trekk, onClick: () -> Unit) {
    val dateFmt = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = trekk.coverImageUrl,
            contentDescription = trekk.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(trekk.title, color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = PrimaryText, modifier = Modifier.size(13.dp))
                Text(trekk.location, color = PrimaryText, fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = PrimaryText, modifier = Modifier.size(13.dp))
                    Text("${trekk.durationDays} days", color = PrimaryText, fontSize = 12.sp)
                }
                DifficultyChip(trekk.difficulty)
            }
        }
        Text(
            dateFmt.format(Date(trekk.startDate)),
            color = PrimaryText,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Bottom)
        )
    }
}

@Composable
private fun DifficultyChip(difficulty: String) {
    val isHard = difficulty.equals("hard", ignoreCase = true) || difficulty.equals("extreme", ignoreCase = true)
    val bg = if (isHard) HardColor.copy(alpha = 0.22f) else SecondaryColor.copy(alpha = 0.18f)
    val fg = if (isHard) HardColor else SecondaryColor
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            difficulty.replaceFirstChar { it.uppercase() },
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BottomNavBar() {
    val items = listOf(
        Triple("Home", Icons.Filled.Home, true),
        Triple("Trekks", Icons.Filled.Explore, false),
        Triple("Map", Icons.Filled.Map, false),
        Triple("Settings", Icons.Filled.Settings, false)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryColor)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { (label, icon, selected) ->
            val tint = if (selected) SecondaryColor else PrimaryText
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(2.dp)
                            .clip(CircleShape)
                            .background(SecondaryColor)
                    )
                } else {
                    Spacer(Modifier.height(2.dp))
                }
                Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
                Text(label, color = tint, fontSize = 11.sp)
            }
        }
    }
}

/** current day (1-based) and total days for an active trek. */
private fun trekDayProgress(trekk: Trekk): Pair<Int, Int> {
    val total = trekk.durationDays.coerceAtLeast(1)
    val elapsed = ((System.currentTimeMillis() - trekk.startDate) / 86_400_000L).toInt() + 1
    return elapsed.coerceIn(1, total) to total
}

private fun formatMeters(m: Int): String =
    if (m >= 1000) "%,d".format(m) else m.toString()
