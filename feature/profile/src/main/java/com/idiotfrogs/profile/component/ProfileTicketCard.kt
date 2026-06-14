package com.idiotfrogs.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import com.idiotfrogs.designsystem.util.wavyStroke
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileTicketCard(
    imageUrl: String,
    title: String,
    date: String,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.noRippleClickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .wavyStroke(
                    amplitude = 1.dp,
                    spacing = 2.dp,
                    color = MSTheme.color.greyG5,
                    fillColor = MSTheme.color.primaryNormal
                )
        )
        Column(
            modifier = Modifier
                .offset(y = (-5).dp)
                .fillMaxWidth()
                .height(133.dp)
                .wavyStroke(
                    amplitude = 1.dp,
                    spacing = 2.dp,
                    color = MSTheme.color.greyG5,
                    fillColor = MSTheme.color.white
                )
                .padding(12.dp)
        ) {
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
                fontSize = 14.dp,
                color = MSTheme.color.greyG3
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileTicketCardPreview() {
    ProfileTicketCard(
        imageUrl = "",
        title = "제목입니다.다ㅏㅏ",
        date = "2027.10.24",
        onClick = {}
    )
}