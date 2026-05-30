package com.idiotfrogs.message.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.resource.R

@Composable
fun MessageCheckBox(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    unselectedBorderColor: Color = MSTheme.color.greyG5,
) {
    Image(
        modifier = modifier
            .size(24.dp)
            .noRippleClickable(onClick = onClick),
        painter = painterResource(
            if (isSelected) {
                R.drawable.img_message_checkbox_active
            } else {
                R.drawable.img_message_checkbox_inactive
            },
        ),
        colorFilter = if (isSelected) null else ColorFilter.tint(unselectedBorderColor),
        contentDescription = "checkbox",
    )
}

@Preview(showBackground = true)
@Composable
private fun MessageCheckBoxPreview() {
    MessageCheckBox(
        isSelected = true,
        onClick = {},
    )
}
