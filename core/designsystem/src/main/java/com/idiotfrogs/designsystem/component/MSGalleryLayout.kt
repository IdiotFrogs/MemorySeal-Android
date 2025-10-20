package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import com.idiotfrogs.resource.R

@Composable
fun MSGalleryLayout(images: List<String>, isSeal: Boolean) {
    Box(
        modifier = Modifier
            .width(if (images.size != 1) 284.dp else 142.dp)
            .clip(RoundedCornerShape(16.dp))
            .blur(if (isSeal) 8.dp else 0.dp)
    ) {
        when (images.size) {
            1 -> SingleImage(images[0])
            2 -> RowImages(images, isFirstRow = true)
            3 -> RowImages(images, isFirstRow = false)
            4 -> TwoByTwo(images)
            else -> PatternRowsBigFirst(images)
        }
    }
}

@Composable
fun SingleImage(image: String) {
    GlideImage(
        imageModel = { image },
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
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
        }
    )
}

@Composable
fun RowImages(images: List<String>, isFirstRow: Boolean) {
    val width = when (images.size) {
        1 -> 284.dp
        2 -> 142.dp
        3 -> (284f / 3f).dp
        else -> 0.dp
    }
    val height = if (isFirstRow) 160.dp else 107.dp

    Row(modifier = Modifier.fillMaxWidth()) {
        images.forEach { image ->
            GlideImage(
                imageModel = { image },
                modifier = Modifier
                    .width(width)
                    .height(height),
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
                }
            )
        }
    }
}

@Composable
fun TwoByTwo(images: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        for (row in 0 until 2) {
            RowImages(images.subList(row * 2, row * 2 + 2), isFirstRow = false)
        }
    }
}

@Composable
fun PatternRowsBigFirst(images: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth()) {
        var index = 0
        val total = images.size
        var rowIndex = 0

        while (index < total) {
            val remaining = total - index
            val rowSize = when {
                remaining == 4 -> 2
                remaining >= 3 -> 3
                else -> remaining
            }

            val subList = images.subList(index, index + rowSize)
            RowImages(subList, isFirstRow = false)

            index += rowSize
            rowIndex++
        }
    }
}