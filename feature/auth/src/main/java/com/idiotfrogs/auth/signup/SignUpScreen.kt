package com.idiotfrogs.auth.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.idiotfrogs.designsystem.component.MSButton
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTextField
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DevicePreview
import com.idiotfrogs.designsystem.util.keyboardAutoScroll
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.rememberKeyboardVisibility
import com.idiotfrogs.designsystem.util.rememberPickerState
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun SignUpRoute(
    signUpViewModel: SignUpViewModel = viewModel(),
    navigateToBack: () -> Unit,
    navigateToErrorScreen: (String) -> Unit,
    navigateToMainScreen: () -> Unit,
) {
    val uiState by signUpViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SignUpUiState.Init -> Unit // 화면 로딩 로직
        SignUpUiState.UiLoaded -> {
            SignUpScreen(
                navigateToBack = navigateToBack,
                signUpViewModel::signUp,
            )
        }
        is SignUpUiState.Error -> navigateToErrorScreen(state.errorMessage.toString())
        SignUpUiState.SignUpSuccess -> navigateToMainScreen()
    }
}

@Composable
fun SignUpScreen(
    navigateToBack: () -> Unit,
    signUp: () -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    val isShowKeyboard = rememberKeyboardVisibility()
    val scrollState = rememberScrollState()
    val (imageUri, launchImagePicker) = rememberPickerState()

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val focusManager = LocalFocusManager.current
    var isShowError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.bgNormal)
            .noRippleClickable { focusManager.clearFocus() }
            .systemBarsPadding()
            .imePadding()
            .keyboardAutoScroll(scrollState)
            .padding(
                top = 16.dp,
                bottom = if (isShowKeyboard) 0.dp else 32.dp
            )
    ) {
        Column {
            Icon(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .noRippleClickable { navigateToBack() },
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = "Back"
            )
            Spacer(modifier = Modifier.height(24.dp))
            MSText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                text = "프로필",
                fontSize = 24.dp
            )
        }
        Spacer(modifier = Modifier.height(19.dp))
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
            } ?: Image(
                modifier = Modifier
                    .noRippleClickable { launchImagePicker() }
                    .size(128.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.img_empty_profile),
                contentDescription = "Profile"
            )
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
                isFocused = isFocused,
                interactionSource = interactionSource
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

        MSButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(
                    horizontal = if (isShowKeyboard) 0.dp else 20.dp
                ),
            isRounded = !isShowKeyboard,
            content = {
                MSText(
                    text = "이 프로필로 할게요!",
                    fontSize = 16.dp,
                    lineHeight = 16.dp.toSp() * 1.6
                )
            },
            onClick = {
                if (textFieldState.text.isEmpty()) {
                    isShowError = true
                } else {
                    signUp()
                }
            }
        )
    }
}

@DevicePreview
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(
        navigateToBack = { },
        signUp = { }
    )
}