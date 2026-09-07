# Haque’sFam — Technical Architecture & Product Specification

## 1. System Architecture
Haque’sFam is designed as a private, family-only ecosystem. In production, it follows a secure client-server model:
- **Mobile Client**: Android (Kotlin, Jetpack Compose, Coroutines, Flow, Room Local Cache & Offline-first Engine).
- **API Gateway & Services**: Modular microservices (Auth, Family, Messaging, Storage, Location, Signaling).
- **Real-Time Communication**: WebSocket gateway for real-time presence, typing indicators, read receipts, and location updates; WebRTC with STUN/TURN for peer-to-peer encrypted voice and video calls.
- **Object Storage**: S3/GCS-compatible chunked & resumable blob storage for photos, 4K family videos, and documents with strictly enforced family quotas (e.g., 100 GB tier).

```
   ┌─────────────────────────────────────────────────────────┐
   │             Android Client (Jetpack Compose)            │
   │  ┌───────────────────────┐   ┌───────────────────────┐  │
   │  │  Presentation (MVVM)  │   │   Room Offline Cache  │  │
   │  └───────────┬───────────┘   └───────────▲───────────┘  │
   └──────────────┼───────────────────────────┼──────────────┘
                  │ HTTPS / WSS / WebRTC      │ Sync
                  ▼                           ▼
   ┌─────────────────────────────────────────────────────────┐
   │               Family Backend API Gateway                │
   ├─────────────┬─────────────┬─────────────┬───────────────┤
   │  Auth &     │ Messaging   │ Location    │ Media/Cloud   │
   │  Family Svc │ Service     │ Service     │ Storage Svc   │
   │  (RBAC)     │ (WebSocket) │ (Geofence)  │ (Chunked S3)  │
   └─────────────┴─────────────┴─────────────┴───────────────┘
```

## 2. Database Schema (Entities & Relationships)
- `FamilyEntity`: `id` (PK), `name`, `inviteCode`, `ownerId`, `storageUsedBytes`, `storageLimitBytes`, `createdAt`
- `MemberEntity`: `id` (PK), `familyId` (FK), `name`, `role` (OWNER, SPOUSE, PARENT, SIBLING, CHILD), `phone`, `avatarUrl`, `isEmergencyContact`, `batteryPercent`, `currentLocationName`, `latitude`, `longitude`, `locationSharingMode`, `lastSeen`
- `MessageEntity`: `id` (PK), `familyId` (FK), `senderId` (FK), `groupId`, `recipientId`, `text`, `type` (TEXT, VOICE, IMAGE, FILE, LOCATION), `mediaUrl`, `voiceDurationSec`, `reactionsJson`, `isStarred`, `timestamp`, `readStatus`
- `PostEntity`: `id` (PK), `familyId` (FK), `authorId` (FK), `content`, `imageUrl`, `likesCount`, `isLikedByMe`, `timestamp`
- `PostCommentEntity`: `id` (PK), `postId` (FK), `authorId` (FK), `text`, `timestamp`
- `MediaFileEntity`: `id` (PK), `familyId` (FK), `uploaderId` (FK), `fileName`, `fileSizeBytes`, `mimeType`, `category` (PHOTO, VIDEO, DOCUMENT), `albumName`, `timestamp`
- `FamilyEventEntity`: `id` (PK), `familyId` (FK), `title`, `dateTimestamp`, `category` (BIRTHDAY, ANNIVERSARY, GATHERING, TRIP), `reminderEnabled`
- `FamilyTaskEntity`: `id` (PK), `familyId` (FK), `title`, `assignedMemberId` (FK), `status` (PENDING, IN_PROGRESS, COMPLETED), `dueDate`

## 3. API Specification & Real-Time Gateway
- `POST /api/v1/auth/verify-otp` -> Issues short-lived access JWT + hardware-bound refresh token.
- `GET /api/v1/family/{familyId}` -> Returns family metadata, members, and quota.
- `POST /api/v1/family/invite/verify` -> Validates invitation token before approval.
- `WS /ws/v1/family/{familyId}/stream` -> Bidirectional real-time stream:
  - `LOCATION_UPDATE`: `{ memberId, lat, lng, battery, accuracy, timestamp }`
  - `TYPING_INDICATOR`: `{ conversationId, memberId, isTyping }`
  - `MESSAGE_SENT`: `{ id, senderId, text, type, metadata }`
  - `CALL_SIGNAL`: `{ callId, sdpOffer, sdpAnswer, iceCandidate, mode }`

## 4. Permission Model & Security
- Strict Family-ID isolation: All queries enforce `WHERE family_id = :familyId`.
- Role-based permissions:
  - OWNER / ADMIN: Invite members, modify quotas, remove members, set emergency contacts.
  - MEMBERS: Post, chat, upload files within family limits, control personal location sharing.
- Location privacy: User decides whether location is shared `ALWAYS`, `1_HOUR`, `15_MINS`, or `NEVER`. Admin cannot override without explicit consent.
- Zero public exposure: No unauthenticated endpoints, no external profile discovery.
