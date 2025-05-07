package com.jkhanh.globaltrip.core.ui.components.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jkhanh.globaltrip.core.ui.theme.*

@Composable
fun ThemeSelector(
    currentTheme: GlobalTripThemeOption,
    onThemeSelected: (GlobalTripThemeOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Theme",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ThemeOption(
                name = "Default",
                colors = listOf(GlobalTripBlue, GlobalTripOrange, GlobalTripGreen),
                isSelected = currentTheme == GlobalTripThemeOption.DEFAULT,
                onClick = { onThemeSelected(GlobalTripThemeOption.DEFAULT) }
            )
            
            ThemeOption(
                name = "Adventure",
                colors = listOf(
                    AdventurePalette.primaryLight,
                    AdventurePalette.secondaryLight,
                    AdventurePalette.tertiaryLight
                ),
                isSelected = currentTheme == GlobalTripThemeOption.ADVENTURE,
                onClick = { onThemeSelected(GlobalTripThemeOption.ADVENTURE) }
            )
            
            ThemeOption(
                name = "Serene",
                colors = listOf(
                    SerenePalette.primaryLight,
                    SerenePalette.secondaryLight,
                    SerenePalette.tertiaryLight
                ),
                isSelected = currentTheme == GlobalTripThemeOption.SERENE,
                onClick = { onThemeSelected(GlobalTripThemeOption.SERENE) }
            )
            
            ThemeOption(
                name = "Vibrant",
                colors = listOf(
                    VibrantPalette.primaryLight,
                    VibrantPalette.secondaryLight,
                    VibrantPalette.tertiaryLight
                ),
                isSelected = currentTheme == GlobalTripThemeOption.VIBRANT,
                onClick = { onThemeSelected(GlobalTripThemeOption.VIBRANT) }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    name: String,
    colors: List<Color>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colors.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
            
            Text(
                text = name,
                style = MaterialTheme.typography.caption,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ThemePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Current Theme Preview",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            backgroundColor = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Primary color preview
                ColorPreviewRow(
                    name = "Primary",
                    color = MaterialTheme.colors.primary,
                    textColor = MaterialTheme.colors.onPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Secondary color preview
                ColorPreviewRow(
                    name = "Secondary",
                    color = MaterialTheme.colors.secondary,
                    textColor = MaterialTheme.colors.onSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Background color preview
                ColorPreviewRow(
                    name = "Background",
                    color = MaterialTheme.colors.background,
                    textColor = MaterialTheme.colors.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Surface color preview
                ColorPreviewRow(
                    name = "Surface",
                    color = MaterialTheme.colors.surface,
                    textColor = MaterialTheme.colors.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Error color preview
                ColorPreviewRow(
                    name = "Error",
                    color = MaterialTheme.colors.error,
                    textColor = MaterialTheme.colors.onError
                )
            }
        }
    }
}

@Composable
private fun ColorPreviewRow(
    name: String,
    color: Color,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.width(100.dp)
        )
        
        Box(
            modifier = Modifier
                .height(36.dp)
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Text on $name",
                color = textColor,
                style = MaterialTheme.typography.body2
            )
        }
    }
}