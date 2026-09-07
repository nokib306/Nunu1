package com.example.data.model

enum class FamilyRole(val displayName: String, val badgeColorHex: Long) {
    OWNER("Family Owner / Admin", 0xFF1E3A8A),
    SPOUSE("Spouse / Parent", 0xFFD97706),
    PARENT("Parent / Elder", 0xFF7C3AED),
    SIBLING("Brother / Sister", 0xFF059669),
    CHILD("Child", 0xFF0284C7),
    TRUSTED_MEMBER("Family Member", 0xFF475569)
}

enum class LocationShareMode(val label: String, val description: String) {
    CONTINUOUS("Continuously", "Always share location with family"),
    ONE_HOUR("For 1 Hour", "Temporary trip / transit"),
    FIFTEEN_MINUTES("For 15 Minutes", "Quick arrival check"),
    OFF("Do Not Share", "Location paused")
}

enum class MessageType {
    TEXT,
    VOICE,
    PHOTO,
    FILE,
    LOCATION
}

enum class CallType {
    VOICE,
    VIDEO
}

enum class CallState {
    IDLE,
    OUTGOING_RINGING,
    INCOMING_RINGING,
    CONNECTED,
    ENDED
}

enum class TaskStatus(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

enum class FileCategory(val label: String) {
    ALL("All Files"),
    PHOTO("Photos"),
    VIDEO("Videos"),
    DOCUMENT("Documents")
}
