package com.example.activitytracker.ui.screens.friends

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ContentCopy

import com.example.activitytracker.Core.theme.*
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
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Text(
            text = "Freunde",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (ownFriendCode.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, PrimaryAccent, RoundedCornerShape(16.dp))
                    .clickable {
                        clipboard.setText(AnnotatedString(ownFriendCode))
                    }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Column {
                    Text(
                        text = "Deine ID",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ownFriendCode,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Kopieren",
                            tint = PrimaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = friendCodeInput,
                onValueChange = { friendCodeInput = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(35),
                placeholder = {
                    Text(
                        "Freundes-ID eingeben",
                        color = TextDescription
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    onSendRequest(friendCodeInput)
                    friendCodeInput = ""
                },
                enabled = friendCodeInput.isNotBlank(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryAccent,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    "Senden",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (pendingRequests.isNotEmpty()) {
                item {
                    Text(
                        text = "Anfragen",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(pendingRequests) { request ->
                    FriendRequestCard(
                        request = request,
                        onAccept = { onAcceptRequest(request.requestId) },
                        onDecline = { onDeclineRequest(request.requestId) }
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            item {
                Text(
                    text = "Meine Freunde",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (friends.isEmpty()) {
                item {
                    Text(
                        text = "Noch keine Freunde hinzugefügt",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                items(friends) { friend ->
                    FriendCard(
                        friend = friend,
                        onRemove = { showRemoveDialog = friend }
                    )
                }
            }
        }
    }

    showRemoveDialog?.let { friend ->
        AlertDialog(
            onDismissRequest = { showRemoveDialog = null },
            title = { Text("Freund entfernen", fontWeight = FontWeight.Bold) },
            text = {
                Text("Möchtest du ${friend.username} wirklich entfernen?")
            },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveFriend(friend.friendId)
                    showRemoveDialog = null
                }) {
                    Text("Ja", color = AlertError, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = null }) {
                    Text("Nein")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun FriendCard(
    friend: FriendDto,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(35))
            .border(1.5.dp, PrimaryAccent, RoundedCornerShape(35)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = friend.username,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp)
        )

        Text(
            text = "🔥 ${friend.streak}",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(end = 4.dp)
        )

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Entfernen",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun FriendRequestCard(
    request: FriendRequestDto,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(35))
            .border(1.5.dp, TertiaryAccent, RoundedCornerShape(35))
            .padding(start = 20.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = request.senderUsername,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onAccept) {
            Text(
                "Annehmen",
                color = SecondaryAccent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
        TextButton(onClick = onDecline) {
            Text(
                "Ablehnen",
                color = AlertError,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}