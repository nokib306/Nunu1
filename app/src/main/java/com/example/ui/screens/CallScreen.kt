package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallState
import com.example.data.model.CallType
import com.example.ui.ActiveCallData
import com.example.ui.components.MemberAvatar

@Composable
fun CallOverlayScreen(
    callData: ActiveCallData,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onScreenShareToggle: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = callData.callState == CallState.CONNECTED
    val minutes = callData.durationSeconds / 60
    val seconds = callData.durationSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .testTag("call_overlay_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "End-to-End Encrypted WebRTC",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = callData.memberName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "${callData.memberRelation} • Haque Family",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (callData.callState) {
                        CallState.OUTGOING_RINGING -> "Calling via family mesh..."
                        CallState.INCOMING_RINGING -> "Incoming call..."
                        CallState.CONNECTED -> "Connected • $timeFormatted • Unlimited Family Call"
                        CallState.ENDED -> "Call ended"
                        else -> ""
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isConnected) Color(0xFF10B981) else Color(0xFF60A5FA)
                )

                if (isConnected) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (callData.isScreenSharing) Color(0xFFDC2626) else Color(0xFF2563EB),
                        modifier = Modifier
                            .clickable(onClick = onScreenShareToggle)
                            .testTag("quick_screenshare_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (callData.isScreenSharing) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (callData.isScreenSharing) "Stop Screen Sharing" else "One-Tap Screen Share",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Central Visual (Screen Share, Video Stream, or Soundwave Avatar)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                if (callData.isScreenSharing && isConnected) {
                    // Interactive Live Screen Sharing Frame
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(340.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEF4444))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIVE SCREEN BROADCAST",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                                Text(
                                    text = "Full HD • 60 FPS",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PresentToAll,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Your Screen is Live to Family",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Showing photos, maps, apps, or documents in real-time with zero lag.",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFDC2626),
                                modifier = Modifier.clickable(onClick = onScreenShareToggle)
                            ) {
                                Text(
                                    text = "Tap to Stop Sharing",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                } else if (callData.callType == CallType.VIDEO && !callData.isCameraOff && isConnected) {
                    // Simulated Live Video Preview Frame
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF334155),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(340.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                MemberAvatar(
                                    initials = callData.avatarInitial,
                                    bgColorHex = callData.avatarBgColorHex,
                                    size = 80.dp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "HD 1080p Video Stream",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Family Peer-to-Peer Relay • No Time Limits",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    // Voice Call soundwave / pulsating avatar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.scale(if (!isConnected) scalePulse else 1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                                .background(Color(callData.avatarBgColorHex).copy(alpha = 0.25f))
                        )
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(Color(callData.avatarBgColorHex).copy(alpha = 0.4f))
                        )
                        MemberAvatar(
                            initials = callData.avatarInitial,
                            bgColorHex = callData.avatarBgColorHex,
                            size = 100.dp
                        )
                    }
                }
            }

            // Action Buttons Panel
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                // Feature controls row
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute
                    CallControlButton(
                        icon = if (callData.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (callData.isMuted) "Unmute" else "Mute",
                        isActive = callData.isMuted,
                        onClick = onMuteToggle
                    )

                    // Video Camera Toggle
                    CallControlButton(
                        icon = if (callData.isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        label = if (callData.isCameraOff) "Start Video" else "Camera Off",
                        isActive = callData.isCameraOff,
                        onClick = onCameraToggle
                    )

                    // Speakerphone
                    CallControlButton(
                        icon = if (callData.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        label = "Speaker",
                        isActive = callData.isSpeakerOn,
                        onClick = onSpeakerToggle
                    )

                    // Screen Share
                    CallControlButton(
                        icon = if (callData.isScreenSharing) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                        label = "Share",
                        isActive = callData.isScreenSharing,
                        onClick = onScreenShareToggle
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Red Hangup Button
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDC2626))
                        .testTag("end_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isActive) Color(0xFFEF4444) else Color.White.copy(alpha = 0.15f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFFCBD5E1)
        )
    }
}
