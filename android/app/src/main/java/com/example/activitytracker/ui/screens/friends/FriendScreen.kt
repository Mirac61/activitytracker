package com.example.activitytracker.ui.screens.friends

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activitytracker.Core.theme.PrimaryAccent
import com.example.activitytracker.Core.theme.SecondaryAccent
import com.example.activitytracker.data.remote.dto.FriendDto
import com.example.activitytracker.data.remote.dto.FriendRequestDto
import java.util.UUID

@Composable
fun FriendScreen(
    friends: List<FriendDto> = emptyList(),
    pendingRequests: List<FriendRequestDto> = emptyList(),
    ownFriendCode: String = "",
    onSendRequest: (String) -> Unit = {},
    onAcceptRequest: (UUID) -> Unit = {},
    onDeclineRequest: (UUID) -> Unit = {},
    onRemoveFriend: (UUID) -> Unit = {}
) {
    var friendCodeInput by remember { mutableStateOf("") }
    var showRemoveDialog by remember { mutableStateOf<FriendDto?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // ─── Titel ────────────────────────────────────────────────
        Text(
            text = "Freunde",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 22.sp),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ─── Eigene ID ────────────────────────────────────────────
        Text(
            text = "Deine ID: $ownFriendCode",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ─── Anfrage senden ───────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = friendCodeInput,
                onValueChange = { friendCodeInput = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Freundes-ID eingeben") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    onSendRequest(friendCodeInput)
                    friendCodeInput = ""
                },
                enabled = friendCodeInput.isNotBlank(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent)
            ) {
                Text("Senden", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ─── Offene Anfragen ──────────────────────────────────────
        if (pendingRequests.isNotEmpty()) {
            Text(
                text = "Anfragen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pendingRequests) { request ->
                    FriendRequestCard(
                        request = request,
                        onAccept = { onAcceptRequest(request.requestId) },
                        onDecline = { onDeclineRequest(request.requestId) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ─── Freundesliste ────────────────────────────────────────
        Text(
            text = "Meine Freunde",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (friends.isEmpty()) {
            Text(
                text = "Noch keine Freunde hinzugefügt",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(friends) { friend ->
                    FriendCard(
                        friend = friend,
                        onRemove = { showRemoveDialog = friend }
                    )
                }
            }
        }
    }

    // ─── Freund entfernen Dialog ──────────────────────────────────
    showRemoveDialog?.let { friend ->
        AlertDialog(
            onDismissRequest = { showRemoveDialog = null },
            title = { Text("Freund entfernen") },
            text = { Text("Möchtest du ${friend.username} wirklich entfernen?") },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveFriend(friend.friendId)
                    showRemoveDialog = null
                }) {
                    Text("Ja", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = null }) {
                    Text("Nein")
                }
            }
        )
    }
}

// ─── FriendCard ───────────────────────────────────────────────────

@Composable
fun FriendCard(
    friend: FriendDto,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.5.dp, PrimaryAccent, RoundedCornerShape(35)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = friend.username,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Entfernen",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

// ─── FriendRequestCard ────────────────────────────────────────────

@Composable
fun FriendRequestCard(
    request: FriendRequestDto,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, PrimaryAccent, RoundedCornerShape(35))
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = request.senderUsername,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onAccept) {
            Text("Annehmen", color = SecondaryAccent)
        }
        TextButton(onClick = onDecline) {
            Text("Ablehnen", color = MaterialTheme.colorScheme.error)
        }
    }
}