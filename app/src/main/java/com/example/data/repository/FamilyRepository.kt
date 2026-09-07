package com.example.data.repository

import com.example.data.local.ConversationEntity
import com.example.data.local.FamilyDao
import com.example.data.local.FamilyEntity
import com.example.data.local.FamilyEventEntity
import com.example.data.local.FamilyFileEntity
import com.example.data.local.FamilyTaskEntity
import com.example.data.local.MemberEntity
import com.example.data.local.MessageEntity
import com.example.data.local.PostEntity
import com.example.data.model.FamilyRole
import com.example.data.model.FileCategory
import com.example.data.model.LocationShareMode
import com.example.data.model.MessageType
import com.example.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class FamilyRepository(private val dao: FamilyDao) {

    val familyProfile: Flow<FamilyEntity?> = dao.getFamilyProfile()
    val members: Flow<List<MemberEntity>> = dao.getAllMembers()
    val conversations: Flow<List<ConversationEntity>> = dao.getConversations()
    val posts: Flow<List<PostEntity>> = dao.getAllPosts()
    val tasks: Flow<List<FamilyTaskEntity>> = dao.getAllTasks()
    val events: Flow<List<FamilyEventEntity>> = dao.getAllEvents()
    val files: Flow<List<FamilyFileEntity>> = dao.getAllFiles()

    fun getMessagesForConversation(convoId: String): Flow<List<MessageEntity>> {
        return dao.getMessagesForConversation(convoId)
    }

    suspend fun sendMessage(
        convoId: String,
        senderId: String,
        senderName: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        voiceDurationSec: Int = 0
    ) {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val currentTime = timeFormat.format(Date())

        val message = MessageEntity(
            conversationId = convoId,
            senderId = senderId,
            senderName = senderName,
            text = text,
            type = type,
            voiceDurationSec = voiceDurationSec,
            timestampFormatted = currentTime,
            isFromMe = true
        )
        dao.insertMessage(message)
        val preview = if (type == MessageType.VOICE) "Voice message ($voiceDurationSec sec)" else text
        dao.updateConversationLastMessage(convoId, "$senderName: $preview", currentTime)
    }

    suspend fun toggleStarMessage(id: Long, isStarred: Boolean) {
        dao.toggleStarMessage(id, isStarred)
    }

    suspend fun addReaction(id: Long, reaction: String) {
        dao.updateReactions(id, reaction)
    }

    suspend fun createPost(
        authorId: String,
        authorName: String,
        authorRelation: String,
        avatarInitial: String,
        avatarColorHex: Long,
        content: String
    ) {
        val newPost = PostEntity(
            authorId = authorId,
            authorName = authorName,
            authorRelation = authorRelation,
            avatarInitial = avatarInitial,
            avatarColorHex = avatarColorHex,
            content = content,
            likesCount = 0,
            isLikedByMe = false,
            commentsCount = 0,
            timeAgo = "Just now",
            timestamp = System.currentTimeMillis()
        )
        dao.insertPost(newPost)
    }

    suspend fun togglePostLike(postId: Long, currentLiked: Boolean, currentCount: Int) {
        val newLiked = !currentLiked
        val newCount = if (newLiked) currentCount + 1 else (currentCount - 1).coerceAtLeast(0)
        dao.updatePostLike(postId, newLiked, newCount)
    }

    suspend fun updateTaskStatus(taskId: Long, newStatus: TaskStatus) {
        dao.updateTaskStatus(taskId, newStatus)
    }

    suspend fun addTask(title: String, assignedTo: String, dueDate: String) {
        val task = FamilyTaskEntity(
            title = title,
            assignedToName = assignedTo,
            status = TaskStatus.PENDING,
            dueDate = dueDate
        )
        dao.insertTask(task)
    }

    suspend fun updateLocationShareMode(memberId: String, mode: LocationShareMode) {
        dao.updateLocationShareMode(memberId, mode)
    }

    suspend fun updateMemberLocation(
        memberId: String,
        lat: Double,
        lng: Double,
        accuracy: Float,
        locationName: String
    ) {
        dao.updateMemberLocation(memberId, lat, lng, accuracy, locationName)
    }

    suspend fun updateAnnouncement(familyId: String, announcement: String) {
        dao.updateAnnouncement(familyId, announcement)
    }

    suspend fun uploadFile(name: String, uploader: String, category: FileCategory, sizeText: String) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val file = FamilyFileEntity(
            fileName = name,
            uploaderName = uploader,
            category = category,
            sizeText = sizeText,
            uploadedDate = dateFormat.format(Date())
        )
        dao.insertFile(file)
    }

    suspend fun addMember(
        name: String,
        relation: String,
        role: FamilyRole = FamilyRole.TRUSTED_MEMBER,
        phone: String = "",
        email: String = ""
    ): MemberEntity {
        val initials = name.trim().split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
            .ifEmpty { "FM" }

        val colors = listOf(
            0xFF2563EBL, // Blue
            0xFF059669L, // Green
            0xFF7C3AEDL, // Purple
            0xFFD97706L, // Amber
            0xFFDB2777L, // Pink
            0xFF0891B2L  // Cyan
        )
        val color = colors[abs(name.hashCode()) % colors.size]

        val newMember = MemberEntity(
            id = "mem_${System.currentTimeMillis() % 100000}",
            familyId = "FAMILY-7X82K",
            name = name.trim(),
            relation = relation.trim().ifEmpty { "Family Member" },
            role = role,
            phone = phone.ifEmpty { "+880 1700 000000" },
            email = email.ifEmpty { "${name.lowercase().replace(" ", "")}@family.internal" },
            avatarInitial = initials,
            avatarBgColorHex = color,
            batteryPercent = 92,
            locationName = "Home",
            locationShareMode = LocationShareMode.CONTINUOUS,
            lastSeen = "Just now",
            isOnline = true
        )
        dao.insertMember(newMember)

        // Post welcome message into family chat
        sendMessage(
            convoId = "convo_general",
            senderId = newMember.id,
            senderName = newMember.name,
            text = "👋 Hello everyone! I just joined the family circle.",
            type = MessageType.TEXT
        )

        return newMember
    }
}
