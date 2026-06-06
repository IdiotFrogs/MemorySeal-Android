package com.idiotfrogs.friend.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun FriendListItem(
    nickName: String,
    profileImageUrl: String,
    isMe: Boolean,
    role: TimeCapsuleRole,
    onMoreClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val isHost = role == TimeCapsuleRole.HOST

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .wavyStroke(
                    color = MSTheme.color.greyG5,
                    strokeWidth = 2.dp,
                    cornerRadius = 20.dp,
                    amplitude = 1.dp,
                    spacing = 2.dp,
                    clipContent = true
                ),
            imageModel = { profileImageUrl.takeIf { it.isNotBlank() } ?: R.drawable.img_profile_54 }
        )
        Spacer(Modifier.width(12.dp))
        if (isHost) {
            Row(
                modifier = Modifier
                    .height(30.dp)
                    .wavyStroke(
                        color = MSTheme.color.bgNormal,
                        strokeWidth = 1.dp,
                        cornerRadius = 14.dp,
                        amplitude = 1.dp,
                        spacing = 2.dp,
                        fillColor = MSTheme.color.bgNormal,
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(R.drawable.img_host),
                    contentDescription = null,
                )
                Spacer(Modifier.width(4.dp))
                MSText(
                    text = "방장",
                    color = MSTheme.color.greyG4,
                    fontSize = 12.dp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        if (isMe) {
            Row(
                modifier = Modifier
                    .height(28.dp)
                    .wavyStroke(
                        color = MSTheme.color.primaryLight,
                        strokeWidth = 1.dp,
                        cornerRadius = 14.dp,
                        spacing = 4.dp,
                        fillColor = MSTheme.color.primaryLight,
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MSText(
                    text = "나",
                    color = MSTheme.color.primaryDark,
                    fontSize = 12.dp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        MSText(
            text = nickName,
            color = MSTheme.color.greyG5,
            fontSize = 16.dp,
            fontWeight = FontWeight.Normal,
        )

        Spacer(Modifier.weight(1f))
        onMoreClick?.let {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .noRippleClickable(it),
                painter = painterResource(R.drawable.img_more),
                contentDescription = "멤버 액션",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendListItemPreview() {
    FriendListItem(
        nickName = "민트 네모 수박",
        profileImageUrl = "",
        isMe = true,
        role = TimeCapsuleRole.HOST,
        onMoreClick = null,
    )
}
