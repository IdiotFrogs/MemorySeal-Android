package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.resource.R
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun MSToast(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .hazeEffect(hazeState) { blurRadius = 22.dp }
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color(0x10505050),
                spotColor = Color(0x10505050)
            )
            .background(Color(0x280B0B0B))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) { content() }
}

@Preview(showBackground = true)
@Composable
fun MSToastPreview() {
    val hazeState = rememberHazeState()

    MSToast(hazeState) {
        Image(
            painter = painterResource(R.drawable.img_friend_accept),
            contentDescription = "알림",
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        MSText(
            text = "참여 요청이 수락되었습니다.",
            color = MSTheme.color.white
        )
    }
}