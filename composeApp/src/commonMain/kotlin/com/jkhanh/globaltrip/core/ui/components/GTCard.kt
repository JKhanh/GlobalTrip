package com.jkhanh.globaltrip.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Card component for the GlobalTrip app
 */
@Composable
fun GTCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colors.surface,
    borderColor: Color? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = elevation,
        backgroundColor = backgroundColor,
        shape = MaterialTheme.shapes.medium,
        border = borderColor?.let { BorderStroke(1.dp, it) }
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

/**
 * Card with title component for the GlobalTrip app
 */
@Composable
fun GTTitledCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    elevation: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colors.surface,
    borderColor: Color? = null,
    titleStyle: TextStyle = MaterialTheme.typography.h6
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = elevation,
        backgroundColor = backgroundColor,
        shape = MaterialTheme.shapes.medium,
        border = borderColor?.let { BorderStroke(1.dp, it) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}
