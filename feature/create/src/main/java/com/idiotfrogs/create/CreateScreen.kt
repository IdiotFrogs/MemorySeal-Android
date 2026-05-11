package com.idiotfrogs.create

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.component.MSCalender
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTextField
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DrawType
import com.idiotfrogs.designsystem.util.keyboardAutoScroll
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.rememberKeyboardVisibility
import com.idiotfrogs.designsystem.util.rememberPickerState
import com.idiotfrogs.designsystem.util.wavyBackground
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.extension.toFile
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes.*
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.todayIn
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun CreateRoute(
    viewModel: CreateViewModel = hiltViewModel(),
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            is CreateSideEffect.NavigateToDetail -> {
                navigator.popBackStack()
                navigator.navigate(Detail(event.response.id))
            }
            CreateSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    when (uiState) {
        UiState.Init -> Unit // 화면 로딩 로직
        is UiState.Success -> {
            CreateScreen(
                onAction = viewModel::onAction
            )
        }
        is UiState.Error -> Unit
    }
}


@OptIn(ExperimentalTime::class)
@Composable
private fun CreateScreen(
    modifier: Modifier = Modifier,
    onAction: (CreateAction) -> Unit
) {
    val context = LocalContext.current
    val titleTextFieldState = rememberTextFieldState()
    val contentTextFieldState = rememberTextFieldState()
    val scrollState = rememberScrollState()
    val (imageUri, launchImagePicker) = rememberPickerState()
    val isShowKeyboard = rememberKeyboardVisibility()

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).atTime(0, 0, 0, 0)
    val selectedDate = remember { mutableStateOf(today) }

    val enabled = titleTextFieldState.text.isNotEmpty() && imageUri != null

    val buttonHorizontalPadding by animateDpAsState(if (isShowKeyboard) 0.dp else 20.dp)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding()
            .keyboardAutoScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            MSDetailHeader(
                title = "타임 티켓 생성",
                paddingValues = PaddingValues(vertical = 16.dp),
                navigateToBack = { onAction(CreateAction.NavigateToBack) }
            )
            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(state = scrollState)
                    .weight(1f),
            ) {
                MSText(
                    modifier = Modifier.padding(start = 6.dp),
                    text = "사진",
                    fontSize = 12.dp,
                    fontWeight = FontWeight.Medium,
                    color = MSTheme.color.greyG3
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .noRippleClickable { launchImagePicker() }
                        .wavyStroke(
                            color = if (imageUri != null) MSTheme.color.greyG5 else MSTheme.color.greyG1,
                            fillColor = null,
                            strokeWidth = 3.dp,
                            cornerRadius = 12.dp,
                            contentPadding = 0.dp,
                            clipContent = true
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    imageUri?.let {
                        GlideImage(
                            imageModel = { imageUri },
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(12.dp)),
                        )
                    } ?: Image(
                        painter = painterResource(R.drawable.ic_add_create),
                        contentDescription = "사진 추가",
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(Modifier.height(16.dp))
                MSText(
                    modifier = Modifier.padding(start = 6.dp),
                    text = "제목",
                    fontSize = 12.dp,
                    fontWeight = FontWeight.Medium,
                    color = MSTheme.color.greyG3
                )
                Spacer(Modifier.height(8.dp))
                MSTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = titleTextFieldState,
                    hint = "제목을 입력해주세요.",
                )
                Spacer(Modifier.height(16.dp))
                MSText(
                    modifier = Modifier.padding(start = 6.dp),
                    text = "간단한 설명",
                    fontSize = 12.dp,
                    fontWeight = FontWeight.Medium,
                    color = MSTheme.color.greyG3
                )
                Spacer(Modifier.height(8.dp))
                MSTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = contentTextFieldState,
                    hint = "설명을 입력해주세요.",
                )
                Spacer(Modifier.height(16.dp))
                MSText(
                    modifier = Modifier.padding(start = 6.dp),
                    text = "오픈 날짜",
                    fontSize = 12.dp,
                    fontWeight = FontWeight.Medium,
                    color = MSTheme.color.greyG3
                )
                Spacer(Modifier.height(8.dp))
                MSCalender { selectedDate.value = it }
                Spacer(Modifier.height(24.dp))
            }
        }
        MSButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = buttonHorizontalPadding)
                .wavyBackground(
                    color = if (enabled) MSTheme.color.primaryNormal else MSTheme.color.primaryLight,
                    drawType = if (isShowKeyboard) DrawType.TOP else DrawType.ALL,
                    contentPadding = if (isShowKeyboard) 0.dp else 5.dp,
                    clipContent = true,
                ),
            enabled = enabled,
            isRounded = !isShowKeyboard,
            colors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.primaryNormal,
                disabledContainerColor = MSTheme.color.primaryLight
            ),
            onClick = {
                val file = imageUri?.toFile(context, "mainImage")
                if (file != null) {
                    onAction(
                        CreateAction.CreateTimeCapsule(
                            titleTextFieldState.text.toString(),
                            contentTextFieldState.text.toString().takeIf { it.isNotEmpty() },
                            selectedDate.value,
                            file
                        )
                    )
                }
            }
        ) {
            MSText(
                text = "생성",
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
    CreateScreen(onAction = {})
}