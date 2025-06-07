package com.jkhanh.globaltrip.feature.trips.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.ui.components.GTButton
import com.jkhanh.globaltrip.feature.trips.presentation.TripListViewModel
import org.koin.compose.koinInject

/**
 * Screen to display the list of trips according to Figma design
 */
@Composable
fun TripListScreen(
    onTripClick: (Trip) -> Unit,
    onCreateTripClick: () -> Unit,
    viewModel: TripListViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            if (state.isSearchActive) {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            placeholder = { Text("Search trips by name or destination...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = viewModel::clearSearch) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = 0.dp
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = "Trips",
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = MaterialTheme.colors.onBackground
                        )
                    },
                    actions = {
                        IconButton(onClick = viewModel::toggleSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = 0.dp
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTripClick,
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier.size(64.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Trip",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colors.primary
                )
            } else if (state.error != null) {
                Text(
                    text = state.error ?: "Unknown error",
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            } else if (!state.isSearchActive && state.allTrips.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "No trips found",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Create your first trip to get started",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    GTButton(
                        text = "Create Trip",
                        onClick = onCreateTripClick
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else if (state.isSearchActive) {
                // Search results
                val tripsToShow = if (state.searchQuery.isBlank()) state.allTrips else state.filteredTrips
                
                if (tripsToShow.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (state.searchQuery.isBlank()) "No trips available" else "No trips found",
                                style = MaterialTheme.typography.h6.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colors.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (state.searchQuery.isBlank()) "Create your first trip to get started" else "Try a different search term",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = if (state.searchQuery.isBlank()) "All Trips (${tripsToShow.size})" else "Search Results (${tripsToShow.size})",
                                style = MaterialTheme.typography.h6.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = MaterialTheme.colors.onBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.background)
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        
                        items(tripsToShow) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = onTripClick
                            )
                        }
                        
                        // Add bottom space for FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Upcoming section
                    if (state.upcomingTrips.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Upcoming")
                        }
                        
                        items(state.upcomingTrips) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = onTripClick
                            )
                        }
                    }
                    
                    // Saved trips section
                    if (state.archivedTrips.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Saved Trips")
                        }
                        
                        items(state.archivedTrips) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = onTripClick
                            )
                        }
                    }
                    
                    // Past trips section (if needed)
                    if (state.pastTrips.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Past Trips")
                        }
                        
                        items(state.pastTrips) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = onTripClick
                            )
                        }
                    }
                    
                    // Add bottom space for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.h6.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}
