package com.example.trekkly.presentation.trekks.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.trekkly.R
import com.example.trekkly.domain.model.DifficultyLevel
import com.example.trekkly.domain.model.Trek
import com.example.trekkly.presentation.theme.DifficultyDifficult
import com.example.trekkly.presentation.theme.DifficultyEasy
import com.example.trekkly.presentation.theme.DifficultyExpert
import com.example.trekkly.presentation.theme.DifficultyModerate
import com.example.trekkly.presentation.trekks.event.DifficultyFilter
import com.example.trekkly.presentation.trekks.event.TrekksUiState
import com.example.trekkly.presentation.trekks.viewmodel.TrekksViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TrekksScreen(
    viewModel: TrekksViewModel = hiltViewModel(),
    onTrekClick:(String) -> Unit ={},
    onFilterClick:()-> Unit={}
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TrekksContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onClearQuery = viewModel::onClearQuery,
        onDifficultySelected = viewModel::onDifficultySelected,
        onTrekClick = onTrekClick,
        onFilterClick = onFilterClick
    )
}

@Composable
private fun TrekksContent(
    uiState: TrekksUiState,
    onQueryChange: (String)-> Unit,
    onClearQuery: ()-> Unit,
    onDifficultySelected: (DifficultyFilter)-> Unit,
    onTrekClick: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(onFilterClick = onFilterClick)
        SearchField(
            query = uiState.query,
            onQueryChange = onQueryChange,
            onClearQuery = onClearQuery,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(14.dp))
        DifficultyChipRow(
            selected = uiState.selectedDifficulty,
            onSelected = onDifficultySelected
        )
        Spacer(Modifier.height(14.dp))
        Box(modifier = Modifier.fillMaxSize()){
            when{
                uiState.isLoading-> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.isEmptyResult->{
                    EmptyState(
                        hasActiveFilters = uiState.hasActiveFilters,
                        error = uiState.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else->{
                    TrekGrid(treks = uiState.treks,onTrekClick = onTrekClick)
                }
            }
        }
    }

}

@Composable
private fun TrekGrid(treks: List<Trek>, onTrekClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(treks, key = {it.id}){
            trek->
            TrekCard(trek=trek,onClick={onTrekClick(trek.id)})
        }
    }
}

@Composable
private fun TrekCard(trek: Trek, onClick: () -> Unit) {
   Surface(
       onClick = onClick,
       shape = RoundedCornerShape(14.dp),
       color = MaterialTheme.colorScheme.surfaceVariant,
       modifier = Modifier.fillMaxWidth()
   ) {
       Column() {
           Box(
               modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)
           ){
               AsyncImage(
                   model = trek.coverImage,
                   contentDescription = trek.title,
                   contentScale = ContentScale.Crop,
                   placeholder = painterResource(R.drawable.mountain_trekk),
                   error = painterResource(R.drawable.mountain_trekk),
                   fallback = painterResource(R.drawable.mountain_trekk),
                   modifier = Modifier.fillMaxSize()
               )
               if (trek.rating>0.0){
                   RatingBadge(
                       rating = trek.rating,
                       modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                   )
               }
               DifficultyBadge(
                   level = trek.difficultyLevel,
                   modifier = Modifier
                       .align(Alignment.BottomStart)
                       .padding(8.dp)
               )
           }

           Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
               Text(
                   text = trek.title,
                   style = MaterialTheme.typography.titleSmall,
                   fontWeight = FontWeight.Bold,
                   color = MaterialTheme.colorScheme.onSurface,
                   maxLines = 1,
                   overflow = TextOverflow.Ellipsis
               )
               Spacer(Modifier.height(3.dp))
               Row(verticalAlignment = Alignment.CenterVertically) {
                   Icon(
                       imageVector = Icons.Outlined.LocationOn,
                       contentDescription = null,
                       tint = MaterialTheme.colorScheme.onSurfaceVariant,
                       modifier = Modifier.size(12.dp)
                   )
                   Spacer(Modifier.size(3.dp))
                   Text(
                       text = trek.location,
                       style = MaterialTheme.typography.bodySmall,
                       color = MaterialTheme.colorScheme.onSurfaceVariant,
                       maxLines = 1,
                       overflow = TextOverflow.Ellipsis
                   )
               }
               Spacer(Modifier.height(6.dp))
               Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically
               ) {
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Icon(
                           imageVector = Icons.Outlined.Schedule,
                           contentDescription = null,
                           tint = MaterialTheme.colorScheme.onSurfaceVariant,
                           modifier = Modifier.size(12.dp)
                       )
                       Spacer(Modifier.size(3.dp))
                       Text(
                           text = "${trek.days}d",
                           style = MaterialTheme.typography.labelSmall,
                           color = MaterialTheme.colorScheme.onSurfaceVariant
                       )
                   }
                   Text(
                       text = "${formatAltitude(trek.altitude)}m",
                       style = MaterialTheme.typography.labelSmall,
                       color = MaterialTheme.colorScheme.onSurfaceVariant
                   )
               }
           }
       }
   }
}

