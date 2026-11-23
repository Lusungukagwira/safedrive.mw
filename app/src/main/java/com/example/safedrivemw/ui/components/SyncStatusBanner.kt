package com.example.safedrivemw.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.data.sync.SyncState

@Composable
fun SyncStatusBanner(
    syncState: SyncState,
    pendingCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val effectiveState = when {
        syncState == SyncState.OFFLINE -> SyncState.OFFLINE
        pendingCount > 0 && syncState == SyncState.ONLINE -> SyncState.SYNCING
        pendingCount == 0 && syncState == SyncState.ONLINE -> SyncState.SYNCED
        else -> syncState
    }

    val (bgColor, textColor, label) = when (effectiveState) {
        SyncState.ONLINE -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "🟢 ONLINE • Connected to Server")
        SyncState.OFFLINE -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "🔴 OFFLINE MODE • Data saved locally")
        SyncState.SYNCING -> Triple(Color(0xFFFEF9C3), Color(0xFF854D0E), "🔄 SYNCING ($pendingCount pending)... • Uploading to Firebase")
        SyncState.SYNCED -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), "✓ SYNCED • All data up to date")
        SyncState.SYNC_FAILED -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "❌ SYNC FAILED • Retrying automatically")
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(8.dp),
            color = bgColor
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
