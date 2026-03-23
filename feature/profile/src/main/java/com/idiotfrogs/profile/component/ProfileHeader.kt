package com.idiotfrogs.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.idiotfrogs.resource.R

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSetting: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MSTheme.color.white)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable(onClick = onBack),
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "chevron_left",
        )
        MSText(
            text = "프로필",
            fontWeight = FontWeight.Bold,
            fontSize = 14.dp,
            color = MSTheme.color.greyG5
        )
        MSText(
            modifier = Modifier.noRippleClickable(onClick = onSetting),
            text = "설정",
            fontWeight = FontWeight.Medium,
            fontSize = 14.dp,
            color = MSTheme.color.greyG3
        )
    }
}

@Preview
@Composable
private fun ProfileHeaderPreview() {
    ProfileHeader(
        onBack = {},
        onSetting = {}
    )
}
