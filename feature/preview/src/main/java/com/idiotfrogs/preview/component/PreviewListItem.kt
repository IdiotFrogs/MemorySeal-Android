package com.idiotfrogs.preview.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSGalleryLayout
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.model.timecapsule.CapsuleContentsData
import com.idiotfrogs.model.timecapsule.TimeCapsuleContentResponseData
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun PreviewListItem(
    contentGroup: TimeCapsuleContentResponseData,
    content: CapsuleContentsData,
    isMine: Boolean,
    showAuthor: Boolean,
    modifier: Modifier = Modifier,
) {
    val message = content.content?.takeIf { it.isNotBlank() }
    val images = content.attachedFileUrls.orEmpty().takeIf { it.isNotEmpty() }

    if (isMine) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
        ) {
            message?.let { text ->
                MSText(
                    modifier = Modifier
                        .widthIn(max = 284.dp)
                        .background(
                            color = MSTheme.color.primaryLight,
                            shape = RoundedCornerShape(20.dp),
                        )
                        .padding(16.dp),
                    text = text,
                    fontSize = 16.dp,
                    fontWeight = FontWeight.Normal,
                    color = MSTheme.color.greyG5,
                )
            }

            images?.let { imageUrls ->
                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                }

                MSGalleryLayout(
                    images = imageUrls,
                    isSeal = false,
                )
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            if (showAuthor) {
                GlideImage(
                    modifier = Modifier
                        .padding(top = 22.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MSTheme.color.greyG1),
                    imageModel = { contentGroup.profileImageUrl },
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
            } else {
                Spacer(Modifier.size(24.dp))
            }

            Spacer(Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                if (showAuthor) {
                    MSText(
                        text = contentGroup.nickname,
                        fontSize = 10.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.greyG3,
                    )

                    Spacer(Modifier.height(8.dp))
                }

                message?.let { text ->
                    MSText(
                        modifier = Modifier
                            .widthIn(max = 284.dp)
                            .background(
                                color = MSTheme.color.bgNormal,
                                shape = RoundedCornerShape(20.dp),
                            )
                            .padding(16.dp),
                        text = text,
                        fontSize = 16.dp,
                        fontWeight = FontWeight.Normal,
                        color = MSTheme.color.greyG5,
                    )
                }

                images?.let { imageUrls ->
                    if (message != null) {
                        Spacer(Modifier.height(8.dp))
                    }

                    MSGalleryLayout(
                        images = imageUrls,
                        isSeal = false,
                    )
                }
            }
        }
    }
}
