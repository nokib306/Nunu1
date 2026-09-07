package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ConversationEntity
import com.example.data.local.FamilyEntity
import com.example.data.local.FamilyEventEntity
import com.example.data.local.FamilyFileEntity
import com.example.data.local.FamilyTaskEntity
import com.example.data.local.MemberEntity
import com.example.data.local.MessageEntity
import com.example.data.local.PostEntity
import com.example.data.model.CallState
import com.example.data.model.CallType
import com.example.data.model.FamilyRole
import com.example.data.model.FileCategory
import com.example.data.model.LocationShareMode
import com.example.data.model.MessageType
import com.example.data.model.TaskStatus
import com.example.data.repository.FamilyRepository
import com.example.data.location.LocationTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
    HOME("Home"),
    CHATS("Chats"),
    MAP("Family Map"),
    MEDIA("Media & Files"),
    CALENDAR("Calendar & Tasks"),
    ADMIN("Admin & Security")
}

data class ActiveCallData(
    val memberName: String,
    val memberRelation: String,
    val avatarInitial: String,
    val avatarBgColorHex: Long,
    val callType: CallType,
    val callState: CallState = CallState.OUTGOING_RINGING,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isScreenSharing: Boolean = false
)

class FamilyViewModel(private val repository: FamilyRepository) : ViewModel() {

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    // Current active member (defaults to Tariqul Haque - Dad / Family Owner)
    private val _currentMemberId = MutableStateFlow("mem_tariq")
    val currentMemberId: StateFlow<String> = _currentMemberId.asStateFlow()

    // Active conversation for chat detail view (null = chat list)
    private val _activeConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeConversation: StateFlow<ConversationEntity?> = _activeConversation.asStateFlow()

    // Active call simulation
    private val _activeCall = MutableStateFlow<ActiveCallData?>(null)
    val activeCall: StateFlow<ActiveCallData?> = _activeCall.asStateFlow()
    private var callTimerJob: Job? = null

    // Emergency SOS State
    private val _showEmergencyDialog = MutableStateFlow(false)
    val showEmergencyDialog: StateFlow<Boolean> = _showEmergencyDialog.asStateFlow()

    private val _isEmergencyBroadcastActive = MutableStateFlow(false)
    val isEmergencyBroadcastActive: StateFlow<Boolean> = _isEmergencyBroadcastActive.asStateFlow()

    // Voice recording simulation
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()
    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()
    private var voiceTimerJob: Job? = null

    // Playing voice message simulation (ID of currently playing message)
    private val _playingVoiceMessageId = MutableStateFlow<Long?>(null)
    val playingVoiceMessageId: StateFlow<Long?> = _playingVoiceMessageId.asStateFlow()

    // UI Dialogs
    private val _showNewPostSheet = MutableStateFlow(false)
    val showNewPostSheet: StateFlow<Boolean> = _showNewPostSheet.asStateFlow()

    private val _showAddTaskSheet = MutableStateFlow(false)
    val showAddTaskSheet: StateFlow<Boolean> = _showAddTaskSheet.asStateFlow()

    private val _showUploadSheet = MutableStateFlow(false)
    val showUploadSheet: StateFlow<Boolean> = _showUploadSheet.asStateFlow()

    private val _showMemberProfileModal = MutableStateFlow<MemberEntity?>(null)
    val showMemberProfileModal: StateFlow<MemberEntity?> = _showMemberProfileModal.asStateFlow()

    private val _showJoinFamilyDialog = MutableStateFlow(false)
    val showJoinFamilyDialog: StateFlow<Boolean> = _showJoinFamilyDialog.asStateFlow()

