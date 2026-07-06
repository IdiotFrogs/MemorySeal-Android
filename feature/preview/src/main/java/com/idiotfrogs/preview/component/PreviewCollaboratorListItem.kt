package com.idiotfrogs.preview.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.preview.PreviewCollaboratorUiModel
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun PreviewCollaboratorListItem(
    collaborator: PreviewCollaboratorUiModel,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(if (isSelected) 80.dp else 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 80.dp else 60.dp)
                .then(
                    if (isSelected) {
                        Modifier.wavyStroke(
                            color = MSTheme.color.primaryNormal,
                            fillColor = MSTheme.color.white,
                            strokeWidth = 3.dp,
                            cornerRadius = 40.dp,
                            amplitude = 1.dp,
                            spacing = 2.dp,
                            contentPadding = 3.dp,
                        )
                    } else {
                        Modifier.padding(3.dp)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            GlideImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MSTheme.color.greyG1),
                imageModel = { collaborator.profileImageUrl },
                loading = {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        painter = painterResource(R.drawable.img_empty_profile),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
                failure = {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        painter = painterResource(R.drawable.img_empty_profile),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
            )
        }

        Spacer(Modifier.height(8.dp))

        MSText(
            modifier = Modifier.fillMaxWidth(),
            text = collaborator.nickname,
            fontSize = 12.dp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = MSTheme.color.greyG5,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
