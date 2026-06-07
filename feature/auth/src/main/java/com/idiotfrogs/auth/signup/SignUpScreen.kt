package com.idiotfrogs.auth.signup

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTextField
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DevicePreview
import com.idiotfrogs.designsystem.util.DrawType
import com.idiotfrogs.designsystem.util.keyboardAutoScroll
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.rememberKeyboardVisibility
import com.idiotfrogs.designsystem.util.rememberPickerState
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.extension.toFile
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
import com.skydoves.landscapist.glide.GlideImage
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            SignUpSideEffect.NavigateToHome -> {
                navigator.clear()
                navigator.navigate(Routes.Home)
            }
            SignUpSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    when (uiState) {
        UiState.Init -> Unit // 화면 로딩 로직
        is UiState.Success -> {
            SignUpScreen(onAction = viewModel::onAction,)
        }
        is UiState.Error -> Unit
    }
}

@Composable
fun SignUpScreen(
    onAction: (SignUpAction) -> Unit,
) {
    val context = LocalContext.current

    val textFieldState = rememberTextFieldState()
    val isShowKeyboard = rememberKeyboardVisibility()
    val scrollState = rememberScrollState()
    val (imageUri, launchImagePicker) = rememberPickerState()

    val focusManager = LocalFocusManager.current
    var isShowError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.bgNormal)
            .noRippleClickable { focusManager.clearFocus() }
            .systemBarsPadding()
            .keyboardAutoScroll(scrollState)
            .padding(
                top = 16.dp,
                bottom = if (isShowKeyboard) 0.dp else 32.dp
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .size(24.dp)
                    .noRippleClickable { onAction(SignUpAction.NavigateToBack) },
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = "Back"
            )
            MSText(
                modifier = Modifier.align(Alignment.Center),
                text = "프로필",
                fontSize = 24.dp
            )
        }
        Spacer(modifier = Modifier.height(74.dp))
        Column(
            modifier = Modifier
                .verticalScroll(
                    state = scrollState,
                    enabled = isShowKeyboard
                )
                .weight(1f),
        ) {
            imageUri?.let {
                GlideImage(
                    imageModel = { imageUri },
                    modifier = Modifier
                        .noRippleClickable { launchImagePicker() }
                        .size(128.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),

                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = MSTheme.color.greyG1,
                            shape = CircleShape
                        )
                        .align(Alignment.CenterHorizontally)
                        .noRippleClickable { launchImagePicker() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(R.drawable.ic_photo),
                        contentDescription = "photo",
                        colorFilter = ColorFilter.tint(MSTheme.color.greyG3)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                            .wavyStroke(
                                color = MSTheme.color.black,
                                cornerRadius = 20.dp,
                                fillColor = MSTheme.color.black,
                                amplitude = 1.dp,
                                spacing = 1.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "edit",
                            colorFilter = ColorFilter.tint(MSTheme.color.white)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            MSText(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 20.dp),
                text = "별명",
                fontWeight = FontWeight.Medium,
                fontSize = 12.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            MSTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textFieldState = textFieldState,
                hint = "별명을 입력해주세요.",
            )
            if (isShowError) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 20.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_warning),
                        contentDescription = "Warning",
                        tint = MSTheme.color.red
                    )
                    MSText(
                        text = "별명을 입력하면 시작할 수 있습니다.",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.dp,
                        color = MSTheme.color.red
                    )
                }
            }
            if (isShowKeyboard) {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        val horizontalPadding by animateDpAsState(
            targetValue = if (isShowKeyboard) 0.dp else 20.dp
        )

        MSButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = horizontalPadding)
                .wavyStroke(
                    color = MSTheme.color.primaryNormal,
                    drawType = if (isShowKeyboard) DrawType.TOP else DrawType.ALL,
                    clipContent = true,
                ),
            isRounded = !isShowKeyboard,
            content = {
                MSText(
                    text = "이 프로필로 할게요!",
                    fontSize = 16.dp,
                    lineHeight = 16.dp.toSp() * 1.6,
                    color = MSTheme.color.white
                )
            },
            onClick = {
                if (textFieldState.text.isEmpty()) {
                    isShowError = true
                } else {
                    val imageFile = imageUri?.toFile(context, "profileImage")
                    onAction(SignUpAction.SignUp(textFieldState.text.toString(), imageFile))
                }
            }
        )
    }
}

@DevicePreview
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(
        onAction = { }
    )
}