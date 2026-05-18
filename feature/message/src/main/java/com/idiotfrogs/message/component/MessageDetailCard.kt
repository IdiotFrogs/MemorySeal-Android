package com.idiotfrogs.message.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R

@Composable
fun MessageDetailCard(
    description: String,
    onEditClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .wavyStroke(
                color = MSTheme.color.white,
                fillColor = MSTheme.color.white,
                strokeWidth = 4.dp,
                contentPadding = 16.dp,
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .noRippleClickable(onClick = onEditClick),
                painter = painterResource(R.drawable.img_message_edit),
                contentDescription = "메시지 수정",
            )
            Spacer(Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .noRippleClickable(onClick = onCloseClick),
                painter = painterResource(R.drawable.img_cancel),
                contentDescription = "닫기",
            )
        }

        Spacer(Modifier.height(16.dp))

        MSText(
            text = description,
            fontWeight = FontWeight.Normal,
            color = MSTheme.color.greyG5,
        )
    }
}

@Preview
@Composable
private fun MessageDetailCardPreview() {
    MessageDetailCard(
        description = "별거 아닌 대화,\n별거 아닌 하루가 이렇게 오래 기억에 남을 줄 몰랐어.",
        onEditClick = {},
        onCloseClick = {},
    )
}
