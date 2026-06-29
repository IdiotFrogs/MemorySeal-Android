package com.idiotfrogs.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.resource.R

@Composable
fun ProfileHeader(
    isChanged: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .noRippleClickable(onBack),
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "chevron left"
        )
        MSText(
            modifier = Modifier.align(Alignment.Center),
            text = "프로필 수정",
            fontWeight = FontWeight.Bold,
            fontSize = 20.dp,
            color = MSTheme.color.black
        )
        MSButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(32.dp),
            cornerRadius = 8.dp,
            contentPadding = PaddingValues(horizontal = 1.dp),
            enabled = isChanged,
            onClick = onSave
        ) {
            MSText(
                text = "저장",
                color = if (isChanged) MSTheme.color.primaryDark else Color(0xFF84B591),
                fontSize = 14.dp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun EditProfileHeaderPreview() {
    var isChanged by remember { mutableStateOf(false) }
    ProfileHeader(
        modifier = Modifier.padding(horizontal = 20.dp),
        isChanged = isChanged,
        onBack = { },
        onSave = { isChanged = !isChanged }
    )
}
