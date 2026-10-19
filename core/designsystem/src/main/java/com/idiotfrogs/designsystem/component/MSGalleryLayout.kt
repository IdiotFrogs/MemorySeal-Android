package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MSGalleryLayout(
    images: List<String>,
    isSeal: Boolean,
    onImageClick: ((Int) -> Unit)? = null,
) {
    if (images.isEmpty()) return

    val imageHeight = if (images.size <= 2) 160.dp else 107.dp

    Column(
        modifier = Modifier
            .width(if (images.size == 1) 142.dp else 284.dp)
            .clip(RoundedCornerShape(16.dp))
            .blur(if (isSeal) 8.dp else 0.dp),
    ) {
        var rowStart = 0

        while (rowStart < images.size) {
            val remaining = images.size - rowStart
            val rowSize = when {
                images.size <= 3 -> images.size
                remaining == 4 -> 2
                else -> minOf(3, remaining)
            }
            val rowIndices = rowStart until rowStart + rowSize

            Row(modifier = Modifier.fillMaxWidth()) {
                for (imageIndex in rowIndices) {
                    val clickModifier = if (!isSeal && onImageClick != null) {
                        Modifier.noRippleClickable { onImageClick(imageIndex) }
                    } else {
                        Modifier
                    }

                    GalleryImage(
                        image = images[imageIndex],
                        modifier = Modifier
                            .weight(1f)
                            .height(imageHeight)
                            .then(clickModifier),
                    )
                }
            }

            rowStart += rowSize
        }
    }
}

@Composable
private fun GalleryImage(
    image: String,
    modifier: Modifier = Modifier,
) {
    GlideImage(
        imageModel = { image },
        modifier = modifier,
        loading = {
            Image(
                painter = painterResource(R.drawable.img_sample),
                contentDescription = "Loading",
                contentScale = ContentScale.Crop,
            )
        },
        failure = {
            Image(
                painter = painterResource(R.drawable.img_sample),
                contentDescription = "Failure",
                contentScale = ContentScale.Crop,
            )
        },
    )
}
