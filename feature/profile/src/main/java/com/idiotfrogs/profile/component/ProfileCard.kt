package com.idiotfrogs.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
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
import com.idiotfrogs.designsystem.component.button.MSButton
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
    Column(
        modifier = modifier.background(color = MSTheme.color.white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        imageUrl?.let {
            GlideImage(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                imageModel = { it }
            )
        } ?: run {
            Image(
                modifier = Modifier.size(width = 78.dp, height = 80.dp),
                painter = painterResource(R.drawable.img_empty_profile),
                contentDescription = "empty_profile"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        MSText(
            text = nickname,
            fontWeight = FontWeight.Bold,
            fontSize = 24.dp,
            color = MSTheme.color.greyG5
        )
        Spacer(modifier = Modifier.height(20.dp))
        MSButton(
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.greyG5,
                disabledContainerColor = MSTheme.color.greyG5
            ),
            pressColors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.greyG5,
                disabledContainerColor = MSTheme.color.greyG5
            ),
            onClick = onEditClick
        ) {
            MSText(
                text = "프로필 수정",
                fontWeight = FontWeight.Bold,
                fontSize = 12.dp,
                color = MSTheme.color.white
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