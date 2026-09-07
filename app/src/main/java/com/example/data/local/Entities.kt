package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.FamilyRole
import com.example.data.model.FileCategory
import com.example.data.model.LocationShareMode
import com.example.data.model.MessageType
import com.example.data.model.TaskStatus

@Entity(tableName = "family_profile")
data class FamilyEntity(
    @PrimaryKey val id: String = "FAMILY-7X82K",
    val name: String = "Haque’sFam",
    val inviteCode: String = "FAM-7X82K",
    val storageUsedMB: Int = 18432, // 18.4 GB
    val storageLimitMB: Int = 102400, // 100 GB family plan
    val announcement: String = "Family dinner at Residence this Friday at 8:00 PM! Don't be late."
)

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey val id: String,
    val familyId: String = "FAMILY-7X82K",
    val name: String,
    val relation: String,
    val role: FamilyRole,
    val phone: String,
    val email: String,
    val avatarInitial: String,
    val avatarBgColorHex: Long,
    val isEmergencyContact: Boolean = false,
    val batteryPercent: Int = 85,
    val isCharging: Boolean = false,
    val locationName: String = "Home",
    val latitude: Double = 23.7937,
    val longitude: Double = 90.4066,
    val locationAccuracyMeters: Int = 12,
    val locationShareMode: LocationShareMode = LocationShareMode.CONTINUOUS,
    val lastSeen: String = "Just now",
    val isOnline: Boolean = true
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val isGroup: Boolean,
    val memberCount: Int,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val avatarInitials: String,
    val avatarColorHex: Long
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val voiceDurationSec: Int = 0,
    val reactions: String = "", // e.g. "❤️2,👍1"
    val isStarred: Boolean = false,
    val timestampFormatted: String,
    val isFromMe: Boolean = false
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: String,
    val authorName: String,
    val authorRelation: String,
    val avatarInitial: String,
    val avatarColorHex: Long,
    val content: String,
    val memoryBadge: String? = null,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val commentsCount: Int = 0,
    val timeAgo: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class FamilyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val assignedToName: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val dueDate: String
)

@Entity(tableName = "events")
data class FamilyEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateStr: String,
    val daysRemaining: String,
    val category: String, // Birthday, Anniversary, Trip
    val iconType: String
)

@Entity(tableName = "files")
data class FamilyFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val uploaderName: String,
    val category: FileCategory,
    val sizeText: String,
    val uploadedDate: String
)
