package com.idiotfrogs.setting.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.resource.R

sealed interface SettingType {
    data class Text(val content: String) : SettingType
    data object Button : SettingType
}

@Composable
fun SettingItem(
    settingType: SettingType,
    title: String,
    titleColor: Color = MSTheme.color.greyG5,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MSTheme.color.white)
            .then(
                if (onClick != null) {
                    Modifier.noRippleClickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 4.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MSText(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 16.dp,
            color = titleColor
        )
        when (settingType) {
            is SettingType.Text -> {
                MSText(
                    text = settingType.content,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG4
                )
            }
            SettingType.Button -> {
                Image(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "chevron_right"
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingItemPreview() {
    SettingItem(
        settingType = SettingType.Button,
        title = "로그아웃"
    )
}