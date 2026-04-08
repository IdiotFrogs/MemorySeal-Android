package com.idiotfrogs.friend.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSToast
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.friend.FriendScreenActionState
import com.idiotfrogs.resource.R
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun FriendTopNotification(
    hazeState: HazeState,
    action: FriendScreenActionState,
    modifier: Modifier = Modifier,
) {
    MSToast(
        hazeState = hazeState,
        modifier = modifier
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
            },
            color = MSTheme.color.white
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FriendTopNotificationPreview() {
    val hazeState = rememberHazeState()

    FriendTopNotification(
        hazeState = hazeState,
        action = FriendScreenActionState.IDLE,
    )
}