package com.idiotfrogs.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    nickname: String,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(
                color = MSTheme.color.white,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        imageUrl?.let {
            GlideImage(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape),
                imageModel = { it }
            )
        } ?: run {
            Image(
                modifier = Modifier.size(54.dp),
                painter = painterResource(R.drawable.img_profile_54),
                contentDescription = "empty_profile"
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        MSText(
            text = nickname,
            fontWeight = FontWeight.Bold,
            fontSize = 16.dp,
            color = MSTheme.color.greyG5
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .background(
                    color = MSTheme.color.greyG1,
                    shape = RoundedCornerShape(29.dp)
                )
                .padding(
                    top = 8.dp, bottom = 8.dp,
                    start = 8.dp, end = 10.dp
                )
                .noRippleClickable(onClick = onEditClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "edit",
                tint = MSTheme.color.greyG3
            )
            Spacer(modifier = Modifier.width(2.dp))
            MSText(
                text = "프로필 수정",
                fontWeight = FontWeight.Medium,
                fontSize = 12.dp,
                color = MSTheme.color.greyG3
            )
        }
    }
}

@Preview
@Composable
private fun ProfileCardPreview() {
    ProfileCard(
        nickname = "용감한사자처럼",
        imageUrl = null,
        onEditClick = {}
    )
}