package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FamilyEventEntity
import com.example.data.local.FamilyTaskEntity
import com.example.data.local.MemberEntity
import com.example.data.local.PostEntity
import com.example.data.model.CallType
import com.example.ui.FamilyViewModel
import com.example.ui.MainTab
import com.example.ui.components.MemberAvatar

@Composable
fun HomeScreen(
    viewModel: FamilyViewModel,
    members: List<MemberEntity>,
    posts: List<PostEntity>,
    events: List<FamilyEventEntity>,
    tasks: List<FamilyTaskEntity>,
    isEmergencyActive: Boolean,
    onNavigateTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMember = viewModel.getCurrentMember()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Top Header with Family Brand & Quick Emergency SOS
        item {
            HomeHeaderSection(
                currentMember = currentMember,
                isEmergencyActive = isEmergencyActive,
                onEmergencyClick = { viewModel.openEmergencyDialog() },
                onSwitchProfileClick = { viewModel.selectTab(MainTab.ADMIN) },
                onOpenJoinDialog = { viewModel.openJoinFamilyDialog() }
            )
        }

        // 2. Active Emergency Alert Banner (if triggered)
        item {
            AnimatedVisibility(visible = isEmergencyActive) {
                EmergencyActiveBanner(
                    onCancel = { viewModel.cancelEmergencySOS() }
                )
            }
        }

        // 3. Easy Member Join & Family Setup Card (Zero password friction)
        item {
            FamilyJoinSetupBanner(
                inviteCode = "FAM-7X82K",
                onOpenJoinDialog = { viewModel.openJoinFamilyDialog() }
            )
        }

        // 4. Pinned Family Announcement
        item {
            PinnedAnnouncementCard(
                announcement = "📢 Family dinner & planning for Eid holiday this Friday 8:00 PM at Dad's home!"
            )
        }

        // 5. Sequential Quick Navigation (Standard & Simple Vibe)
        // 💬 Chat → 📍 Map → 📞 Calls → 📁 Media & Files → 📅 Events
        item {
            FamilyQuickFlowRow(
                onNavigateTab = onNavigateTab,
                onOpenJoinDialog = { viewModel.openJoinFamilyDialog() }
            )
        }

        // 6. Live Family Status (Avatars, battery, live location + Add Member button)
        item {
            FamilyMembersLiveStatus(
                members = members,
                onMemberClick = { member ->
                    viewModel.setMemberProfileModal(member)
                },
                onCallMember = { member, type ->
                    viewModel.startCall(member, type)
                },
                onAddMemberClick = { viewModel.openJoinFamilyDialog() }
            )
        }

        // 6. Family Hero Banner / Featured Memory
        item {
            FeaturedFamilyBanner(
                onExploreMedia = { onNavigateTab(MainTab.MEDIA) }
            )
        }

        // 7. Recent Family Social Feed Highlight
        item {
            posts.firstOrNull()?.let { topPost ->
                RecentPostCard(
                    post = topPost,
                    onLikeClick = { viewModel.togglePostLike(topPost) },
                    onViewFeed = { onNavigateTab(MainTab.HOME) }
                )
            }
        }

        // 8. Upcoming Family Events & Pending Tasks
        item {
            EventsAndTasksPreview(
                events = events,
                tasks = tasks,
                onToggleTask = { viewModel.toggleTaskStatus(it) },
                onViewAll = { onNavigateTab(MainTab.CALENDAR) }
            )
        }

        // 9. Family Storage Quota preview
        item {
            FamilyStoragePreviewCard(
                onViewFiles = { onNavigateTab(MainTab.MEDIA) }
            )
        }
    }
}

