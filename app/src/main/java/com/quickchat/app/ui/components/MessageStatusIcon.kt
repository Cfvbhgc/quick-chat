package com.quickchat.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickchat.app.domain.model.MessageStatus
import com.quickchat.app.ui.theme.MessageReadBlue
import com.quickchat.app.ui.theme.SentGray

/**
 * Renders message delivery status as checkmark icons:
 * - SENT: single gray check
 * - DELIVERED: double gray check
 * - READ: double blue check
 */
@Composable
fun MessageStatusIcon(
    status: MessageStatus,
    modifier: Modifier = Modifier
) {
    val iconSize = 16.dp

    when (status) {
        MessageStatus.SENT -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                modifier = modifier.size(iconSize),
                tint = SentGray
            )
        }
        MessageStatus.DELIVERED -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Delivered",
                modifier = modifier.size(iconSize),
                tint = SentGray
            )
        }
        MessageStatus.READ -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Read",
                modifier = modifier.size(iconSize),
                tint = MessageReadBlue
            )
        }
    }
}
