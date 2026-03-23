package com.idiotfrogs.friend.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.resource.R

@Composable
fun FriendListItem(
    nickName: String,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image( // TODO 추 후 AsyncImage 변경 필요
            painter = painterResource(R.drawable.img_profile_32),
            contentDescription = "프로필",
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.width(8.dp))
        MSText(
            text = nickName,
            color = MSTheme.color.greyG5,
            fontSize = 16.dp,
            fontWeight = FontWeight.Normal,
        )

        Spacer(Modifier.weight(1f))
        MSText(
            modifier = Modifier
                .background(
                    color = MSTheme.color.greyG1,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 7.5.dp)
                .noRippleClickable { onReject() },
            text = "거절",
            color = MSTheme.color.greyG4,
            fontSize = 14.dp,
        )
        Spacer(Modifier.width(8.dp))
        MSText(
            modifier = Modifier
                .background(
                    color = MSTheme.color.primaryLight,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 7.5.dp)
                .noRippleClickable { onAccept() },
            text = "수락",
            color = MSTheme.color.primaryDark,
            fontSize = 14.dp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FriendListItemPreview() {
    FriendListItem(
        "nickName",
        onAccept = {},
        onReject = {},
    )
}