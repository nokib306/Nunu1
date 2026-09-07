package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FamilyRole

@Composable
fun MemberAvatar(
    initials: String,
    bgColorHex: Long,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showOnlineBadge: Boolean = false,
    isOnline: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            color = Color(bgColorHex)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.38f).sp
                )
            }
        }
        if (showOnlineBadge) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.BottomEnd)
            ) {
                Box(
                    modifier = Modifier
                        .size(size * 0.22f)
                        .clip(CircleShape)
                        .background(if (isOnline) Color(0xFF10B981) else Color(0xFF94A3B8))
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun RoleBadge(
    role: FamilyRole,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(role.badgeColorHex).copy(alpha = 0.12f)
    ) {
        Text(
            text = role.displayName,
            color = Color(role.badgeColorHex),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.testTag("role_badge")
        )
    }
}
