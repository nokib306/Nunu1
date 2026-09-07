package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LocationShareMode
import com.example.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {

    @Query("SELECT * FROM family_profile LIMIT 1")
    fun getFamilyProfile(): Flow<FamilyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFamilyProfile(family: FamilyEntity)

    @Query("UPDATE family_profile SET announcement = :announcement WHERE id = :familyId")
    suspend fun updateAnnouncement(familyId: String, announcement: String)

    @Query("SELECT * FROM members ORDER BY role ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: String): MemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity)

    @Query("UPDATE members SET locationShareMode = :mode WHERE id = :memberId")
    suspend fun updateLocationShareMode(memberId: String, mode: LocationShareMode)

    @Query("UPDATE members SET latitude = :lat, longitude = :lng, locationAccuracyMeters = :accuracy, locationName = :locationName, lastSeen = 'Just now' WHERE id = :memberId")
    suspend fun updateMemberLocation(memberId: String, lat: Double, lng: Double, accuracy: Float, locationName: String)

    @Query("UPDATE members SET batteryPercent = :battery WHERE id = :memberId")
    suspend fun updateBattery(memberId: String, battery: Int)

    @Query("SELECT * FROM conversations")
    fun getConversations(): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Query("SELECT * FROM messages WHERE conversationId = :convoId ORDER BY id ASC")
    fun getMessagesForConversation(convoId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE messages SET isStarred = :isStarred WHERE id = :id")
    suspend fun toggleStarMessage(id: Long, isStarred: Boolean)

    @Query("UPDATE messages SET reactions = :reactions WHERE id = :id")
    suspend fun updateReactions(id: Long, reactions: String)

    @Query("UPDATE conversations SET lastMessage = :lastMsg, lastMessageTime = :time WHERE id = :convoId")
    suspend fun updateConversationLastMessage(convoId: String, lastMsg: String, time: String)

    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("UPDATE posts SET isLikedByMe = :isLiked, likesCount = :newCount WHERE id = :postId")
    suspend fun updatePostLike(postId: Long, isLiked: Boolean, newCount: Int)

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks(): Flow<List<FamilyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: FamilyTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<FamilyTaskEntity>)

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: TaskStatus)

    @Query("SELECT * FROM events ORDER BY id ASC")
    fun getAllEvents(): Flow<List<FamilyEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<FamilyEventEntity>)

    @Query("SELECT * FROM files ORDER BY id DESC")
    fun getAllFiles(): Flow<List<FamilyFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FamilyFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<FamilyFileEntity>)
}
