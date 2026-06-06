package com.idiotfrogs.friend.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke

@Composable
fun FriendInviteButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .wavyStroke(
                color = MSTheme.color.primaryNormal,
                strokeWidth = 2.dp,
                amplitude = 1.dp,
                spacing = 3.dp,
                fillColor = MSTheme.color.primaryLight,
            )
            .noRippleClickable(onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(icon),
            contentDescription = "icon",
        )
        Spacer(Modifier.width(4.dp))
        MSText(
            text = text,
            color = MSTheme.color.primaryDark,
        )
    }
}
