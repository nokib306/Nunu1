package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MemberEntity
import com.example.data.location.LocationTracker
import com.example.data.model.CallType
import com.example.data.model.LocationShareMode
import com.example.ui.FamilyViewModel
import com.example.ui.components.MemberAvatar

@Composable
fun FamilyMapScreen(
    members: List<MemberEntity>,
    viewModel: FamilyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationTracker = remember { LocationTracker(context) }
    var hasLocationPermission by remember { mutableStateOf(locationTracker.hasLocationPermission()) }
    val isLiveTrackingActive by viewModel.isLiveLocationTrackingActive.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        hasLocationPermission = fineGranted || coarseGranted
        if (hasLocationPermission) {
            viewModel.startLiveLocationTracking(locationTracker)
        }
    }

    val currentMember = viewModel.getCurrentMember()
    var selectedMember by remember { mutableStateOf<MemberEntity?>(members.firstOrNull()) }
    var showPrivacyMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
            .testTag("family_map_screen")
    ) {
        // 1. High-fidelity custom Canvas Map with roads, lake, greenery, and radar rings
        FamilyMapCanvas(
            members = members,
            selectedMember = selectedMember,
            onSelectMember = { selectedMember = it }
        )

        // 2. Top Privacy Control & Geofence Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            // Privacy control & Live GPS banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isLiveTrackingActive) Color(0xFFDCFCE7) else Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isLiveTrackingActive) Icons.Default.GpsFixed else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (isLiveTrackingActive) Color(0xFF059669) else Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sharing: ${currentMember?.locationShareMode?.label ?: "Continuously"}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isLiveTrackingActive) "Google Play Services GPS: Active" else "Fused Location: Standby",
                                    fontSize = 11.sp,
                                    color = if (isLiveTrackingActive) Color(0xFF059669) else Color(0xFF64748B),
                                    fontWeight = if (isLiveTrackingActive) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }

                        // Live Location tracking toggle / permission request button
                        Button(
                            onClick = {
                                if (!hasLocationPermission) {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                } else {
                                    if (isLiveTrackingActive) {
                                        viewModel.stopLiveLocationTracking()
                                    } else {
                                        viewModel.startLiveLocationTracking(locationTracker)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    !hasLocationPermission -> Color(0xFF1E3A8A)
                                    isLiveTrackingActive -> Color(0xFFDC2626)
                                    else -> Color(0xFF059669)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("toggle_live_gps_button")
                        ) {
                            Icon(
                                imageVector = if (!hasLocationPermission) Icons.Default.MyLocation else if (isLiveTrackingActive) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when {
                                    !hasLocationPermission -> "Grant GPS"
                                    isLiveTrackingActive -> "Stop GPS"
                                    else -> "Live GPS"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { showPrivacyMenu = true }
                            ) {
                                Text(
                                    text = "Mode",
                                    color = Color(0xFF1E3A8A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showPrivacyMenu,
                                onDismissRequest = { showPrivacyMenu = false }
                            ) {
                                LocationShareMode.entries.forEach { mode ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(mode.label, fontWeight = FontWeight.Bold)
                                                Text(mode.description, fontSize = 11.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = {
                                            viewModel.setLocationShareMode(mode)
                                            showPrivacyMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recent Geofence Arrival Alert Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFEFF6FF)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Arrived Alert: Mom reached Central Market 4m ago (Accuracy ±15m)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }
        }

        // 3. Bottom Member Selection Carousel & Detail Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 92.dp)
                .align(Alignment.BottomCenter)
        ) {
            // Member Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members, key = { it.id }) { member ->
                    val isSelected = selectedMember?.id == member.id
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFFF1F5F9),
                        modifier = Modifier.clickable { selectedMember = member }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            MemberAvatar(
                                initials = member.avatarInitial,
                                bgColorHex = member.avatarBgColorHex,
                                size = 26.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = member.relation,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }

            // Selected Member Detail Panel
            selectedMember?.let { member ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MemberAvatar(
                                    initials = member.avatarInitial,
                                    bgColorHex = member.avatarBgColorHex,
                                    size = 46.dp,
                                    showOnlineBadge = true,
                                    isOnline = member.isOnline
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = member.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFEFF6FF)
                                        ) {
                                            Text(
                                                text = member.relation,
                                                color = Color(0xFF2563EB),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${member.locationName} • Last updated ${member.lastSeen}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            // Battery Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (member.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                                    contentDescription = null,
                                    tint = if (member.batteryPercent < 20) Color(0xFFDC2626) else Color(0xFF059669),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${member.batteryPercent}%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions Row: Call, Video, Message, Focus
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.startCall(member, CallType.VOICE) },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEFF6FF)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Voice Call",
                                        tint = Color(0xFF1E3A8A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Voice Call",
                                        color = Color(0xFF1E3A8A),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.startCall(member, CallType.VIDEO) },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFECFDF5)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = "Video Call",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Video Call",
                                        color = Color(0xFF059669),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyMapCanvas(
    members: List<MemberEntity>,
    selectedMember: MemberEntity?,
    onSelectMember: (MemberEntity) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(members) {
                detectTapGestures { offset ->
                    val width = size.width
                    val height = size.height

                    // Hit test member pins
                    members.forEach { member ->
                        val pos = getPinOffset(member.id, width.toFloat(), height.toFloat())
                        val dist = (pos - offset).getDistance()
                        if (dist < 80f) {
                            onSelectMember(member)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height

        // Background terrain
        drawRect(color = Color(0xFFF1F5F9))

        // Parks / Greenery
        drawRoundRect(
            color = Color(0xFFDCFCE7),
            topLeft = Offset(w * 0.08f, h * 0.22f),
            size = Size(w * 0.35f, h * 0.18f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
        )
        drawRoundRect(
            color = Color(0xFFDCFCE7),
            topLeft = Offset(w * 0.58f, h * 0.45f),
            size = Size(w * 0.36f, h * 0.22f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
        )

        // River curve
        val riverPath = Path().apply {
            moveTo(0f, h * 0.65f)
            cubicTo(
                w * 0.3f, h * 0.60f,
                w * 0.6f, h * 0.72f,
                w, h * 0.68f
            )
            lineTo(w, h * 0.74f)
            cubicTo(
                w * 0.6f, h * 0.78f,
                w * 0.3f, h * 0.66f,
                0f, h * 0.71f
            )
            close()
        }
        drawPath(path = riverPath, color = Color(0xFFBAE6FD))

        // Main Roads & Boulevards
        // Primary Horizontal Highway
        drawRect(
            color = Color(0xFFFFFFFF),
            topLeft = Offset(0f, h * 0.38f),
            size = Size(w, 24f)
        )
        drawRect(
            color = Color(0xFFCBD5E1),
            topLeft = Offset(0f, h * 0.38f + 11f),
            size = Size(w, 2f)
        )

        // Secondary Horizontal Road
        drawRect(
            color = Color(0xFFFFFFFF),
            topLeft = Offset(0f, h * 0.54f),
            size = Size(w, 18f)
        )

        // Vertical Avenues
        drawRect(
            color = Color(0xFFFFFFFF),
            topLeft = Offset(w * 0.32f, 0f),
            size = Size(22f, h)
        )
        drawRect(
            color = Color(0xFFFFFFFF),
            topLeft = Offset(w * 0.68f, 0f),
            size = Size(20f, h)
        )

        // Bridges over river
        drawRect(
            color = Color(0xFF94A3B8),
            topLeft = Offset(w * 0.30f, h * 0.62f),
            size = Size(26f, h * 0.12f)
        )
        drawRect(
            color = Color(0xFF94A3B8),
            topLeft = Offset(w * 0.66f, h * 0.65f),
            size = Size(24f, h * 0.12f)
        )

        // Draw Member Markers
        members.forEach { member ->
            val pinOffset = getPinOffset(member.id, w, h)
            val isSelected = selectedMember?.id == member.id

            // Accuracy pulse / halo
            drawCircle(
                color = Color(member.avatarBgColorHex).copy(alpha = if (isSelected) 0.25f else 0.12f),
                radius = if (isSelected) 60f else 38f,
                center = pinOffset
            )

            // Pin background circle
            drawCircle(
                color = Color.White,
                radius = 28f,
                center = pinOffset
            )
            drawCircle(
                color = Color(member.avatarBgColorHex),
                radius = 24f,
                center = pinOffset
            )

            // Selection ring
            if (isSelected) {
                drawCircle(
                    color = Color(0xFF2563EB),
                    radius = 32f,
                    center = pinOffset,
                    style = Stroke(width = 5f)
                )
            }
        }
    }
}

private fun getPinOffset(memberId: String, width: Float, height: Float): Offset {
    return when (memberId) {
        "mem_tariq" -> Offset(width * 0.32f, height * 0.36f)   // Dad @ Home
        "mem_nasreen" -> Offset(width * 0.68f, height * 0.42f) // Mom @ Central Market
        "mem_samiul" -> Offset(width * 0.24f, height * 0.52f)  // Brother @ University
        "mem_farhana" -> Offset(width * 0.72f, height * 0.28f) // Sister @ Art Center
        "mem_abdul" -> Offset(width * 0.40f, height * 0.34f)   // Grandpa @ Garden
        else -> Offset(width * 0.5f, height * 0.5f)
    }
}
