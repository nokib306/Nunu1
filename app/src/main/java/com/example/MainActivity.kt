package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.ConversationEntity
import com.example.data.local.HaqueFamDatabase
import com.example.data.model.CallType
import com.example.data.repository.FamilyRepository
import com.example.ui.FamilyViewModel
import com.example.ui.FamilyViewModelFactory
import com.example.ui.MainTab
import com.example.ui.screens.AdminSecurityScreen
import com.example.ui.screens.CalendarTasksScreen
import com.example.ui.screens.CallOverlayScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.CloudMediaScreen
import com.example.ui.screens.EmergencySosDialog
import com.example.ui.screens.FamilyMapScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JoinFamilyDialog
import com.example.ui.screens.MemberProfileModal
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = HaqueFamDatabase.getDatabase(this)
        val repository = FamilyRepository(database.familyDao())

        setContent {
            MyApplicationTheme {
                val viewModel: FamilyViewModel = viewModel(
                    factory = FamilyViewModelFactory(repository)
                )
                HaqueFamApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HaqueFamApp(viewModel: FamilyViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val activeConversation by viewModel.activeConversation.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val showEmergencyDialog by viewModel.showEmergencyDialog.collectAsState()
    val isEmergencyActive by viewModel.isEmergencyBroadcastActive.collectAsState()
    val memberProfileModal by viewModel.showMemberProfileModal.collectAsState()
    val showJoinFamilyDialog by viewModel.showJoinFamilyDialog.collectAsState()

    val familyProfile by viewModel.familyProfile.collectAsState()
    val members by viewModel.members.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val activeMessages by viewModel.activeMessages.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val events by viewModel.events.collectAsState()
    val files by viewModel.files.collectAsState()

    Box(modifier = Modifier.fillMaxSize().testTag("haque_fam_app_root")) {
        Scaffold(
            bottomBar = {
                // Show bottom bar only when not inside a chat detail conversation or in a full call
                if (activeConversation == null && activeCall == null) {
                    FamilyNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // If viewing a conversation detail
                if (activeConversation != null) {
                    ChatDetailScreen(
                        conversation = activeConversation!!,
                        messages = activeMessages,
                        members = members,
                        viewModel = viewModel,
                        onBack = { viewModel.closeConversation() }
                    )
                } else {
                    // Main Tabs
                    when (selectedTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                members = members,
                                posts = posts,
                                events = events,
                                tasks = tasks,
                                isEmergencyActive = isEmergencyActive,
                                onNavigateTab = { viewModel.selectTab(it) }
                            )
                        }

                        MainTab.CHATS -> {
                            ChatListScreen(
                                conversations = conversations,
                                onConversationClick = { convo ->
                                    viewModel.openConversation(convo)
                                }
                            )
                        }

                        MainTab.MAP -> {
                            FamilyMapScreen(
                                members = members,
                                viewModel = viewModel
                            )
                        }

                        MainTab.MEDIA -> {
                            CloudMediaScreen(
                                files = files,
                                viewModel = viewModel
                            )
                        }

                        MainTab.CALENDAR -> {
                            CalendarTasksScreen(
                                events = events,
                                tasks = tasks,
                                viewModel = viewModel
                            )
                        }

                        MainTab.ADMIN -> {
                            AdminSecurityScreen(
                                family = familyProfile,
                                members = members,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }

        // Active Call Fullscreen Overlay
        activeCall?.let { call ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CallOverlayScreen(
                    callData = call,
                    onMuteToggle = { viewModel.toggleMute() },
                    onCameraToggle = { viewModel.toggleCamera() },
                    onSpeakerToggle = { viewModel.toggleSpeaker() },
                    onScreenShareToggle = { viewModel.toggleScreenShare() },
                    onEndCall = { viewModel.endCall() }
                )
            }
        }

        // Emergency SOS Dialog
        if (showEmergencyDialog) {
            EmergencySosDialog(
                currentMember = viewModel.getCurrentMember(),
                emergencyContacts = members.filter { it.isEmergencyContact },
                onDismiss = { viewModel.dismissEmergencyDialog() },
                onConfirmBroadcast = { viewModel.triggerEmergencySOS() },
                onCallContact = { contact ->
                    viewModel.startCall(contact, CallType.VOICE)
                    viewModel.dismissEmergencyDialog()
                }
            )
        }

        // Member Profile Modal
        memberProfileModal?.let { member ->
            MemberProfileModal(
                member = member,
                onDismiss = { viewModel.setMemberProfileModal(null) },
                onStartCall = { target, callType ->
                    viewModel.setMemberProfileModal(null)
                    viewModel.startCall(target, callType)
                },
                onStartChat = { target ->
                    viewModel.setMemberProfileModal(null)
                    // Find or create direct convo
                    val targetConvo = conversations.firstOrNull { it.id.contains("mom") && target.name.contains("Nasreen") }
                        ?: conversations.firstOrNull { it.id.contains("samiul") && target.name.contains("Samiul") }
                        ?: conversations.firstOrNull()
                    if (targetConvo != null) {
                        viewModel.openConversation(targetConvo)
                    }
                }
            )
        }

        // Quick Setup & Join Family Dialog
        if (showJoinFamilyDialog) {
            JoinFamilyDialog(
                currentInviteCode = familyProfile?.inviteCode ?: "FAM-7X82K",
                familyName = familyProfile?.name ?: "Haque’sFam",
                onDismiss = { viewModel.closeJoinFamilyDialog() },
                onJoinMember = { name, relation, role, phone ->
                    viewModel.joinOrAddFamilyMember(name, relation, role, phone)
                }
            )
        }
    }
}

@Composable
fun FamilyNavigationBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.testTag("family_nav_bar")
    ) {
        NavigationBarItem(
            selected = selectedTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color(0xFF1E3A8A),
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = selectedTab == MainTab.CHATS,
            onClick = { onTabSelected(MainTab.CHATS) },
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
            label = { Text("Chats", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color(0xFF1E3A8A),
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_chats")
        )

        NavigationBarItem(
            selected = selectedTab == MainTab.MAP,
            onClick = { onTabSelected(MainTab.MAP) },
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Map") },
            label = { Text("Map", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color(0xFF1E3A8A),
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_map")
        )

        NavigationBarItem(
            selected = selectedTab == MainTab.MEDIA,
            onClick = { onTabSelected(MainTab.MEDIA) },
            icon = { Icon(Icons.Default.Folder, contentDescription = "Media") },
            label = { Text("Media", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color(0xFF1E3A8A),
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_media")
        )

        NavigationBarItem(
            selected = selectedTab == MainTab.CALENDAR,
            onClick = { onTabSelected(MainTab.CALENDAR) },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Schedule") },
            label = { Text("Schedule", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color(0xFF1E3A8A),
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_calendar")
        )

        NavigationBarItem(
            selected = selectedTab == MainTab.ADMIN,
            onClick = { onTabSelected(MainTab.ADMIN) },
            icon = { Icon(Icons.Default.Security, contentDescription = "Admin") },
            label = { Text("Admin", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color(0xFF1E3A8A),
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_admin")
        )
    }
}