    val familyProfile: StateFlow<FamilyEntity?> = repository.familyProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val members: StateFlow<List<MemberEntity>> = repository.members
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = repository.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMessages: StateFlow<List<MessageEntity>> = _activeConversation
        .flatMapLatest { convo ->
            if (convo != null) repository.getMessagesForConversation(convo.id)
            else MutableStateFlow(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<PostEntity>> = repository.posts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<FamilyTaskEntity>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<FamilyEventEntity>> = repository.events
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val files: StateFlow<List<FamilyFileEntity>> = repository.files
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    fun openConversation(conversation: ConversationEntity) {
        _activeConversation.value = conversation
    }

    fun closeConversation() {
        _activeConversation.value = null
    }

    fun switchActiveMember(memberId: String) {
        _currentMemberId.value = memberId
    }

    fun openJoinFamilyDialog() {
        _showJoinFamilyDialog.value = true
    }

    fun closeJoinFamilyDialog() {
        _showJoinFamilyDialog.value = false
    }

    fun joinOrAddFamilyMember(
        name: String,
        relation: String,
        role: FamilyRole = FamilyRole.TRUSTED_MEMBER,
        phone: String = ""
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val member = repository.addMember(name, relation, role, phone)
            _currentMemberId.value = member.id
            _showJoinFamilyDialog.value = false
        }
    }

    fun getCurrentMember(): MemberEntity? {
        return members.value.find { it.id == _currentMemberId.value }
            ?: members.value.firstOrNull()
    }

    // Messaging actions
    fun sendTextMessage(convoId: String, text: String) {
        if (text.isBlank()) return
        val current = getCurrentMember() ?: return
        viewModelScope.launch {
            repository.sendMessage(
                convoId = convoId,
                senderId = current.id,
                senderName = current.relation,
                text = text.trim(),
                type = MessageType.TEXT
            )
        }
    }

    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _recordingSeconds.value = 0
        voiceTimerJob?.cancel()
        voiceTimerJob = viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                _recordingSeconds.value += 1
            }
        }
    }

    fun stopAndSendVoiceRecording(convoId: String) {
        val duration = _recordingSeconds.value
        _isRecordingVoice.value = false
        voiceTimerJob?.cancel()
        if (duration < 1) return

        val current = getCurrentMember() ?: return
        viewModelScope.launch {
            repository.sendMessage(
                convoId = convoId,
                senderId = current.id,
                senderName = current.relation,
                text = "Voice message (${duration}s)",
                type = MessageType.VOICE,
                voiceDurationSec = duration
            )
        }
    }

    fun cancelVoiceRecording() {
        _isRecordingVoice.value = false
        _recordingSeconds.value = 0
        voiceTimerJob?.cancel()
    }

    fun togglePlayVoiceMessage(messageId: Long) {
        if (_playingVoiceMessageId.value == messageId) {
            _playingVoiceMessageId.value = null
        } else {
            _playingVoiceMessageId.value = messageId
            viewModelScope.launch {
                delay(4000)
                if (_playingVoiceMessageId.value == messageId) {
                    _playingVoiceMessageId.value = null
                }
            }
        }
    }

    fun shareCurrentLocationInChat(convoId: String) {
        val current = getCurrentMember() ?: return
        viewModelScope.launch {
            repository.sendMessage(
                convoId = convoId,
                senderId = current.id,
                senderName = current.relation,
                text = "📍 Live Location: ${current.locationName} (${current.latitude}, ${current.longitude})",
                type = MessageType.LOCATION
            )
        }
    }

    fun sendPhotoMessage(convoId: String, photoUri: String = "", caption: String = "Family Photo") {
        val current = getCurrentMember() ?: return
        viewModelScope.launch {
            repository.sendMessage(
                convoId = convoId,
                senderId = current.id,
                senderName = current.relation,
                text = "📷 $caption",
                type = MessageType.PHOTO
            )
        }
    }

    fun toggleStarMessage(messageId: Long, currentStarred: Boolean) {
        viewModelScope.launch {
            repository.toggleStarMessage(messageId, !currentStarred)
        }
    }

    fun addMessageReaction(messageId: Long, emoji: String) {
        viewModelScope.launch {
            repository.addReaction(messageId, emoji)
        }
    }

    // Feed actions
    fun createPost(content: String) {
        if (content.isBlank()) return
        val current = getCurrentMember() ?: return
        viewModelScope.launch {
            repository.createPost(
                authorId = current.id,
                authorName = current.name,
                authorRelation = current.relation,
                avatarInitial = current.avatarInitial,
                avatarColorHex = current.avatarBgColorHex,
                content = content.trim()
            )
            _showNewPostSheet.value = false
        }
    }

    fun togglePostLike(post: PostEntity) {
        viewModelScope.launch {
            repository.togglePostLike(post.id, post.isLikedByMe, post.likesCount)
        }
    }

    // Task actions
    fun toggleTaskStatus(task: FamilyTaskEntity) {
        val nextStatus = when (task.status) {
            TaskStatus.PENDING -> TaskStatus.IN_PROGRESS
            TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
            TaskStatus.COMPLETED -> TaskStatus.PENDING
        }
        viewModelScope.launch {
            repository.updateTaskStatus(task.id, nextStatus)
        }
    }

    fun addNewTask(title: String, assignedTo: String, dueDate: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(title.trim(), assignedTo, dueDate.ifBlank { "Flexible" })
            _showAddTaskSheet.value = false
        }
    }

    // File actions
    fun uploadFile(name: String, category: FileCategory, sizeText: String) {
        val current = getCurrentMember() ?: return
        viewModelScope.launch {
            repository.uploadFile(name, current.relation, category, sizeText)
            _showUploadSheet.value = false
        }
    }

    // Calling actions
    fun startCall(member: MemberEntity, callType: CallType) {
        _activeCall.value = ActiveCallData(
            memberName = member.name,
            memberRelation = member.relation,
            avatarInitial = member.avatarInitial,
            avatarBgColorHex = member.avatarBgColorHex,
            callType = callType,
            callState = CallState.OUTGOING_RINGING
        )

        // Simulate WebRTC handshake & connection after 2 seconds
        viewModelScope.launch {
            delay(2000)
            if (_activeCall.value != null && _activeCall.value?.callState == CallState.OUTGOING_RINGING) {
                _activeCall.value = _activeCall.value?.copy(callState = CallState.CONNECTED)
                startCallTimer()
            }
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_activeCall.value?.callState == CallState.CONNECTED) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    fun toggleMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isCameraOff = !it.isCameraOff) }
    }

    fun toggleSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleScreenShare() {
        _activeCall.value = _activeCall.value?.let { it.copy(isScreenSharing = !it.isScreenSharing) }
    }

    fun endCall() {
        callTimerJob?.cancel()
        _activeCall.value = _activeCall.value?.copy(callState = CallState.ENDED)
        viewModelScope.launch {
            delay(600)
            _activeCall.value = null
        }
    }

    // Location privacy toggle
    private var locationTrackingJob: Job? = null
    private val _isLiveLocationTrackingActive = MutableStateFlow(false)
    val isLiveLocationTrackingActive: StateFlow<Boolean> = _isLiveLocationTrackingActive.asStateFlow()

    fun startLiveLocationTracking(locationTracker: LocationTracker) {
        if (!locationTracker.hasLocationPermission()) return
        _isLiveLocationTrackingActive.value = true
        locationTrackingJob?.cancel()
        locationTrackingJob = viewModelScope.launch {
            try {
                locationTracker.getLocationUpdates(intervalMs = 5000L).collect { location ->
                    val current = getCurrentMember() ?: return@collect
                    repository.updateMemberLocation(
                        memberId = current.id,
                        lat = location.latitude,
                        lng = location.longitude,
                        accuracy = location.accuracy,
                        locationName = "Live GPS Device"
                    )
                }
            } catch (e: Exception) {
                _isLiveLocationTrackingActive.value = false
            }
        }
    }

    fun stopLiveLocationTracking() {
        locationTrackingJob?.cancel()
        _isLiveLocationTrackingActive.value = false
    }

    fun setLocationShareMode(mode: LocationShareMode) {
        val current = getCurrentMember() ?: return
        viewModelScope.launch {
            repository.updateLocationShareMode(current.id, mode)
        }
    }

    // Emergency actions
    fun openEmergencyDialog() {
        _showEmergencyDialog.value = true
    }

    fun dismissEmergencyDialog() {
        _showEmergencyDialog.value = false
    }

    fun triggerEmergencySOS() {
        _isEmergencyBroadcastActive.value = true
        _showEmergencyDialog.value = false
        val current = getCurrentMember()
        val sender = current?.relation ?: "Family Member"
        viewModelScope.launch {
            repository.sendMessage(
                convoId = "convo_family",
                senderId = current?.id ?: "emergency",
                senderName = "🚨 SOS ALERT",
                text = "EMERGENCY ALERT: $sender triggered Emergency Mode! Live location: ${current?.locationName ?: "Unknown"} (Lat: ${current?.latitude ?: 23.79}, Lng: ${current?.longitude ?: 90.41}). Please check in immediately!",
                type = MessageType.LOCATION
            )
        }
    }

    fun cancelEmergencySOS() {
        _isEmergencyBroadcastActive.value = false
    }

    // Dialog state toggles
    fun setShowNewPostSheet(show: Boolean) { _showNewPostSheet.value = show }
    fun setShowAddTaskSheet(show: Boolean) { _showAddTaskSheet.value = show }
    fun setShowUploadSheet(show: Boolean) { _showUploadSheet.value = show }
    fun setMemberProfileModal(member: MemberEntity?) { _showMemberProfileModal.value = member }

    fun updateAnnouncement(newText: String) {
        viewModelScope.launch {
            repository.updateAnnouncement("FAMILY-7X82K", newText)
        }
    }
}

class FamilyViewModelFactory(private val repository: FamilyRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamilyViewModel::class.java)) {
            return FamilyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
