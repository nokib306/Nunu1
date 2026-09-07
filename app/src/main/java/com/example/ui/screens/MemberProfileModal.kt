package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.MemberEntity
import com.example.data.model.CallType
import com.example.ui.components.MemberAvatar
import com.example.ui.components.RoleBadge

@Composable
fun MemberProfileModal(
    member: MemberEntity,
    onDismiss: () -> Unit,
    onStartCall: (MemberEntity, CallType) -> Unit,
    onStartChat: (MemberEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("member_profile_modal")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Family Member Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar, Name, Role
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MemberAvatar(
                        initials = member.avatarInitial,
                        bgColorHex = member.avatarBgColorHex,
                        size = 56.dp,
                        showOnlineBadge = true,
                        isOnline = member.isOnline
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = member.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${member.relation} • ${if (member.isOnline) "Active Now" else member.lastSeen}",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        RoleBadge(role = member.role)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Info Cards
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        ProfileInfoRow(icon = Icons.Default.Phone, label = "Phone", value = member.phone)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = member.email)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileInfoRow(
                            icon = Icons.Default.LocationOn,
                            label = "Location",
                            value = "${member.locationName} (±${member.locationAccuracyMeters}m)"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileInfoRow(
                            icon = if (member.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                            label = "Battery",
                            value = "${member.batteryPercent}% ${if (member.isCharging) "(Charging)" else ""}"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons: Audio Call, Video Call, Chat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onStartCall(member, CallType.VOICE) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Voice", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onStartCall(member, CallType.VIDEO) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Video", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onStartChat(member) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold)
    }
}
