package com.idiotfrogs.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.component.MSCalender
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTextField
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.keyboardAutoScroll
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.rememberPickerState
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun CreateScreen(modifier: Modifier = Modifier) {
    val navigator = LocalComposeMSNavigator.current

    val titleTextFieldState = rememberTextFieldState()
    val contentTextFieldState = rememberTextFieldState()
    val scrollState = rememberScrollState()
    val (imageUri, launchImagePicker) = rememberPickerState()
    val enabled = titleTextFieldState.text.isNotEmpty() && contentTextFieldState.text.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
            .keyboardAutoScroll(scrollState)
    ) {
        MSDetailHeader(
            title = "타임 티켓 생성하기",
            paddingValues = PaddingValues(vertical = 16.dp),
            navigateToBack = { navigator.popBackStack() }
        )
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .weight(1f),
        ) {
            MSText(
                text = "사진",
                fontSize = 12.dp,
                fontWeight = FontWeight.Medium,
                color = MSTheme.color.greyG5
            )
            Spacer(Modifier.height(8.dp))
            imageUri?.let {
                GlideImage(
                    imageModel = { imageUri },
                    modifier = Modifier
                        .noRippleClickable { launchImagePicker() }
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } ?: Image(
                modifier = Modifier
                    .border(1.dp, MSTheme.color.greyG2, RoundedCornerShape(12.dp))
                    .padding(48.dp)
                    .noRippleClickable { launchImagePicker() },
                painter = painterResource(R.drawable.ic_photo),
                contentDescription = "사진"
            )
            Spacer(Modifier.height(16.dp))
            MSText(
                text = "제목",
                fontSize = 12.dp,
                fontWeight = FontWeight.Medium,
                color = MSTheme.color.greyG5
            )
            Spacer(Modifier.height(8.dp))
            MSTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = titleTextFieldState,
                hint = "제목을 입력해주세요.",
            )
            Spacer(Modifier.height(16.dp))
            MSText(
                text = "간단한 설명",
                fontSize = 12.dp,
                fontWeight = FontWeight.Medium,
                color = MSTheme.color.greyG5
            )
            Spacer(Modifier.height(8.dp))
            MSTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = contentTextFieldState,
                hint = "설명을 입력해주세요.",
            )
            Spacer(Modifier.height(16.dp))
            MSText(
                text = "오픈 날짜",
                fontSize = 12.dp,
                fontWeight = FontWeight.Medium,
                color = MSTheme.color.greyG5
            )
            Spacer(Modifier.height(8.dp))
            MSCalender {  }
            Spacer(Modifier.height(24.dp))
        }
        MSButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.primaryNormal,
                disabledContainerColor = MSTheme.color.primaryLight
            ),
            onClick = {}
        ) {
            MSText(
                text = "오픈 날짜",
                fontSize = 12.dp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MSTheme.color.white else MSTheme.color.greyG3
            )
        }
    }
}

@Preview
@Composable
fun CreateScreenPreview() {
    CreateScreen()
}