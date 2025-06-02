package com.jkhanh.globaltrip.feature.trips.ui.create

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.MaterialTheme
import com.jkhanh.globaltrip.core.ui.components.GTTextField
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripBlue
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripBlack
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripWhite
import com.jkhanh.globaltrip.core.ui.theme.SerenePalette
import com.jkhanh.globaltrip.feature.trips.presentation.TripCreateViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import network.chaintech.kmp_date_time_picker.ui.date_range_picker.WheelDateRangePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.MAX
import network.chaintech.kmp_date_time_picker.utils.MIN
import org.koin.compose.koinInject

/**
 * Screen for creating a new trip based on the provided HTML design
 */
@Composable
fun TripCreateScreen(
    viewModel: TripCreateViewModel = koinInject(),
    onNavigateBack: () -> Unit,
    onTripCreated: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    // Handle successful trip creation
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess && state.tripId != null) {
            onTripCreated(state.tripId!!)
            viewModel.resetSuccessState()
        }
    }

    val currentDate = remember {
        val now = kotlinx.datetime.Clock.System.now()
        val localDateTime = now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        LocalDate(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth)
    }

// Use current date for both start and end dates initially
    val startDate = remember { currentDate }
    val endDate = remember { currentDate }
    
    // Initialize viewModel with default values if not already set
    LaunchedEffect(Unit) {
        if (state.startDate == null) {
            viewModel.updateStartDate(startDate)
        }
        if (state.endDate == null) {
            viewModel.updateEndDate(endDate)
        }
    }
    
    Scaffold(
        backgroundColor = MaterialTheme.colors.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colors.surface)
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                    
                    Text(
                        text = "New trip",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.015).sp,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable { 
                                viewModel.createTrip() 
                            }
                        )
                    }
                }
                
                // Title field
                Text(
                    text = "Title",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                )
                
                GTTextField(
                    value = state.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                
                // Description field
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                )
                
                GTTextField(
                    value = state.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    placeholder = "Add a description",
                    singleLine = false,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(144.dp)
                        .padding(horizontal = 16.dp)
                )
                
                // Dates Section
                Text(
                    text = "Dates",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.015).sp,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                )
                
                // Date selection
                var showDatePicker by remember { mutableStateOf(false) }
                
                // Display selected dates in a Row
                val dateFormat = remember {
                    { date: LocalDate? ->
                        date?.let {
                            "${it.dayOfMonth} ${it.month.name.take(3)} ${it.year}"
                        } ?: "Select date"
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = dateFormat(state.startDate),
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    
                    Text(
                        text = "to",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onBackground
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = dateFormat(state.endDate),
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
                
                // Date picker
                if (showDatePicker) {
                    WheelDateRangePickerView(
                        showDatePicker = true,
                        title = "Select Trip Dates",
                        doneLabel = "Done",
                        initialFromDate = state.startDate,
                        initialToDate = state.endDate,
                        selectFutureDate = true,
                        selectPastDate = true,
                        height = 200.dp,
                        rowCount = 5,
                        showShortMonths = true,
                        dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
                        containerColor = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(16.dp),
                        dateRangeBoxColor = MaterialTheme.colors.surface,
                        dateRangeSelectedBoxColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        dateRangeBoxBorderColor = MaterialTheme.colors.primary.copy(alpha = 0.2f),
                        dateRangeSelectedBoxBorderColor = MaterialTheme.colors.primary,
                        dateRangeBoxShape = RoundedCornerShape(8.dp),
                        onFromDateChangeListener = { fromDate ->
                            viewModel.updateStartDate(fromDate)
                        },
                        onToDateChangeListener = { toDate ->
                            viewModel.updateEndDate(toDate)
                        },
                        onDoneClick = { fromDate, toDate ->
                            viewModel.updateStartDate(fromDate)
                            viewModel.updateEndDate(toDate)
                            showDatePicker = false
                        },
                        onDismiss = {
                            showDatePicker = false
                        }
                    )
                }
                
                // Destination Section
                Text(
                    text = "Destination",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.015).sp,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                )
                
                // Location field
                Text(
                    text = "Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                )
                
                GTTextField(
                    value = state.destination,
                    onValueChange = { viewModel.updateDestination(it) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                
                // Cover Photo Section
                Text(
                    text = "Cover photo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.015).sp,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                )
                
                // Image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .aspectRatio(3f / 2f)
                        .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // This is a placeholder, you would typically load an image here
                    Text(
                        text = "Select cover image",
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                // Archive switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Archive this trip",
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                    
                    Switch(
                        checked = state.isArchived,
                        onCheckedChange = { viewModel.updateArchiveStatus(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colors.onPrimary,
                            checkedTrackColor = MaterialTheme.colors.primary,
                            uncheckedThumbColor = MaterialTheme.colors.onSurface,
                            uncheckedTrackColor = MaterialTheme.colors.surface
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Loading indicator
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}