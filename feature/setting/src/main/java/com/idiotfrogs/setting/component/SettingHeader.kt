package com.idiotfrogs.setting.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
fun SettingHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MSTheme.color.white)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable(onClick = onBack),
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "chevron_left",
        )
        MSText(
            modifier = Modifier.align(Alignment.Center),
            text = "설정",
            fontWeight = FontWeight.Bold,
            fontSize = 20.dp,
            color = MSTheme.color.greyG5
        )
    }
}

@Preview
@Composable
private fun SettingHeaderPreview() {
    SettingHeader(onBack = {})
}