package com.quickchat.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AvatarCircle(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isOnline: Boolean = false
) {
    val initials = name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    // Deterministic color from name hash so each contact gets a consistent avatar color
    val colors = listOf(
        0xFF1B72C0, 0xFF6750A4, 0xFF7D5260, 0xFF006C51,
        0xFFB3261E, 0xFF7C5800, 0xFF006399, 0xFF984061
    )
    val bgColor = androidx.compose.ui.graphics.Color(colors[name.hashCode().mod(colors.size).let { if (it < 0) it + colors.size else it }])

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = (size.value * 0.38f).sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (isOnline) {
            OnlineStatusDot(
                isOnline = true,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
