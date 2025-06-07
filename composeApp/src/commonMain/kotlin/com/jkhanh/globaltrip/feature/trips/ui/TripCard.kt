package com.jkhanh.globaltrip.feature.trips.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jkhanh.globaltrip.core.domain.model.Trip
import kotlinx.datetime.LocalDate

/**
 * Card component to display a trip summary based on Figma design
 */
@Composable
fun TripCard(
    trip: Trip,
    onClick: (Trip) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .clickable { onClick(trip) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trip image placeholder
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.primary.copy(alpha = 0.15f)) // Use theme color with reduced opacity
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Trip destination as title
            Text(
                text = trip.destination,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Trip dates
            Text(
                text = formatDateRange(trip.startDate, trip.endDate),
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Host info - Using owner ID for now
            Text(
                text = "Hosted by ${trip.ownerId.substringBefore("@")}",
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }
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
 * Format a date as a string per Figma design (e.g., "Mar 9, 2023")
 */
private fun formatDate(date: LocalDate): String {
    val month = date.month.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .take(3)
    
    return "$month ${date.dayOfMonth}, ${date.year}"
}
