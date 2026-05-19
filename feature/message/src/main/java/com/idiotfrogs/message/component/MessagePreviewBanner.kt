package com.idiotfrogs.message.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R

@Composable
fun MessagePreviewBanner(
    imageRes: Int,
    title: String,
    description: String,
    onPreviewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wavyStroke(
                color = MSTheme.color.primaryLight,
                fillColor = MSTheme.color.primaryLight,
                contentPadding = 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(imageRes),
            contentDescription = title,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            MSText(
                text = title,
                fontSize = 16.dp,
                color = MSTheme.color.primaryDark,
            )
            MSText(
                text = description,
                fontSize = 12.dp,
                fontWeight = FontWeight.Medium,
                color = MSTheme.color.primaryDark,
            )
        }
        Spacer(Modifier.width(12.dp))
        MSButton(
            onClick = onPreviewClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.greyG5,
            ),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 9.dp),
        ) {
            MSText(
                text = "미리보기",
                fontSize = 12.dp,
                color = MSTheme.color.white,
            )
        }
    }
}

@Preview
@Composable
private fun MessagePreviewBannerPreview() {
    MessagePreviewBanner(
        imageRes = R.drawable.img_message_banner,
        title = "메시지",
        description = "메시지로 추억을 남겨보세요.",
        onPreviewClick = {},
    )
}