@Composable
private fun HomeHeaderSection(
    currentMember: MemberEntity?,
    isEmergencyActive: Boolean,
    onEmergencyClick: () -> Unit,
    onSwitchProfileClick: () -> Unit,
    onOpenJoinDialog: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E3A8A)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Haque’sFam",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF3B82F6).copy(alpha = 0.35f),
                            modifier = Modifier.clickable(onClick = onOpenJoinDialog)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GroupAdd,
                                    contentDescription = "Invite Code",
                                    tint = Color(0xFF93C5FD),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "FAM-7X82K",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    Text(
                        text = "Hi, ${currentMember?.name?.split(" ")?.firstOrNull() ?: "Tariqul"} 👋 • Private Family Circle",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Emergency Button
                    Button(
                        onClick = onEmergencyClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEmergencyActive) Color(0xFFEF4444) else Color(0xFFDC2626)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("emergency_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency SOS",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SOS",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Active Member Switcher Pill
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clickable(onClick = onSwitchProfileClick)
                            .testTag("profile_switcher_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            MemberAvatar(
                                initials = currentMember?.avatarInitial ?: "TH",
                                bgColorHex = currentMember?.avatarBgColorHex ?: 0xFF1E3A8A,
                                size = 28.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentMember?.relation ?: "Dad",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyJoinSetupBanner(
    inviteCode: String,
    onOpenJoinDialog: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("family_join_setup_banner"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Family Member Setup",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Easy 1-step join for spouse, kids & elders",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.clickable {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(inviteCode))
                        isCopied = true
                        android.widget.Toast.makeText(context, "Code copied: $inviteCode", android.widget.Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = inviteCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenJoinDialog,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Add Member",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onOpenJoinDialog,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color(0xFF334155),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share Invite",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyActiveBanner(onCancel: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🚨 EMERGENCY SOS ACTIVE",
                    color = Color(0xFF991B1B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Live GPS coordinates broadcasted to all family members.",
                    color = Color(0xFF7F1D1D),
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Dismiss", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

@Composable
private fun PinnedAnnouncementCard(announcement: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PushPin,
                contentDescription = "Pinned Announcement",
                tint = Color(0xFFB45309),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "PINNED FAMILY NOTICE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = announcement,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF78350F),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun FamilyQuickFlowRow(
    onNavigateTab: (MainTab) -> Unit,
    onOpenJoinDialog: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FAMILY MODULES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.8.sp
            )
            Text(
                text = "Simple & Fast",
                fontSize = 11.sp,
                color = Color(0xFF2563EB),
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickNavItem(
                icon = Icons.Default.Chat,
                label = "Chats",
                bgGradient = listOf(Color(0xFF0D9488), Color(0xFF14B8A6)),
                onClick = { onNavigateTab(MainTab.CHATS) }
            )
            QuickNavItem(
                icon = Icons.Default.LocationOn,
                label = "Map",
                bgGradient = listOf(Color(0xFF059669), Color(0xFF10B981)),
                onClick = { onNavigateTab(MainTab.MAP) }
            )
            QuickNavItem(
                icon = Icons.Default.Call,
                label = "Calls",
                bgGradient = listOf(Color(0xFF2563EB), Color(0xFF3B82F6)),
                onClick = { onNavigateTab(MainTab.CHATS) }
            )
            QuickNavItem(
                icon = Icons.Default.Folder,
                label = "Cloud Vault",
                bgGradient = listOf(Color(0xFF4F46E5), Color(0xFF6366F1)),
                onClick = { onNavigateTab(MainTab.MEDIA) }
            )
            QuickNavItem(
                icon = Icons.Default.CalendarMonth,
                label = "Events",
                bgGradient = listOf(Color(0xFF9333EA), Color(0xFFA855F7)),
                onClick = { onNavigateTab(MainTab.CALENDAR) }
            )
            QuickNavItem(
                icon = Icons.Default.PersonAdd,
                label = "+ Member",
                bgGradient = listOf(Color(0xFFEA580C), Color(0xFFF97316)),
                onClick = onOpenJoinDialog
            )
        }
    }
}

@Composable
private fun QuickNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    bgGradient: List<Color>,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("quick_nav_$label"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(bgGradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
private fun FamilyMembersLiveStatus(
    members: List<MemberEntity>,
    onMemberClick: (MemberEntity) -> Unit,
    onCallMember: (MemberEntity, CallType) -> Unit,
    onAddMemberClick: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FAMILY MEMBERS LIVE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.8.sp
            )
            Text(
                text = "${members.size} connected • Unlimited",
                fontSize = 12.sp,
                color = Color(0xFF059669),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            members.forEach { member ->
                Surface(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { onMemberClick(member) }
                        .testTag("member_card_${member.id}"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MemberAvatar(
                            initials = member.avatarInitial,
                            bgColorHex = member.avatarBgColorHex,
                            size = 46.dp,
                            showOnlineBadge = true,
                            isOnline = member.isOnline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = member.name.split(" ").firstOrNull() ?: member.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = member.relation,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Battery & Location Chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (member.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                                contentDescription = null,
                                tint = if (member.batteryPercent < 20) Color(0xFFDC2626) else Color(0xFF059669),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${member.batteryPercent}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = member.locationName,
                            fontSize = 10.sp,
                            color = Color(0xFF475569),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Call Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(
                                onClick = { onCallMember(member, CallType.VOICE) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Voice Call",
                                    tint = Color(0xFF1E3A8A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onCallMember(member, CallType.VIDEO) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Video Call",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick "+ Add Member" Card at the end of the Family Circle
            Surface(
                modifier = Modifier
                    .width(135.dp)
                    .clickable { onAddMemberClick() }
                    .testTag("add_member_quick_card"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEFF6FF)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Member",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+ Add Member",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF2563EB)
                    )
                    Text(
                        text = "Join with code",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "INSTANT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedFamilyBanner(
    onExploreMedia: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .clickable(onClick = onExploreMedia),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_family_banner),
                    contentDescription = "Family moments illustration",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0x99000000))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "FAMILY VAULT & ALBUMS",
                        color = Color(0xFFFBBF24),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Cherished Moments & Shared Vault",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentPostCard(
    post: PostEntity,
    onLikeClick: () -> Unit,
    onViewFeed: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MemberAvatar(
                    initials = post.avatarInitial,
                    bgColorHex = post.avatarColorHex,
                    size = 38.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${post.authorRelation})",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Text(
                        text = post.timeAgo,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                post.memoryBadge?.let { badge ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                fontSize = 14.sp,
                color = Color(0xFF1E293B),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onLikeClick)
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) Color(0xFFDC2626) else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likesCount} family likes",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${post.commentsCount} comments",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun EventsAndTasksPreview(
    events: List<FamilyEventEntity>,
    tasks: List<FamilyTaskEntity>,
    onToggleTask: (FamilyTaskEntity) -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "UPCOMING & FAMILY CHORES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.8.sp
            )
            Text(
                text = "View All",
                fontSize = 12.sp,
                color = Color(0xFF1E3A8A),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onViewAll)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Next Event Card
        events.firstOrNull()?.let { event ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "${event.dateStr} • ${event.daysRemaining}",
                            fontSize = 12.sp,
                            color = Color(0xFF3B82F6)
                        )
                    }
                }
            }
        }

        // Active Task snippet
        tasks.firstOrNull()?.let { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onToggleTask(task) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (task.status == com.example.data.model.TaskStatus.COMPLETED)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Status",
                            tint = if (task.status == com.example.data.model.TaskStatus.COMPLETED)
                                Color(0xFF059669)
                            else
                                Color(0xFF94A3B8)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Assigned: ${task.assignedToName} • ${task.dueDate}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (task.status) {
                            com.example.data.model.TaskStatus.COMPLETED -> Color(0xFFD1FAE5)
                            com.example.data.model.TaskStatus.IN_PROGRESS -> Color(0xFFFEF3C7)
                            com.example.data.model.TaskStatus.PENDING -> Color(0xFFF1F5F9)
                        }
                    ) {
                        Text(
                            text = task.status.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (task.status) {
                                com.example.data.model.TaskStatus.COMPLETED -> Color(0xFF065F46)
                                com.example.data.model.TaskStatus.IN_PROGRESS -> Color(0xFF92400E)
                                com.example.data.model.TaskStatus.PENDING -> Color(0xFF475569)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyStoragePreviewCard(
    onViewFiles: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Unlimited Family Cloud",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "No storage caps • All family members included",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = "UNLIMITED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Family Vault Status",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "100% Free & Unlimited Resources",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                    Text(
                        text = "Open Vault →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable(onClick = onViewFiles)
                    )
                }
            }
        }
    }
}
