package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FamilyRole
import com.example.data.model.FileCategory
import com.example.data.model.LocationShareMode
import com.example.data.model.MessageType
import com.example.data.model.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FamilyEntity::class,
        MemberEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        PostEntity::class,
        FamilyTaskEntity::class,
        FamilyEventEntity::class,
        FamilyFileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HaqueFamDatabase : RoomDatabase() {

    abstract fun familyDao(): FamilyDao

    companion object {
        @Volatile
        private var INSTANCE: HaqueFamDatabase? = null

        fun getDatabase(context: Context): HaqueFamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HaqueFamDatabase::class.java,
                    "haque_fam_db"
                ).build()
                INSTANCE = instance
                CoroutineScope(Dispatchers.IO).launch {
                    if (instance.familyDao().getMemberById("mem_tariq") == null) {
                        seedInitialData(instance.familyDao())
                    }
                }
                instance
            }
        }

        suspend fun seedInitialData(dao: FamilyDao) {
            // Seed Family Profile
            dao.setFamilyProfile(
                FamilyEntity(
                    id = "FAMILY-7X82K",
                    name = "Haque’sFam",
                    inviteCode = "FAM-7X82K",
                    storageUsedMB = 18432,
                    storageLimitMB = 102400,
                    announcement = "📢 Family dinner & planning for Eid holiday this Friday 8:00 PM at Dad's home!"
                )
            )

            // Seed Members
            val members = listOf(
                MemberEntity(
                    id = "mem_tariq",
                    name = "Tariqul Haque",
                    relation = "Dad",
                    role = FamilyRole.OWNER,
                    phone = "+1 (555) 234-5678",
                    email = "tariqul.haque@family.net",
                    avatarInitial = "TH",
                    avatarBgColorHex = 0xFF1E3A8A,
                    isEmergencyContact = true,
                    batteryPercent = 88,
                    isCharging = false,
                    locationName = "Home Residence",
                    latitude = 23.7937,
                    longitude = 90.4066,
                    locationAccuracyMeters = 10,
                    locationShareMode = LocationShareMode.CONTINUOUS,
                    lastSeen = "Online",
                    isOnline = true
                ),
                MemberEntity(
                    id = "mem_nasreen",
                    name = "Nasreen Haque",
                    relation = "Mom",
                    role = FamilyRole.SPOUSE,
                    phone = "+1 (555) 234-5679",
                    email = "nasreen.haque@family.net",
                    avatarInitial = "NH",
                    avatarBgColorHex = 0xFFD97706,
                    isEmergencyContact = true,
                    batteryPercent = 64,
                    isCharging = false,
                    locationName = "Central Market & Grocery",
                    latitude = 23.7985,
                    longitude = 90.4125,
                    locationAccuracyMeters = 15,
                    locationShareMode = LocationShareMode.CONTINUOUS,
                    lastSeen = "2m ago",
                    isOnline = true
                ),
                MemberEntity(
                    id = "mem_samiul",
                    name = "Samiul Haque",
                    relation = "Brother (Son)",
                    role = FamilyRole.SIBLING,
                    phone = "+1 (555) 345-6789",
                    email = "samiul.haque@campus.edu",
                    avatarInitial = "SH",
                    avatarBgColorHex = 0xFF059669,
                    isEmergencyContact = false,
                    batteryPercent = 42,
                    isCharging = false,
                    locationName = "City University Campus",
                    latitude = 23.7852,
                    longitude = 90.3995,
                    locationAccuracyMeters = 18,
                    locationShareMode = LocationShareMode.ONE_HOUR,
                    lastSeen = "5m ago",
                    isOnline = true
                ),
                MemberEntity(
                    id = "mem_farhana",
                    name = "Farhana Haque",
                    relation = "Sister (Daughter)",
                    role = FamilyRole.SIBLING,
                    phone = "+1 (555) 456-7890",
                    email = "farhana.haque@art.org",
                    avatarInitial = "FH",
                    avatarBgColorHex = 0xFF7C3AED,
                    isEmergencyContact = false,
                    batteryPercent = 95,
                    isCharging = true,
                    locationName = "National Art Center",
                    latitude = 23.8055,
                    longitude = 90.4182,
                    locationAccuracyMeters = 8,
                    locationShareMode = LocationShareMode.CONTINUOUS,
                    lastSeen = "Online",
                    isOnline = true
                ),
                MemberEntity(
                    id = "mem_abdul",
                    name = "Abdul Haque",
                    relation = "Grandfather",
                    role = FamilyRole.PARENT,
                    phone = "+1 (555) 567-8901",
                    email = "abdul.haque@family.net",
                    avatarInitial = "AH",
                    avatarBgColorHex = 0xFF0284C7,
                    isEmergencyContact = true,
                    batteryPercent = 75,
                    isCharging = false,
                    locationName = "Home Residence - Garden",
                    latitude = 23.7938,
                    longitude = 90.4072,
                    locationAccuracyMeters = 12,
                    locationShareMode = LocationShareMode.CONTINUOUS,
                    lastSeen = "10m ago",
                    isOnline = true
                )
            )
            dao.insertMembers(members)

            // Seed Conversations
            val convos = listOf(
                ConversationEntity(
                    id = "convo_family",
                    title = "👨‍👩‍👧‍👦 Entire Haque Family",
                    isGroup = true,
                    memberCount = 5,
                    lastMessage = "Mom: Don't forget to pick up the spices, Samiul!",
                    lastMessageTime = "12:42 PM",
                    unreadCount = 2,
                    avatarInitials = "HF",
                    avatarColorHex = 0xFF1E3A8A
                ),
                ConversationEntity(
                    id = "convo_siblings",
                    title = "✨ Brothers & Sisters",
                    isGroup = true,
                    memberCount = 2,
                    lastMessage = "Farhana: Look at this sketch idea for Dad's card",
                    lastMessageTime = "11:15 AM",
                    unreadCount = 0,
                    avatarInitials = "BS",
                    avatarColorHex = 0xFF7C3AED
                ),
                ConversationEntity(
                    id = "convo_mom",
                    title = "Nasreen Haque (Mom)",
                    isGroup = false,
                    memberCount = 2,
                    lastMessage = "Are you on your way home soon?",
                    lastMessageTime = "1:05 PM",
                    unreadCount = 1,
                    avatarInitials = "NH",
                    avatarColorHex = 0xFFD97706
                ),
                ConversationEntity(
                    id = "convo_samiul",
                    title = "Samiul Haque (Brother)",
                    isGroup = false,
                    memberCount = 2,
                    lastMessage = "Bro, can you send the updated cloud file?",
                    lastMessageTime = "Yesterday",
                    unreadCount = 0,
                    avatarInitials = "SH",
                    avatarColorHex = 0xFF059669
                )
            )
            dao.insertConversations(convos)

            // Seed Messages for Family Group
            val messages = listOf(
                MessageEntity(
                    conversationId = "convo_family",
                    senderId = "mem_tariq",
                    senderName = "Dad",
                    text = "Assalamu Alaikum family! Hope everyone is having a blessed Monday.",
                    type = MessageType.TEXT,
                    timestampFormatted = "9:30 AM",
                    isFromMe = false,
                    reactions = "❤️ 3, 🤲 2"
                ),
                MessageEntity(
                    conversationId = "convo_family",
                    senderId = "mem_abdul",
                    senderName = "Grandpa",
                    text = "Wa Alaikum Assalam. The garden flowers are blooming beautifully today.",
                    type = MessageType.TEXT,
                    timestampFormatted = "9:38 AM",
                    isFromMe = false,
                    reactions = "🌸 4"
                ),
                MessageEntity(
                    conversationId = "convo_family",
                    senderId = "mem_nasreen",
                    senderName = "Mom",
                    text = "Voice message from market (0:18)",
                    type = MessageType.VOICE,
                    voiceDurationSec = 18,
                    timestampFormatted = "12:15 PM",
                    isFromMe = false
                ),
                MessageEntity(
                    conversationId = "convo_family",
                    senderId = "mem_samiul",
                    senderName = "Samiul",
                    text = "I'm just finishing up my computer science lecture. Heading out in 20 minutes!",
                    type = MessageType.TEXT,
                    timestampFormatted = "12:35 PM",
                    isFromMe = true,
                    reactions = "👍 2"
                ),
                MessageEntity(
                    conversationId = "convo_family",
                    senderId = "mem_nasreen",
                    senderName = "Mom",
                    text = "Don't forget to pick up the spices, Samiul!",
                    type = MessageType.TEXT,
                    timestampFormatted = "12:42 PM",
                    isFromMe = false
                )
            )
            messages.forEach { dao.insertMessage(it) }

            // Seed Posts
            val posts = listOf(
                PostEntity(
                    authorId = "mem_tariq",
                    authorName = "Tariqul Haque",
                    authorRelation = "Dad",
                    avatarInitial = "TH",
                    avatarColorHex = 0xFF1E3A8A,
                    content = "Memories from our Cox's Bazar family holiday! The calm waves at sunset were unforgettable. Looking forward to our next family escape together soon. 🌅🌊",
                    memoryBadge = "✨ 1 Year Ago Today",
                    likesCount = 5,
                    isLikedByMe = true,
                    commentsCount = 3,
                    timeAgo = "3 hours ago"
                ),
                PostEntity(
                    authorId = "mem_nasreen",
                    authorName = "Nasreen Haque",
                    authorRelation = "Mom",
                    avatarInitial = "NH",
                    avatarColorHex = 0xFFD97706,
                    content = "Alhamdulillah, Farhana's contemporary oil painting was selected for the National Art Center honors! So incredibly proud of our daughter's talent and dedication! 🎨✨👏",
                    memoryBadge = null,
                    likesCount = 4,
                    isLikedByMe = true,
                    commentsCount = 4,
                    timeAgo = "Yesterday"
                )
            )
            dao.insertPosts(posts)

            // Seed Tasks
            val tasks = listOf(
                FamilyTaskEntity(
                    title = "Pick up grocery spices & dairy from market",
                    assignedToName = "Samiul",
                    status = TaskStatus.IN_PROGRESS,
                    dueDate = "Today, 5:00 PM"
                ),
                FamilyTaskEntity(
                    title = "Pay electricity & broadband utility bills",
                    assignedToName = "Dad",
                    status = TaskStatus.COMPLETED,
                    dueDate = "Sep 5"
                ),
                FamilyTaskEntity(
                    title = "Schedule Grandfather's routine health checkup",
                    assignedToName = "Mom",
                    status = TaskStatus.PENDING,
                    dueDate = "Tomorrow"
                ),
                FamilyTaskEntity(
                    title = "Design invitations for Mom's Birthday dinner",
                    assignedToName = "Farhana",
                    status = TaskStatus.PENDING,
                    dueDate = "This Friday"
                )
            )
            dao.insertTasks(tasks)

            // Seed Events
            val events = listOf(
                FamilyEventEntity(
                    title = "Mom's Birthday Celebration 🎂",
                    dateStr = "Friday, Sep 11",
                    daysRemaining = "4 days away",
                    category = "Birthday",
                    iconType = "cake"
                ),
                FamilyEventEntity(
                    title = "Family Eid Gathering & Feast 🌙",
                    dateStr = "Sunday, Sep 20",
                    daysRemaining = "13 days away",
                    category = "Gathering",
                    iconType = "celebration"
                ),
                FamilyEventEntity(
                    title = "Grandpa's Health & Eye Checkup 🩺",
                    dateStr = "Wednesday, Sep 23",
                    daysRemaining = "16 days away",
                    category = "Medical",
                    iconType = "medical"
                ),
                FamilyEventEntity(
                    title = "Family Autumn Road Trip 🚗",
                    dateStr = "Friday, Oct 2",
                    daysRemaining = "25 days away",
                    category = "Trip",
                    iconType = "trip"
                )
            )
            dao.insertEvents(events)

            // Seed Files
            val files = listOf(
                FamilyFileEntity(
                    fileName = "Passport_Copies_Family_Vault.pdf",
                    uploaderName = "Tariqul (Dad)",
                    category = FileCategory.DOCUMENT,
                    sizeText = "4.8 MB",
                    uploadedDate = "Aug 28, 2026"
                ),
                FamilyFileEntity(
                    fileName = "Family_Health_Insurance_Policy.pdf",
                    uploaderName = "Tariqul (Dad)",
                    category = FileCategory.DOCUMENT,
                    sizeText = "2.3 MB",
                    uploadedDate = "Jul 15, 2026"
                ),
                FamilyFileEntity(
                    fileName = "CoxsBazar_Family_Reunion_FullRes.zip",
                    uploaderName = "Samiul",
                    category = FileCategory.ALL,
                    sizeText = "340 MB",
                    uploadedDate = "Sep 01, 2026"
                ),
                FamilyFileEntity(
                    fileName = "Grandpa_80th_Celebration_Video.mp4",
                    uploaderName = "Farhana",
                    category = FileCategory.VIDEO,
                    sizeText = "128 MB",
                    uploadedDate = "May 20, 2026"
                ),
                FamilyFileEntity(
                    fileName = "Farhana_Art_Exhibition_Brochure.pdf",
                    uploaderName = "Farhana",
                    category = FileCategory.DOCUMENT,
                    sizeText = "5.1 MB",
                    uploadedDate = "Sep 04, 2026"
                )
            )
            dao.insertFiles(files)
        }
    }
}
