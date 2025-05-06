package com.jkhanh.globaltrip.feature.trips.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.ui.components.GTCard
import kotlinx.datetime.LocalDate

/**
 * Card component to display a trip summary
 */
@Composable
fun TripCard(
    trip: Trip,
    onClick: (Trip) -> Unit,
    modifier: Modifier = Modifier
) {
    GTCard(
        modifier = modifier.clickable { onClick(trip) },
        elevation = 2.dp
    ) {
        Column {
            // Trip cover image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.2f))
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Trip title
            Text(
                text = trip.title,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Trip dates
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colors.primary
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = formatDateRange(trip.startDate, trip.endDate),
                    style = MaterialTheme.typography.body2
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Trip location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colors.primary
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = trip.destination,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Format a date range as a string
 */
private fun formatDateRange(startDate: LocalDate?, endDate: LocalDate?): String {
    if (startDate == null || endDate == null) {
        return "Unknown dates"
    }
    return "${formatDate(startDate)} - ${formatDate(endDate)}"
}

/**
 * Format a date as a string
 */
private fun formatDate(date: LocalDate): String {
    return "${date.month.name.lowercase().capitalize().take(3)} ${date.dayOfMonth}, ${date.year}"
}
