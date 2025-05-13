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
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripBlue
import com.jkhanh.globaltrip.feature.trips.presentation.TripCreateViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import network.chaintech.kmp_date_time_picker.ui.date_range_picker.WheelDateRangePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
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
        backgroundColor = Color(0xFFF5F7FA)
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
                            .background(Color(0xFFE7EFF3))
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0D161B)
                        )
                    }
                    
                    Text(
                        text = "New trip",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.015).sp,
                        color = Color(0xFF0D161B),
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
                            color = Color(0xFF4C7D9A),
                            modifier = Modifier.clickable { 
                                viewModel.createTrip() 
                            }
                        )
                    }
                }
                
                // Title field
                FormField(
                    label = "Title",
                    value = state.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = ""
                )
                
                // Description field
                FormField(
                    label = "Description",
                    value = state.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    placeholder = "Add a description",
                    singleLine = false,
                    maxLines = 5
                )
                
                // Dates Section
                Text(
                    text = "Dates",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.015).sp,
                    color = Color(0xFF0D161B),
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
                            .background(color = Color(0xFFE7EFF3), shape = RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = dateFormat(state.startDate),
                            fontSize = 16.sp,
                            color = Color(0xFF0D161B)
                        )
                    }
                    
                    Text(
                        text = "to",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D161B)
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(color = Color(0xFFE7EFF3), shape = RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = dateFormat(state.endDate),
                            fontSize = 16.sp,
                            color = Color(0xFF0D161B)
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
                        minDate = LocalDate(2000, 1, 1),
                        maxDate = LocalDate(2050, 12, 31),
                        height = 200.dp,
                        rowCount = 5,
                        showShortMonths = true,
                        dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
                        containerColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        dateRangeBoxColor = Color(0xFFE7EFF3),
                        dateRangeSelectedBoxColor = Color(0xFF139CEC).copy(alpha = 0.1f),
                        dateRangeBoxBorderColor = Color(0xFFE7EFF3),
                        dateRangeSelectedBoxBorderColor = Color(0xFF139CEC),
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
                    color = Color(0xFF0D161B),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                )
                
                // Location field
                FormField(
                    label = "Location",
                    value = state.destination,
                    onValueChange = { viewModel.updateDestination(it) },
                    placeholder = ""
                )
                
                // Cover Photo Section
                Text(
                    text = "Cover photo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.015).sp,
                    color = Color(0xFF0D161B),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                )
                
                // Image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .aspectRatio(3f / 2f)
                        .background(color = Color(0xFFE7EFF3), shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // This is a placeholder, you would typically load an image here
                    Text(
                        text = "Select cover image",
                        color = Color(0xFF4C7D9A),
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
                        color = Color(0xFF0D161B)
                    )
                    
                    Switch(
                        checked = state.isArchived,
                        onCheckedChange = { viewModel.updateArchiveStatus(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF139CEC),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE7EFF3)
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

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0D161B),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (singleLine) {
            androidx.compose.material.TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    if (placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFF4C7D9A)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFE7EFF3),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = GlobalTripBlue,
                    textColor = Color(0xFF0D161B)
                ),
                singleLine = true
            )
        } else {
            androidx.compose.material.TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    if (placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFF4C7D9A)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFE7EFF3),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = GlobalTripBlue,
                    textColor = Color(0xFF0D161B)
                ),
                maxLines = maxLines
            )
        }
    }
}


