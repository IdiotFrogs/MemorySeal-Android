package com.idiotfrogs.message.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke

@Composable
fun MessageSettingListItem(
    title: String,
    description: String,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isDeleteMode) {
            MessageCheckBox(
                isSelected = isSelected,
                onClick = onClick,
            )
            Spacer(Modifier.width(16.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .wavyStroke(
                    color = if (isSelected) {
                        MSTheme.color.primaryNormal
                    } else {
                        MSTheme.color.bgNormal
                    },
                    fillColor = MSTheme.color.white,
                    strokeWidth = 2.dp,
                    cornerRadius = 12.dp,
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            MSText(
                text = title,
                color = MSTheme.color.greyG5,
            )
            Spacer(Modifier.height(4.dp))
            MSText(
                text = description,
                fontWeight = FontWeight.Normal,
                color = MSTheme.color.greyG4,
            )
        }
    }
}

@Preview
@Composable
private fun MessageSettingListItemPreview() {
    MessageSettingListItem(
        title = "메시지 1",
        description = "벌써 시간이 이렇게 흘렀네...",
        isDeleteMode = true,
        isSelected = true,
        onClick = {},
    )
}
