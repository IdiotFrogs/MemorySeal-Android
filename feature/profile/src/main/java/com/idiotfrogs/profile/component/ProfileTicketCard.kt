package com.idiotfrogs.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileTicketCard(
    imageUrl: String,
    title: String,
    date: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(
                color = MSTheme.color.white,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .noRippleClickable(onClick = onClick)
    ) {
        GlideImage(
            modifier = Modifier.aspectRatio(1f),
            imageModel = { imageUrl },
        )
        Spacer(modifier = Modifier.height(12.dp))
        MSText(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.dp,
            color = MSTheme.color.greyG5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
        MSText(
            text = date,
            fontWeight = FontWeight.Normal,
            fontSize = 12.dp,
            color = MSTheme.color.greyG3
        )
    }
}

@Preview
@Composable
private fun ProfileTicketCardPreview() {
    ProfileTicketCard(
        imageUrl = "",
        title = "제목입니다.다ㅏㅏ",
        date = "2027.10.24",
        onClick = {}
    )
}