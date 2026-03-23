package com.idiotfrogs.setting.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    }
}

@Preview
@Composable
private fun SettingHeaderPreview() {
    SettingHeader(onBack = {})
}