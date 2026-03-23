package com.quickchat.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickchat.app.ui.theme.OfflineGray
import com.quickchat.app.ui.theme.OnlineGreen

@Composable
fun OnlineStatusDot(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(14.dp)
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .background(
                color = if (isOnline) OnlineGreen else OfflineGray,
                shape = CircleShape
            )
    )
}
