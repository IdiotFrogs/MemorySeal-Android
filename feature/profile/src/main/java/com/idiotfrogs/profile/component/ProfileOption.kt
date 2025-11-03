package com.idiotfrogs.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme

@Composable
fun ProfileOption(
    modifier: Modifier = Modifier,
    option: String,
    optionColor: Color = MSTheme.color.greyG5,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MSText(
            text = option,
            fontWeight = FontWeight.Normal,
            fontSize = 16.dp,
            color = optionColor
        )
        trailingContent()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ProfileOptionPreview() {
    ProfileOption(
        modifier = Modifier.padding(horizontal = 20.dp),
        option = "앱 버전",
        trailingContent = {
            MSText(
                text = "v0.2",
                fontWeight = FontWeight.Normal,
                fontSize = 16.dp,
                color = MSTheme.color.greyG4
            )
        }
    )
}
