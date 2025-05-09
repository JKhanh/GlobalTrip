package com.jkhanh.globaltrip.feature.trips.ui.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jkhanh.globaltrip.core.ui.components.GTButton
import com.jkhanh.globaltrip.core.ui.components.GTTextField
import com.jkhanh.globaltrip.feature.trips.presentation.TripCreateViewModel

/**
 * Screen for creating a new trip
 */
@Composable
fun TripCreateScreen(
    viewModel: TripCreateViewModel,
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Trip") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Trip title field
                GTTextField(
                    value = state.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = "Trip Title",
                    placeholder = "Enter trip title"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Destination field
                GTTextField(
                    value = state.destination,
                    onValueChange = { viewModel.updateDestination(it) },
                    label = "Destination",
                    placeholder = "Enter destination"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date selectors (platform-specific implementation needed)
                Text(
                    text = "Date Selection will be implemented with platform-specific pickers",
                    style = MaterialTheme.typography.caption
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description field
                GTTextField(
                    value = state.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = "Description",
                    placeholder = "Enter trip description",
                    singleLine = false,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Create button
                GTButton(
                    text = "Create Trip",
                    onClick = { viewModel.createTrip() },
                    enabled = state.isValid && !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption
                    )
                }
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
