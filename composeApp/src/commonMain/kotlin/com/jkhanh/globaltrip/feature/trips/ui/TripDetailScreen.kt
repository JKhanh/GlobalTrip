package com.jkhanh.globaltrip.feature.trips.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.feature.trips.presentation.TripDetailViewModel
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject

/**
 * Trip detail screen showing comprehensive trip information
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TripDetailScreen(
    tripId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripDetailViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("Trip Details") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (uiState.trip != null) {
                    IconButton(onClick = { onNavigateToEdit(tripId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Trip")
                    }
                }
            },
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 0.dp
        )
        
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadTrip(tripId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            uiState.trip != null -> {
                TripDetailContent(
                    trip = uiState.trip!!,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun TripDetailContent(
    trip: Trip,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (trip.coverImageUrl != null) {
                // TODO: Implement image loading
                Text(
                    "Trip Image",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary
                )
            } else {
                Text(
                    "No Image",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Trip Title
        Text(
            text = trip.title,
            style = MaterialTheme.typography.h4.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colors.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Location
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = trip.destination,
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colors.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dates
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatDateRange(trip.startDate, trip.endDate),
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colors.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Description Section
        if (trip.description.isNotBlank()) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colors.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = trip.description,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Trip Details Section
        Text(
            text = "Trip Details",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colors.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                DetailRow(
                    label = "Owner",
                    value = trip.ownerId.substringBefore("@")
                )
                
                if (trip.collaboratorIds.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(
                        label = "Collaborators",
                        value = "${trip.collaboratorIds.size} people"
                    )
                }
                
                if (trip.isArchived) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "This trip is archived",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
        
        // Add bottom padding for better scrolling experience
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Format a date range as a string
 */
private fun formatDateRange(startDate: LocalDate?, endDate: LocalDate?): String {
    if (startDate == null && endDate == null) {
        return "Dates not set"
    }
    
    return when {
        startDate != null && endDate != null -> "${formatDate(startDate)} - ${formatDate(endDate)}"
        startDate != null -> "From ${formatDate(startDate)}"
        endDate != null -> "Until ${formatDate(endDate)}"
        else -> "Dates not set"
    }
}

/**
 * Format a date as a string (e.g., "Mar 9, 2023")
 */
private fun formatDate(date: LocalDate): String {
    val month = date.month.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .take(3)
    
    return "$month ${date.dayOfMonth}, ${date.year}"
}

