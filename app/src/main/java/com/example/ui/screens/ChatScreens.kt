package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationEntity
import com.example.data.local.MemberEntity
import com.example.data.local.MessageEntity
import com.example.data.model.CallType
import com.example.data.model.MessageType
import com.example.ui.FamilyViewModel
import com.example.ui.components.MemberAvatar

@Composable
fun ChatListScreen(
    conversations: List<ConversationEntity>,
    onConversationClick: (ConversationEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("chat_list_screen")
    ) {
        // Header
        Surface(
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = "Family Messenger",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "End-to-end encrypted private family channels",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
        ) {
            items(conversations, key = { it.id }) { convo ->
                ConversationItem(
                    conversation = convo,
                    onClick = { onConversationClick(convo) }
                )
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: ConversationEntity,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("convo_item_${conversation.id}"),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MemberAvatar(
                initials = conversation.avatarInitials,
                bgColorHex = conversation.avatarColorHex,
                size = 52.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = conversation.lastMessageTime,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${conversation.unreadCount}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversation: ConversationEntity,
    messages: List<MessageEntity>,
    members: List<MemberEntity>,
    viewModel: FamilyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val isRecording by viewModel.isRecordingVoice.collectAsState()
    val recordSeconds by viewModel.recordingSeconds.collectAsState()
    val playingVoiceId by viewModel.playingVoiceMessageId.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = conversation.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = if (conversation.isGroup) "${conversation.memberCount} family members" else "Online • Tap to call",
                            fontSize = 11.sp,
                            color = Color(0xFF059669)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    // Call target member (or first member)
                    val targetMember = members.firstOrNull { !it.name.contains("Tariqul") } ?: members.first()
                    IconButton(onClick = { viewModel.startCall(targetMember, CallType.VOICE) }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Audio Call",
                            tint = Color(0xFF1E3A8A)
                        )
                    }
                    IconButton(onClick = { viewModel.startCall(targetMember, CallType.VIDEO) }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = Color(0xFF059669)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    if (isRecording) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEE2E2), RoundedCornerShape(24.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDC2626))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recording audio... 0:${recordSeconds.toString().padStart(2, '0')}",
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Row {
                                IconButton(onClick = { viewModel.cancelVoiceRecording() }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cancel",
                                        tint = Color(0xFF991B1B)
                                    )
                                }
                                IconButton(onClick = { viewModel.stopAndSendVoiceRecording(conversation.id) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send Voice",
                                        tint = Color(0xFF059669)
                                    )
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Location share shortcut
                            IconButton(
                                onClick = { viewModel.shareCurrentLocationInChat(conversation.id) },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = "Share Location",
                                    tint = Color(0xFF059669)
                                )
                            }

                            // Camera photo share shortcut
                            IconButton(
                                onClick = {
                                    viewModel.sendPhotoMessage(
                                        convoId = conversation.id,
                                        caption = "Family Snapshot"
                                    )
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("chat_camera_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Capture & Share Photo",
                                    tint = Color(0xFF2563EB)
                                )
                            }

                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = { Text("Family message...", fontSize = 14.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_field"),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedBorderColor = Color(0xFF2563EB)
                                ),
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (inputText.isNotBlank()) {
                                            viewModel.sendTextMessage(conversation.id, inputText)
                                            inputText = ""
                                        }
                                    }
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            if (inputText.isBlank()) {
                                IconButton(
                                    onClick = { viewModel.startVoiceRecording() },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Note",
                                        tint = Color(0xFF2563EB)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        viewModel.sendTextMessage(conversation.id, inputText)
                                        inputText = ""
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB))
                                        .testTag("send_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F5F9)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageBubble(
                    message = message,
                    isPlayingVoice = playingVoiceId == message.id,
                    onToggleVoice = { viewModel.togglePlayVoiceMessage(message.id) },
                    onToggleStar = { viewModel.toggleStarMessage(message.id, message.isStarred) },
                    onAddReaction = { emoji -> viewModel.addMessageReaction(message.id, emoji) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: MessageEntity,
    isPlayingVoice: Boolean,
    onToggleVoice: () -> Unit,
    onToggleStar: () -> Unit,
    onAddReaction: (String) -> Unit
) {
    val isFromMe = message.isFromMe

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
    ) {
        if (!isFromMe) {
            Text(
                text = message.senderName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569),
                modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isFromMe) 16.dp else 4.dp,
                bottomEnd = if (isFromMe) 4.dp else 16.dp
            ),
            color = if (isFromMe) Color(0xFF1E3A8A) else Color(0xFFF1F5F9),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                when (message.type) {
                    MessageType.VOICE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = onToggleVoice,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isFromMe) Color.White.copy(alpha = 0.2f) else Color(0xFFEFF6FF))
                            ) {
                                Icon(
                                    imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Voice Note",
                                    tint = if (isFromMe) Color.White else Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = if (isFromMe) Color.White.copy(alpha = 0.8f) else Color(0xFF2563EB),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "0:${message.voiceDurationSec.toString().padStart(2, '0')}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFromMe) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                    MessageType.LOCATION -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = if (isFromMe) Color(0xFF6EE7B7) else Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.text,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isFromMe) Color.White else Color(0xFF0F172A)
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = if (isFromMe) Color.White else Color(0xFF0F172A),
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isStarred) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Starred",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = message.timestampFormatted,
                        fontSize = 10.sp,
                        color = if (isFromMe) Color.White.copy(alpha = 0.7f) else Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Reactions Pill
        if (message.reactions.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clickable { onAddReaction("❤️") }
            ) {
                Text(
                    text = message.reactions,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
