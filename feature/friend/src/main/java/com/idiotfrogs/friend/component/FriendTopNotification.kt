package com.idiotfrogs.friend.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.friend.FriendScreenActionState
import com.idiotfrogs.resource.R

@Composable
fun FriendTopNotification(
    action: FriendScreenActionState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color(0x50505029),
                spotColor = Color(0x50505029)
            )
            .background(
                color = MSTheme.color.white,
                shape = CircleShape
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                if (action == FriendScreenActionState.ACCEPT || action == FriendScreenActionState.COPY) R.drawable.img_friend_accept
                else R.drawable.img_friend_reject
            ),
            contentDescription = "알림",
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        MSText(
            text = when (action) {
                FriendScreenActionState.ACCEPT -> "참여 요청이 수락되었습니다."
                FriendScreenActionState.COPY -> "참여 코드 복사되었습니다."
                else -> "참여 요청이 거절되었습니다."
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FriendTopNotificationPreview() {
    FriendTopNotification(FriendScreenActionState.IDLE)
}