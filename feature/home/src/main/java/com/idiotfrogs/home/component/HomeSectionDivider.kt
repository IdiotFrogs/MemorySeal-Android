package com.idiotfrogs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
fun HomeSectionDivider(
    modifier: Modifier = Modifier,
    sectionName: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MSText(
            text = sectionName,
            fontWeight = FontWeight.Bold,
            fontSize = 20.dp,
            color = MSTheme.color.black
        )
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = "ic_chevron_right"
        )
    }
}