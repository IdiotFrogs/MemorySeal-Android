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
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke

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
            modifier = Modifier.size(28.dp),
            painter = painterResource(imageRes),
            contentDescription = title,
        )
        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            MSText(
                text = title,
                fontSize = 12.dp,
                color = MSTheme.color.primaryDark,
            )
            MSText(
                text = description,
                fontSize = 10.dp,
                fontWeight = FontWeight.Medium,
                color = MSTheme.color.primaryDark,
            )
        }
        Spacer(Modifier.width(8.dp))
        MSButton(
            onClick = onPreviewClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.greyG5,
            ),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        ) {
            MSText(
                text = "미리보기",
                fontSize = 10.dp,
                color = MSTheme.color.white,
            )
        }
    }
}