@Composable
private fun RatingBadge(rating: Double, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.55f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(10.dp)
            )
            Spacer(Modifier.size(3.dp))
            Text(
                text = formatRating(rating),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DifficultyBadge(level: DifficultyLevel, modifier: Modifier = Modifier) {
    val accent = level.accentColor()
    // The two hard buckets get a solid pill so severity reads at a glance;
    // the gentler ones sit quietly on a scrim over the photo.
    val emphasised = level == DifficultyLevel.DIFFICULT || level == DifficultyLevel.EXPERT

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = if (emphasised) accent else Color.Black.copy(alpha = 0.45f)
    ) {
        Text(
            text = level.label(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = if (emphasised) MaterialTheme.colorScheme.onPrimary else accent,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

private fun DifficultyLevel.label(): String = when (this) {
    DifficultyLevel.EASY -> "Easy"
    DifficultyLevel.MODERATE -> "Moderate"
    DifficultyLevel.DIFFICULT -> "Hard"
    DifficultyLevel.EXPERT -> "Expert"
}

private fun DifficultyLevel.accentColor(): Color = when (this) {
    DifficultyLevel.EASY -> DifficultyEasy
    DifficultyLevel.MODERATE -> DifficultyModerate
    DifficultyLevel.DIFFICULT -> DifficultyDifficult
    DifficultyLevel.EXPERT -> DifficultyExpert
}

private fun formatAltitude(metres: Int): String =
    NumberFormat.getIntegerInstance(Locale.US).format(metres)

private fun formatRating(rating: Double): String =
    if (rating % 1.0 == 0.0) rating.toInt().toString()
    else String.format(Locale.US, "%.1f", rating)

@Composable
private fun EmptyState(
    hasActiveFilters: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    // An empty grid has three very different causes, and silently showing the same
    // sentence for all of them makes a sync failure look like an empty catalogue.
    val message = when {
        error != null -> "Couldn't load treks: $error"
        hasActiveFilters -> "No treks match your search."
        else -> "No treks available yet."
    }
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = if (error != null) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(horizontal = 32.dp)
    )
}
@Composable
private fun DifficultyChipRow(selected: DifficultyFilter, onSelected: (DifficultyFilter) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ){
        items(DifficultyFilter.entries){filter->
            val isSelected = filter == selected
            Surface(onClick = {onSelected(filter)},
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            {
                Text(
                    text = filter.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                )
            }
        }
    }
}


@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically)
        {
            Icon(imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp))

            Spacer(Modifier.size(10.dp))
            Box(modifier= Modifier.weight(1f).height(48.dp), contentAlignment = Alignment.CenterStart){
                if (query.isEmpty()){
                    Text(
                        text = "Search treks or destinations...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onClearQuery()
                            focusManager.clearFocus()
                        }
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ScreenHeader(onFilterClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "All Treks",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Surface(
            onClick = onFilterClick,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(imageVector = Icons.Outlined.Tune,
                contentDescription = "Filter Treks",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp).padding(9.dp))
        }
    }
}

