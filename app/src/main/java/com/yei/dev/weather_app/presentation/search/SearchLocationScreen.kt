package com.yei.dev.weather_app.presentation.search

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yei.dev.weather_app.R
import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.presentation.components.ProgressCircleScreen
import com.yei.dev.weather_app.presentation.components.ShieldHandlerScreen
import com.yei.dev.weather_app.presentation.topBar.WeatherTopBar
import com.yei.dev.weather_app.ui.theme.Purple40
import com.yei.dev.weather_app.ui.theme.Purple700
import com.yei.dev.weather_app.ui.theme.Purple80

@Composable
fun WeatherSearchScreen(
    viewModel: WeatherSearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onLocationSelected: (Location) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple700)
    ) {
        WeatherTopBar(
            title = "Search Location",
            onBackClick = onBackClick
        )

        SearchBar(
            query = state.searchQuery,
            onQueryChange = { viewModel.onEvent(SearchEvent.OnSearchQueryChanged(it)) },
            onClear = { viewModel.onEvent(SearchEvent.OnClearSearch) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val uiState = state.uiState) {
            is SearchUiState.Idle -> {
                IdleState()
            }

            is SearchUiState.Loading -> {
                LoadingState()
            }

            is SearchUiState.Success -> {
                SuccessState(
                    locations = uiState.locations,
                    onLocationClick = { location ->
                        viewModel.onEvent(SearchEvent.OnLocationSelected(location))
                        onLocationSelected(location)
                    }
                )
            }

            is SearchUiState.Empty -> {
                EmptyState(query = uiState.query)
            }

            is SearchUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = { viewModel.onEvent(SearchEvent.OnRetry) }
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.search_hint),
                color = Purple700
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Purple700
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Purple700
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Purple80,
            unfocusedContainerColor = Purple40,
            focusedTextColor = Purple700,
            unfocusedTextColor = Purple700,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun IdleState() {
    ShieldHandlerScreen(
        icon = Icons.Default.Search,
        title = stringResource(R.string.title_search_for_a_location),
        description = stringResource(R.string.description_search_for_a_location),
    ) { }
}

@Composable
private fun LoadingState() {
    ProgressCircleScreen(
        description = stringResource(R.string.description_circle_bar_searching)
    )
}

@Composable
private fun SuccessState(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(locations) { location ->
            LocationItem(
                location = location,
                onClick = { onLocationClick(location) }
            )
        }
    }
}

@Composable
private fun LocationItem(
    location: Location,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Purple40
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = location.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildString {
                        if (location.region.isNotEmpty()) {
                            append(location.region)
                            append(", ")
                        }
                        append(location.country)
                    },
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyState(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        ShieldHandlerScreen(
            icon = Icons.Default.Info,
            title = stringResource(R.string.no_results_message),
            description = stringResource(R.string.description_circle_bar_no_results, query),
        ) { }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    ShieldHandlerScreen(
        icon = Icons.Default.Dangerous,
        title = stringResource(R.string.error_message_unknown),
        description = message,
        textButton = stringResource(R.string.text_button_retry)
    ) {
        onRetry()
    }
}
