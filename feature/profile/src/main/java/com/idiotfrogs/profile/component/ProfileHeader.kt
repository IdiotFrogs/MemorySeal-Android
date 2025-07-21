package com.idiotfrogs.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    isChanged: Boolean,
    onSave: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "chevron left"
        )
        MSText(
            text = "프로필",
            fontWeight = FontWeight.Bold,
            fontSize = 14.dp,
            color = MSTheme.color.black
        )
        MSText(
            modifier = Modifier.noRippleClickable(onSave),
            text = "저장",
            fontWeight = FontWeight.Bold,
            fontSize = 14.dp,
            color = if (isChanged) MSTheme.color.primaryNormal else MSTheme.color.greyG2,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ProfileHeaderPreview() {
    var isChanged by remember { mutableStateOf(false) }
    ProfileHeader(
        modifier = Modifier.padding(horizontal = 20.dp),
        isChanged = isChanged,
        onSave = { isChanged = !isChanged }
    )
}
