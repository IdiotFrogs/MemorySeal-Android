package com.idiotfrogs.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.resource.R

@Composable
fun MembarListItem(
    nickName: String,
    modifier: Modifier = Modifier,
    isMembar: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image( // TODO 추 후 AsyncImage 변경 필요
            painter = painterResource(R.drawable.img_profile),
            contentDescription = "프로필"
        )
        Spacer(Modifier.width(8.dp))
        MSText(
            text = nickName,
            color = MSTheme.color.greyG5,
            fontSize = 16.dp,
            fontWeight = FontWeight.Normal,
        )

        if (!isMembar) {
            Spacer(Modifier.weight(1f))
            MSText(
                modifier = Modifier
                    .background(
                        color = MSTheme.color.greyG5.copy(0.1f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(5.dp),
                text = "방장",
                color = MSTheme.color.greyG5,
                fontSize = 12.dp,
            )
        }
    }
}