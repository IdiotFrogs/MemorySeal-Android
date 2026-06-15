package com.idiotfrogs.profile.editprofile

import android.util.Log
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTextField
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.rememberPickerState
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.extension.toFile
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.profile.component.EditProfileBottomSheet
import com.idiotfrogs.profile.component.ProfileHeader
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
import com.skydoves.landscapist.glide.GlideImage
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun EditProfileRoute(
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            EditProfileSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    when (uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            EditProfileScreen(onAction = viewModel::onAction)
        }
        is UiState.Error -> Unit
    }
}

@Composable
fun EditProfileScreen(
    onAction: (EditProfileAction) -> Unit
) {
    val context = LocalContext.current

    var isChanged by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val pickerState = rememberPickerState()
    var imageUri by remember(pickerState.first) { mutableStateOf(pickerState.first) }
    val launchImagePicker = pickerState.second

    val textFieldState = rememberTextFieldState()

    LaunchedEffect(textFieldState.text) {
        // TODO: 추후 기존 프로필과 비교 로직 작성
        isChanged = textFieldState.text.isNotEmpty()
    }

    if (showBottomSheet) {
        EditProfileBottomSheet(
            onDismiss = { showBottomSheet = false },
            onSelectImage = { launchImagePicker() },
            onDefaultImage = { imageUri = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        ProfileHeader(
            isChanged = isChanged,
            onBack = { onAction(EditProfileAction.NavigateToBack) },
            onSave = {
                val file = imageUri?.toFile(context, "profileImage")
                Log.d("test", file?.name.toString())
                /** TODO: 저장 로직 */
                onAction(EditProfileAction.NavigateToBack)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        imageUri?.let {
            Box {
                GlideImage(
                    imageModel = { imageUri },
                    modifier = Modifier
                        .noRippleClickable { showBottomSheet = true }
                        .size(120.dp)
                        .clip(CircleShape)
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

        } ?: run {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MSTheme.color.greyG1,
                        shape = CircleShape
                    )
                    .align(Alignment.CenterHorizontally)
                    .noRippleClickable { showBottomSheet = true },
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
        Spacer(modifier = Modifier.height(16.dp))
        MSText(
            text = "닉네임",
            fontWeight = FontWeight.Normal,
            fontSize = 12.dp,
            color = MSTheme.color.greyG5
        )
        Spacer(modifier = Modifier.height(8.dp))
        MSTextField(
            modifier = Modifier.fillMaxWidth(),
            textFieldState = textFieldState,
            hint = ""
        )
    }
}

@Preview
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(onAction = {})
}