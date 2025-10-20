package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme

@Composable
fun MSMessageItem(
    type: MessageType,
    text: String = "",
    imageList: List<String> = emptyList(),
    isSeal: Boolean = false,
) {
    if (type == MessageType.TEXT) {
        MSText(
            modifier = Modifier
                .background(
                    color = MSTheme.color.primaryLight,
                    shape = RoundedCornerShape(16.dp)
                )
                .blur(if (isSeal) 8.dp else 0.dp)
                .padding(16.dp),
            text = text,
            fontSize = 16.dp,
            fontWeight = FontWeight.Normal
        )
    } else {
        MSGalleryLayout(imageList, isSeal)
    }
}



@Preview
@Composable
fun MSMessageItemPreview() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MSMessageItem(
            type = MessageType.TEXT,
            text = "별거 아닌 대화, 별거 아닌 하루가 이렇게 기억에 남을 줄이야. 고마워, 다음에도 함께하자."
        )
        MSMessageItem(
            type = MessageType.TEXT,
            text = "별거 아닌 대화, 별거 아닌 하루가 이렇게 기억에 남을 줄이야. 고마워, 다음에도 함께하자.",
            isSeal = true
        )
        MSMessageItem(
            type = MessageType.PHOTO,
            imageList = listOf("", "")
        )
        MSMessageItem(
            type = MessageType.PHOTO,
            isSeal = true,
            imageList = listOf("")
        )
        MSMessageItem(
            type = MessageType.PHOTO,
            imageList = listOf("", "", "")
        )
        MSMessageItem(
            type = MessageType.PHOTO,
            isSeal = true,
            imageList = listOf("", "", "", "")
        )
        MSMessageItem(
            type = MessageType.PHOTO,
            imageList = listOf("", "", "", "", "")
        )
        MSMessageItem(
            type = MessageType.PHOTO,
            isSeal = true,
            imageList = listOf("", "", "", "", "", "")
        )
        MSMessageItem(
            type = MessageType.PHOTO,
            imageList = listOf("", "", "", "", "", "", "")
        )
    }
}

enum class MessageType {
    TEXT, PHOTO
}